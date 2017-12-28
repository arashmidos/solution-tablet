package com.parsroyal.solutiontablet.ui.fragment.dialogFragment;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.parsroyal.solutiontablet.R;
import com.parsroyal.solutiontablet.biz.impl.NewCustomerPicDataTransferBizImpl;
import com.parsroyal.solutiontablet.biz.impl.OrdersDataTransferBizImpl;
import com.parsroyal.solutiontablet.biz.impl.PaymentsDataTransferBizImpl;
import com.parsroyal.solutiontablet.biz.impl.QAnswersDataTransferBizImpl;
import com.parsroyal.solutiontablet.biz.impl.SaleRejectsDataTransferBizImpl;
import com.parsroyal.solutiontablet.biz.impl.VisitInformationDataTransferBizImpl;
import com.parsroyal.solutiontablet.constants.Constants;
import com.parsroyal.solutiontablet.constants.Constants.SendOrder;
import com.parsroyal.solutiontablet.constants.Constants.TransferOrder;
import com.parsroyal.solutiontablet.constants.StatusCodes;
import com.parsroyal.solutiontablet.data.entity.Payment;
import com.parsroyal.solutiontablet.data.event.DataTransferErrorEvent;
import com.parsroyal.solutiontablet.data.event.DataTransferEvent;
import com.parsroyal.solutiontablet.data.event.DataTransferSuccessEvent;
import com.parsroyal.solutiontablet.data.model.BaseSaleDocument;
import com.parsroyal.solutiontablet.data.model.DataTransferList;
import com.parsroyal.solutiontablet.data.model.QAnswerDto;
import com.parsroyal.solutiontablet.data.model.VisitInformationDto;
import com.parsroyal.solutiontablet.service.CustomerService;
import com.parsroyal.solutiontablet.service.PaymentService;
import com.parsroyal.solutiontablet.service.QuestionnaireService;
import com.parsroyal.solutiontablet.service.SaleOrderService;
import com.parsroyal.solutiontablet.service.VisitService;
import com.parsroyal.solutiontablet.service.impl.CustomerServiceImpl;
import com.parsroyal.solutiontablet.service.impl.DataTransferServiceImpl;
import com.parsroyal.solutiontablet.service.impl.PaymentServiceImpl;
import com.parsroyal.solutiontablet.service.impl.QuestionnaireServiceImpl;
import com.parsroyal.solutiontablet.service.impl.SaleOrderServiceImpl;
import com.parsroyal.solutiontablet.ui.MainActivity;
import com.parsroyal.solutiontablet.ui.adapter.DataTransferAdapter;
import com.parsroyal.solutiontablet.util.Empty;
import com.parsroyal.solutiontablet.util.ToastUtil;
import com.parsroyal.solutiontablet.util.Updater;
import java.io.File;
import java.util.List;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

/**
 * @author Arash
 */
public class DataTransferDialogFragment extends DialogFragment {

  private static final String TAG = DataTransferDialogFragment.class.getSimpleName();
  @BindView(R.id.recycler_view)
  RecyclerView recyclerView;
  @BindView(R.id.toolbar_title)
  TextView toolbarTitle;
  @BindView(R.id.data_transfer_btn)
  Button dataTransferBtn;
  @BindView(R.id.upload_data_btn_disabled)
  Button uploadDataBtnDisabled;
  @BindView(R.id.progress_bar)
  ProgressBar progressBar;
  @BindView(R.id.cancel_btn)
  TextView cancelBtn;
  @BindView(R.id.root)
  FrameLayout root;

  private MainActivity mainActivity;
  private String action;
  private VisitService visitService;
  private boolean transferStarted;
  private boolean transferFinished = false;
  private DataTransferList currentModel;
  private List<DataTransferList> model;
  private int currentPosition = -1;
  private DataTransferServiceImpl dataTransferService;
  private boolean isGet;
  private DataTransferAdapter adapter;

  public DataTransferDialogFragment() {
    // Required empty public constructor
  }

  public static DataTransferDialogFragment newInstance(Bundle arguments) {
    DataTransferDialogFragment fragment = new DataTransferDialogFragment();
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
    dataTransferService = new DataTransferServiceImpl(mainActivity);

    Bundle args = getArguments();
    if (Empty.isNotEmpty(args)) {
      String action = args.getString(Constants.DATA_TRANSFER_ACTION, "");
      if ("".equals(action)) {
        return inflater.inflate(R.layout.empty_view, container, false);
      }
      switch (action) {
        case Constants.DATA_TRANSFER_GET:
          isGet = true;
          setData();
          break;
        case Constants.DATA_TRANSFER_SEND_DATA:
          isGet = false;
          setData();
          break;
      }

      return view;
    } else {
      return inflater.inflate(R.layout.empty_view, container, false);
    }
  }

  private void invokeSendData() {
    isGet = false;
    startTransfer();
  }

