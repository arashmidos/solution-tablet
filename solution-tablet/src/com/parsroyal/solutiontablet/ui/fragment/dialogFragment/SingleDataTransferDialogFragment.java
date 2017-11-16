package com.parsroyal.solutiontablet.ui.fragment.dialogFragment;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.parsroyal.solutiontablet.R;
import com.parsroyal.solutiontablet.biz.impl.NewCustomerPicDataTransferBizImpl;
import com.parsroyal.solutiontablet.biz.impl.OrdersDataTransferBizImpl;
import com.parsroyal.solutiontablet.constants.Constants;
import com.parsroyal.solutiontablet.constants.StatusCodes;
import com.parsroyal.solutiontablet.constants.VisitInformationDetailType;
import com.parsroyal.solutiontablet.data.entity.VisitInformationDetail;
import com.parsroyal.solutiontablet.data.event.ErrorEvent;
import com.parsroyal.solutiontablet.data.event.Event;
import com.parsroyal.solutiontablet.data.event.SendOrderEvent;
import com.parsroyal.solutiontablet.data.model.BaseSaleDocument;
import com.parsroyal.solutiontablet.data.searchobject.VisitInformationDetailSO;
import com.parsroyal.solutiontablet.service.CustomerService;
import com.parsroyal.solutiontablet.service.SaleOrderService;
import com.parsroyal.solutiontablet.service.VisitService;
import com.parsroyal.solutiontablet.service.impl.CustomerServiceImpl;
import com.parsroyal.solutiontablet.service.impl.SaleOrderServiceImpl;
import com.parsroyal.solutiontablet.service.impl.VisitServiceImpl;
import com.parsroyal.solutiontablet.ui.MainActivity;
import com.parsroyal.solutiontablet.ui.adapter.DataTransferAdapter;
import com.parsroyal.solutiontablet.util.Empty;
import com.parsroyal.solutiontablet.util.ToastUtil;
import java.io.File;
import java.util.List;
import org.greenrobot.eventbus.Subscribe;

/**
 * @author Arash
 */
public class SingleDataTransferDialogFragment extends DialogFragment {

  @BindView(R.id.recycler_view)
  RecyclerView recyclerView;
  @BindView(R.id.toolbar_title)
  TextView toolbarTitle;
  @BindView(R.id.upload_data_btn)
  Button uploadDataBtn;
  @BindView(R.id.cancel_btn)
  TextView cancelBtn;

  private MainActivity mainActivity;
  private long orderId;
  private long visitId;
  private DataTransferAdapter adapter;
  private VisitService visitService;
  private boolean transferStarted;
  private boolean transferFinished = false;
  private VisitInformationDetail currentModel;
  private List<VisitInformationDetail> model;

  public SingleDataTransferDialogFragment() {
    // Required empty public constructor
  }

  public static SingleDataTransferDialogFragment newInstance(Bundle arguments) {
    SingleDataTransferDialogFragment fragment = new SingleDataTransferDialogFragment();
    fragment.setArguments(arguments);
    return fragment;
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setStyle(DialogFragment.STYLE_NORMAL, R.style.myDialog);
    setRetainInstance(true);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    View view = inflater.inflate(R.layout.fragment_single_data_transfer_dialog, container, false);
    ButterKnife.bind(this, view);
    mainActivity = (MainActivity) getActivity();

    Bundle args = getArguments();
    if (Empty.isNotEmpty(args)) {
      visitId = args.getLong(Constants.VISIT_ID, -1);
      if (visitId == -1) {
        return inflater.inflate(R.layout.empty_view, container, false);
      }

      visitService = new VisitServiceImpl(mainActivity);

      setUpRecyclerView();
      return view;
    } else {
      return inflater.inflate(R.layout.empty_view, container, false);
    }
  }

  //set up recycler view
  private void setUpRecyclerView() {
    model = getModel();
    adapter = new DataTransferAdapter(mainActivity, this, getSimpleModel());
    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
    recyclerView.setLayoutManager(linearLayoutManager);
    recyclerView.setAdapter(adapter);
  }

  private List<VisitInformationDetail> getSimpleModel() {
    VisitInformationDetailSO visitInformationDetailSO = new VisitInformationDetailSO(visitId,
        VisitInformationDetail.COL_TYPE);
    return visitService.searchVisitDetail(visitInformationDetailSO);
  }

  private List<VisitInformationDetail> getModel() {
    VisitInformationDetailSO visitInformationDetailSO = new VisitInformationDetailSO(visitId);
    return visitService.searchVisitDetail(visitInformationDetailSO);
  }

  @OnClick({R.id.close, R.id.upload_data_btn, R.id.cancel_btn})
  public void onClick(View view) {
    switch (view.getId()) {
      case R.id.close:
      case R.id.cancel_btn:
        getDialog().dismiss();
        break;
      case R.id.upload_data_btn:
        startTransfer();

        break;
    }
  }

  private void startTransfer() {
    transferStarted = true;
    Toast.makeText(mainActivity, "Uploading data...", Toast.LENGTH_SHORT).show();
    uploadDataBtn.setEnabled(false);
    //TODO SHAKIB Loading on button
    for (int i = 0; i < model.size() && !transferFinished; i++) {

      //Uploading started
      VisitInformationDetail visitInformationDetail = model.get(i);
      adapter.setCurrent(visitInformationDetail.getType());
      currentModel = visitInformationDetail;
      switch (VisitInformationDetailType.getByValue(visitInformationDetail.getType())) {
        case CREATE_ORDER:
        case CREATE_REJECT:
        case CREATE_INVOICE:
          sendOrder(visitInformationDetail.getTypeId());
          break;
        case TAKE_PICTURE:
          sendPicture(visitInformationDetail.getVisitInformationId());
          break;
        case FILL_QUESTIONNAIRE:

          break;
        case SAVE_LOCATION:
          break;
        case CASH:
          break;
        case NO_ORDER:
          break;
      }
    }
    transferFinished = true;

    //send VisitInformationDetail
  }

  private void sendPicture(long visitId) {
    CustomerService customerService = new CustomerServiceImpl(mainActivity);
    File pics = customerService.getAllCustomerPicForSendByVisitId(visitId);

    new NewCustomerPicDataTransferBizImpl(mainActivity, null, pics, visitId).exchangeData();
  }

  private void sendOrder(Long orderId) {
    SaleOrderService saleOrderService = new SaleOrderServiceImpl(mainActivity);
    BaseSaleDocument saleOrder = saleOrderService.findOrderDocumentByOrderId(orderId);
    if (Empty.isNotEmpty(saleOrder)) {
      OrdersDataTransferBizImpl dataTransfer = new OrdersDataTransferBizImpl(mainActivity, null);
      dataTransfer.sendSingleOrder(saleOrder);
    } else {
      //Set icon to error
    }
  }

  @Subscribe
  public void getMessage(Event event) {
    if (event instanceof SendOrderEvent) {

      if (event.getStatusCode().equals(StatusCodes.SUCCESS)) {
        adapter.setFinished(currentModel.getType());
      } else {
        adapter.setError(currentModel.getType());
        transferFinished = true;
      }
    } else if (event instanceof ErrorEvent) {
      ToastUtil.toastError(getActivity(), event.getStatusCode().toString());
      transferFinished = true;
    }
  }

  @Override
  public void onDestroyView() {
    //workaround for this issue: https://code.google.com/p/android/issues/detail?id=17423 (unable to retain instance after configuration change)
    if (getDialog() != null && getRetainInstance()) {
      getDialog().setDismissMessage(null);
    }
    super.onDestroyView();
  }
}
