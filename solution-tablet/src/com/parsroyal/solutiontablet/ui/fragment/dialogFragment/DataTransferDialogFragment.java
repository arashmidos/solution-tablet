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
import android.widget.Toast;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.parsroyal.solutiontablet.R;
import com.parsroyal.solutiontablet.biz.impl.GoodsRequestDataTransferBizImpl;
import com.parsroyal.solutiontablet.constants.Constants;
import com.parsroyal.solutiontablet.constants.Constants.TransferGetDistributorOrder;
import com.parsroyal.solutiontablet.constants.Constants.TransferGetOrder;
import com.parsroyal.solutiontablet.constants.Constants.TransferSendOrder;
import com.parsroyal.solutiontablet.constants.StatusCodes;
import com.parsroyal.solutiontablet.data.event.ActionEvent;
import com.parsroyal.solutiontablet.data.event.DataTransferErrorEvent;
import com.parsroyal.solutiontablet.data.event.DataTransferEvent;
import com.parsroyal.solutiontablet.data.event.DataTransferSuccessEvent;
import com.parsroyal.solutiontablet.data.model.DataTransferList;
import com.parsroyal.solutiontablet.service.VisitService;
import com.parsroyal.solutiontablet.service.impl.DataTransferServiceImpl;
import com.parsroyal.solutiontablet.service.impl.SettingServiceImpl;
import com.parsroyal.solutiontablet.ui.MainActivity;
import com.parsroyal.solutiontablet.ui.adapter.DataTransferAdapter;
import com.parsroyal.solutiontablet.util.Empty;
import com.parsroyal.solutiontablet.util.ToastUtil;
import com.parsroyal.solutiontablet.util.Updater;
import com.parsroyal.solutiontablet.util.constants.ApplicationKeys;
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
  private boolean canceled;
  private SettingServiceImpl settingService;

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
    settingService = new SettingServiceImpl(mainActivity);

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

  //set up recycler view
  private void setData() {

    if (isGet) {
      String saleType = settingService.getSettingValue(ApplicationKeys.SETTING_SALE_TYPE);
      if (saleType.equals(ApplicationKeys.SALE_DISTRIBUTER)) {

        model = getDistributorReceiveModel();
      } else {

        model = getReceiveModel();
      }
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

  private List<DataTransferList> getDistributorReceiveModel() {
    return DataTransferList.dataTransferDistributorGetList(mainActivity);
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
          EventBus.getDefault().post(new ActionEvent(StatusCodes.ACTION_REFRESH_DATA));
          getDialog().dismiss();
        } else {
          if (isGet) {
            dataTransferService.clearData(Constants.FULL_UPDATE);
          }
          startTransfer();
        }
        break;
    }
  }

  private void startTransfer() {
    transferStarted = true;
    transferFinished = false;
    canceled = false;
    currentPosition = -1;
    switchButtonState();
    sendNextDetail();
  }

  private void switchButtonState() {
    if (transferStarted && !transferFinished) {
      dataTransferBtn.setVisibility(View.INVISIBLE);
      progressBar.setVisibility(View.VISIBLE);
      uploadDataBtnDisabled.setVisibility(View.VISIBLE);
    } else {
//      if (canceled) {
//      }else {
      dataTransferBtn.setVisibility(View.VISIBLE);
      uploadDataBtnDisabled.setVisibility(View.GONE);
      progressBar.setVisibility(View.GONE);
      dataTransferBtn.setText(R.string.finish);

      dataTransferBtn
          .setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_check_white_18_dp, 0);
//      }
    }
  }

  private void sendNextDetail() {
    //Data Transfer started
    currentPosition++;
    if (currentPosition == adapter.getItemCount()) {
      finishTransfer();
      return;
    }
    adapter.setCurrent(currentPosition);

    currentModel = model.get(currentPosition);

    switch (currentModel.getId()) {
      case TransferGetOrder.PROVINCE:
        dataTransferService.getAllProvinces();
        break;
      case TransferGetOrder.CITY:
        dataTransferService.getAllCities();
        break;
      case TransferGetOrder.INFO:
        dataTransferService.getAllBaseInfos();
        break;
      case TransferGetOrder.GOODS_GROUP:
        dataTransferService.getAllGoodsGroups();
        break;
      case TransferGetOrder.QUESTIONNAIRE:
        dataTransferService.getAllQuestionnaires();
        break;
      case TransferGetOrder.GOODS:
        dataTransferService.getAllGoods();
        break;
      case TransferGetOrder.VISITLINE:
        dataTransferService.getAllVisitLines();
        break;
      case TransferGetOrder.GOODS_IMAGES:
        Updater.downloadGoodsImages(getActivity());
        break;
      case TransferSendOrder.NEW_CUSTOMERS:
        Thread t = new Thread(() -> dataTransferService.sendAllNewCustomers());
        t.start();
        break;
      case TransferSendOrder.ADDRESS:
        Thread t2 = new Thread(() -> dataTransferService.sendAllUpdatedCustomers());
        t2.start();
        break;
      case TransferSendOrder.POSITION:
        Thread t3 = new Thread(() -> dataTransferService.sendAllPositions());
        t3.start();
        break;
      case TransferSendOrder.QUESTIONNAIRE:
        Thread t4 = new Thread(() -> dataTransferService.sendAllAnswers());
        t4.start();
        break;
      case TransferSendOrder.PAYMENT:
        Thread t5 = new Thread(() -> dataTransferService.sendAllPayments());
        t5.start();
        break;
      case TransferSendOrder.ORDER:
        Thread t6 = new Thread(() -> dataTransferService.sendAllOrders());
        t6.start();
        break;
      case TransferSendOrder.RETURN_ORDER:
        Thread t7 = new Thread(() -> dataTransferService.sendAllSaleRejects());
        t7.start();
        break;
      case TransferSendOrder.VISIT_DETAIL:
        Thread t8 = new Thread(() -> dataTransferService.sendAllVisitInformation());
        t8.start();
        break;
      case TransferSendOrder.CUSTOMER_PICS:
        Thread t9 = new Thread(() -> dataTransferService.sendAllCustomerPics());
        t9.start();
        break;
      case TransferGetDistributorOrder.GOODS_FOR_DELIVERY:
        Thread t10 = new Thread(() -> dataTransferService.getAllDeliverableGoods());
        t10.start();
        break;
      case TransferGetDistributorOrder.VISITLINES_FOR_DELIVERY:
        Thread t11 = new Thread(() -> dataTransferService.getAllVisitLinesForDelivery());
        t11.start();
        break;
      case TransferGetDistributorOrder.ORDERS_FOR_DELIVERY:
        Thread t12 = new Thread(() -> dataTransferService.getAllOrdersForDelivery());
        t12.start();
        break;
      case TransferGetDistributorOrder.GOODS_REQUEST:
        Thread t13 = new Thread(() -> dataTransferService.getGoodRequest());
        t13.start();
      default:

    }
  }

  @Subscribe
  public void getMessage(DataTransferEvent event) {
    if (event instanceof DataTransferSuccessEvent) {
      if (event.getStatusCode().equals(StatusCodes.SUCCESS)) {
        adapter.setUpdate(currentPosition, event.getMessage());
        adapter.setFinished(currentPosition);
        sendNextDetail();
      } else if (event.getStatusCode().equals(StatusCodes.NO_DATA_ERROR)) {
        adapter.setFinished(currentPosition, getString(StatusCodes.NO_DATA_ERROR.getMessage()));
        sendNextDetail();
      } else if (event.getStatusCode().equals(StatusCodes.UPDATE)) {
        adapter.setUpdate(currentPosition, event.getMessage());
      }
    } else if (event instanceof DataTransferErrorEvent) {
      if (isGet) {
        adapter.setError(currentPosition);
        cancelTransfer();
      } else {
        adapter.setError(currentPosition, event.getMessage());
        sendNextDetail();
      }
    }
  }

  private void finishTransfer() {
    mainActivity.runOnUiThread(() -> {

      ToastUtil.toastMessage(root, isGet ? getString(R.string.get_data_completed_successfully)
          : getString(R.string.send_data_completed_successfully));
      transferFinished = true;
      cancelBtn.setVisibility(View.GONE);
      switchButtonState();
    });
  }

  private void cancelTransfer() {
    transferFinished = true;
    canceled = true;
    mainActivity.runOnUiThread(() -> {
      switchButtonState();
      ToastUtil.toastError(root, getString(R.string.error_in_exchanging_data));
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