  //set up recycler view
  private void setData() {

    if (isGet) {
      model = getReceiveModel();
      dataTransferBtn.setText(getString(R.string.get_data));

      dataTransferBtn
          .setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_file_download_white_18dp, 0);

      toolbarTitle.setText(getString(R.string.get_data));
    } else {
      model = getSendModel();

      dataTransferBtn.setText(getString(R.string.send_data));
      toolbarTitle.setText(getString(R.string.send_data));
    }
    adapter = new DataTransferAdapter(mainActivity, this, model);
    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
    recyclerView.setLayoutManager(linearLayoutManager);
    recyclerView.setAdapter(adapter);
  }

  private List<DataTransferList> getReceiveModel() {
    return DataTransferList.dataTransferGetList(mainActivity);
  }

  private List<DataTransferList> getSendModel() {
    return DataTransferList.dataTransferSendList(mainActivity);
  }

  @OnClick({R.id.close, R.id.data_transfer_btn, R.id.cancel_btn})
  public void onClick(View view) {
    switch (view.getId()) {
      case R.id.close:
      case R.id.cancel_btn:
        getDialog().dismiss();
        break;
      case R.id.data_transfer_btn:
        if (transferFinished) {
          getDialog().dismiss();
        } else {
          startTransfer();
        }
        break;
    }
  }

  private void startTransfer() {
    transferStarted = true;
    transferFinished = false;
    currentPosition = -1;
    switchButtonState();
    sendNextDetail();
  }

  private void switchButtonState() {
    if (transferStarted && !transferFinished) {
      dataTransferBtn.setVisibility(View.INVISIBLE);
      uploadDataBtnDisabled.setVisibility(View.VISIBLE);
      progressBar.setVisibility(View.VISIBLE);
    } else {
      dataTransferBtn.setVisibility(View.VISIBLE);
      uploadDataBtnDisabled.setVisibility(View.GONE);
      progressBar.setVisibility(View.GONE);
    }
  }

  private void sendNextDetail() {
    //Data Transfer started
    currentPosition++;
    if (currentPosition == adapter.getItemCount()) {
      finishTransfer();
    }
    adapter.setCurrent(currentPosition);

    currentModel = model.get(currentPosition);

    switch (currentModel.getId()) {
      case TransferOrder.PROVINCE:
        dataTransferService.getAllProvinces();
        break;
      case TransferOrder.CITY:
        dataTransferService.getAllCities();
        break;
      case TransferOrder.INFO:
        dataTransferService.getAllBaseInfos();
        break;
      case TransferOrder.GOODS_GROUP:
        dataTransferService.getAllGoodsGroups();
        break;
      case TransferOrder.QUESTIONNAIRE:
        dataTransferService.getAllQuestionnaires();
        break;
      case TransferOrder.GOODS:
        dataTransferService.getAllGoods();
        break;
      case TransferOrder.VISITLINE:
        dataTransferService.getAllVisitLines();
        break;
      case TransferOrder.GOODS_IMAGES:
        Updater.downloadGoodsImages(getActivity());
        break;
      case SendOrder.NEW_CUSTOMERS:
        Thread t = new Thread(() -> dataTransferService.sendAllNewCustomers());
        t.start();
        break;
      default:
    }
  }

  private void sendReject(long typeId) {
    SaleOrderServiceImpl saleOrderService = new SaleOrderServiceImpl(mainActivity);
    BaseSaleDocument saleOrder = saleOrderService.findOrderDocumentByOrderId(typeId);

    if (Empty.isNotEmpty(saleOrder)) {
      Thread dataTransfer = new Thread(() -> {

        SaleRejectsDataTransferBizImpl dataTransfer1 = new SaleRejectsDataTransferBizImpl(
            mainActivity, null);
        dataTransfer1.setOrder(saleOrder);
        dataTransfer1.exchangeData();
        if (dataTransfer1.getSuccess() == 1) {
          sendNextDetail();
        } else {
          cancelTransfer();
        }
      });
      dataTransfer.start();
    } else {
      sendNextDetail();
    }
  }

  //Sync call
  private void sendAnswers(long visitId) {
    QuestionnaireService questionnaireService = new QuestionnaireServiceImpl(mainActivity);
    List<QAnswerDto> answersForSend = questionnaireService.getAllAnswersDtoForSend(visitId);

    if (Empty.isEmpty(answersForSend)) {
      sendNextDetail();
    } else {
      QAnswersDataTransferBizImpl qAnswersDataTransferBizImpl = new QAnswersDataTransferBizImpl(
          mainActivity, null);

      Thread sendDataThead = new Thread(() -> {

        for (int i = 0; i < answersForSend.size(); i++) {
          QAnswerDto qAnswerDto = answersForSend.get(i);
          qAnswersDataTransferBizImpl.setAnswer(qAnswerDto);
          qAnswersDataTransferBizImpl.exchangeData();
        }
        if (qAnswersDataTransferBizImpl.getSuccess() == qAnswersDataTransferBizImpl.getTotal()) {
          sendNextDetail();
        } else {
          cancelTransfer();
        }
      });
      sendDataThead.start();
    }
  }

  private void sendPayments(long visitId) {
    PaymentService paymentService = new PaymentServiceImpl(mainActivity);
    List<Payment> payments = paymentService.getAllPaymentsByVisitId(visitId);
    Thread sendDataThead = new Thread(() -> {

      if (Empty.isNotEmpty(payments)) {
        PaymentsDataTransferBizImpl paymentsDataTransferBiz = new PaymentsDataTransferBizImpl(
            mainActivity, null);
        paymentsDataTransferBiz.setPayments(payments);
        paymentsDataTransferBiz.exchangeData();
      } else {
        sendNextDetail();
      }
    });
    sendDataThead.start();
  }

  private void sendPicture(long visitId) {
    CustomerService customerService = new CustomerServiceImpl(mainActivity);
    File pics = customerService.getAllCustomerPicForSendByVisitId(visitId);
    if (Empty.isEmpty(pics)) {
      sendNextDetail();
    }
    Thread sendDataThead = new Thread(
        () -> new NewCustomerPicDataTransferBizImpl(mainActivity, null, pics, visitId)
            .exchangeData());
    sendDataThead.start();
  }

  //Async call
  private void sendOrder(Long orderId) {
    SaleOrderService saleOrderService = new SaleOrderServiceImpl(mainActivity);
    BaseSaleDocument saleOrder = saleOrderService.findOrderDocumentByOrderId(orderId);
    if (Empty.isNotEmpty(saleOrder)) {
      OrdersDataTransferBizImpl dataTransfer = new OrdersDataTransferBizImpl(mainActivity, null);
      dataTransfer.sendSingleOrder(saleOrder);
    } else {
      //We have sent them before
      sendNextDetail();
    }
  }

  @Subscribe
  public void getMessage(DataTransferEvent event) {
    if (event instanceof DataTransferSuccessEvent) {
      if (event.getStatusCode().equals(StatusCodes.SUCCESS)) {
        adapter.setFinished(currentPosition);
        sendNextDetail();
      } else {
        if (event.getStatusCode().equals(StatusCodes.NO_DATA_ERROR)) {
          adapter.setFinished(currentPosition, getString(StatusCodes.NO_DATA_ERROR.getMessage()));
        }
      }
    } else if (event instanceof DataTransferErrorEvent) {
      if (isGet) {
        adapter.setError(currentPosition);
        cancelTransfer();
      } else {
        adapter.setError(currentPosition);
        sendNextDetail();
      }
    }
  }

  private void sendVisit() {

    List<VisitInformationDto> visitInformationList = visitService.getAllVisitDetailForSend(0L);
    if (Empty.isEmpty(visitInformationList)) {
      cancelTransfer();
      return;
    }
    VisitInformationDataTransferBizImpl dataTransfer = new VisitInformationDataTransferBizImpl(
        mainActivity, null);
    Thread sendDataThead = new Thread(() -> {

      for (int i = 0; i < visitInformationList.size(); i++) {
        VisitInformationDto visitInformationDto = visitInformationList.get(i);
        if (visitInformationDto.getDetails() == null
            || visitInformationDto.getDetails().size() == 0) {
          visitService.deleteVisitById(visitInformationDto.getId());
          getActivity().runOnUiThread(this::cancelTransfer);
          return;
        }
        dataTransfer.setData(visitInformationDto);
        dataTransfer.exchangeData();
      }
      getActivity().runOnUiThread(this::finishTransfer);
    });
    sendDataThead.start();
  }

  private void finishTransfer() {
    mainActivity.runOnUiThread(() -> {

      ToastUtil.toastMessage(root, isGet ? getString(R.string.get_data_completed_successfully)
          : getString(R.string.send_data_completed_successfully));
      transferFinished = true;
      cancelBtn.setVisibility(View.GONE);
      switchButtonState();
      dataTransferBtn.setText(R.string.finish);

      dataTransferBtn
          .setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_check_white_18_dp, 0);
    });
  }

  private void cancelTransfer() {
    transferFinished = true;
    mainActivity.runOnUiThread(() -> {
      switchButtonState();
//      adapter.setError(currentPosition);
      ToastUtil.toastError(root, getString(R.string.error_in_sending_data));
    });
  }


  @Override
  public void onResume() {
    super.onResume();
    EventBus.getDefault().register(this);
  }

  @Override
  public void onPause() {
    super.onPause();
    EventBus.getDefault().unregister(this);
  }

  @Override
  public void onDestroyView() {
    if (getDialog() != null && getRetainInstance()) {
      getDialog().setDismissMessage(null);
    }
    super.onDestroyView();
  }
}
