package com.parsroyal.solutiontablet.ui.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.crashlytics.android.Crashlytics;
import com.parsroyal.solutiontablet.R;
import com.parsroyal.solutiontablet.SolutionTabletApplication;
import com.parsroyal.solutiontablet.constants.Constants;
import com.parsroyal.solutiontablet.constants.SaleOrderStatus;
import com.parsroyal.solutiontablet.constants.StatusCodes;
import com.parsroyal.solutiontablet.data.entity.Customer;
import com.parsroyal.solutiontablet.data.entity.Position;
import com.parsroyal.solutiontablet.data.entity.VisitInformationDetail;
import com.parsroyal.solutiontablet.data.event.ActionEvent;
import com.parsroyal.solutiontablet.data.event.ErrorEvent;
import com.parsroyal.solutiontablet.data.model.SaleOrderDto;
import com.parsroyal.solutiontablet.exception.UnknownSystemException;
import com.parsroyal.solutiontablet.service.SettingService;
import com.parsroyal.solutiontablet.service.impl.CustomerServiceImpl;
import com.parsroyal.solutiontablet.service.impl.SaleOrderServiceImpl;
import com.parsroyal.solutiontablet.service.impl.SettingServiceImpl;
import com.parsroyal.solutiontablet.service.impl.VisitServiceImpl;
import com.parsroyal.solutiontablet.ui.activity.MainActivity;
import com.parsroyal.solutiontablet.ui.fragment.dialogFragment.SingleDataTransferDialogFragment;
import com.parsroyal.solutiontablet.util.DialogUtil;
import com.parsroyal.solutiontablet.util.Empty;
import com.parsroyal.solutiontablet.util.Logger;
import com.parsroyal.solutiontablet.util.PreferenceHelper;
import com.parsroyal.solutiontablet.util.ToastUtil;
import com.parsroyal.solutiontablet.util.constants.ApplicationKeys;
import java.util.List;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import timber.log.Timber;

public class PhoneVisitDetailFragment extends BaseFragment {

  private static final String TAG = PhoneVisitDetailFragment.class.getName();
  @BindView(R.id.msg)
  TextView msg;
  @BindView(R.id.sub_msg)
  TextView subMsg;

  private CustomerServiceImpl customerService;
  private MainActivity mainActivity;
  private SaleOrderServiceImpl saleOrderService;
  private long customerId;
  private long visitId;
  private VisitServiceImpl visitService;
  private Customer customer;
  private SaleOrderDto orderDto;
  private long visitLineBackendId;
  private SettingService settingService;

  public PhoneVisitDetailFragment() {
    // Required empty public constructor
  }

  public static PhoneVisitDetailFragment newInstance() {
    return new PhoneVisitDetailFragment();
  }

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }

  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {

    // Inflate the layout for this fragment
    View view = inflater.inflate(R.layout.empty_view, container, false);
    ButterKnife.bind(this, view);
    Bundle args = getArguments();
    if (Empty.isNotEmpty(args)) {
      customerId = args.getLong(Constants.CUSTOMER_ID);
      mainActivity = (MainActivity) getActivity();
      customerService = new CustomerServiceImpl(mainActivity);
      settingService = new SettingServiceImpl();
      visitService = new VisitServiceImpl(mainActivity);
      customer = customerService.getCustomerById(customerId);
      if (customer == null) {
        return inflater.inflate(R.layout.empty_view, container, false);
      }
      saleOrderService = new SaleOrderServiceImpl(mainActivity);

      visitId = args.getLong(Constants.ORIGIN_VISIT_ID);
      visitLineBackendId = args.getLong(Constants.VISITLINE_BACKEND_ID);

      msg.setText("");
      subMsg.setText("");
      return view;
    } else {
      return inflater.inflate(R.layout.empty_view, container, false);
    }
  }

  public void finishVisiting() {
    try {
      List<VisitInformationDetail> detailList = visitService.getAllVisitDetailById(visitId);
      if (detailList.size() == 0) {

        visitService.deleteVisitById(visitId);
        Customer customer = customerService.getCustomerById(customerId);
        saleOrderService.deleteForAllCustomerOrdersByStatus(customer.getBackendId(),
            SaleOrderStatus.DRAFT.getId());
        mainActivity.removeFragment(this);
      } else {
        tryFindingLocation();
        saveVisit();
      }
    } catch (Exception ex) {
      Crashlytics
          .log(Log.ERROR, "General Exception", "Error in finishing visit " + ex.getMessage());
      Log.e(TAG, ex.getMessage(), ex);
      ToastUtil.toastError(mainActivity, new UnknownSystemException(ex));
    }
  }

  private void doFinishVisiting() {
    try {
      visitService.finishVisiting(visitId, 0L);
      Customer customer = customerService.getCustomerById(customerId);
      saleOrderService.deleteForAllCustomerOrdersByStatus(customer.getBackendId(),
          SaleOrderStatus.DRAFT.getId());
      mainActivity.removeFragment(this);
    } catch (Exception ex) {

      Log.e(TAG, ex.getMessage(), ex);
      ToastUtil.toastError(mainActivity, new UnknownSystemException(ex));
    }
  }

  private void tryFindingLocation() {
    Position position = SolutionTabletApplication.getInstance().getLastSavedPosition();

    if (Empty.isNotEmpty(position)) {
      visitService.updateVisitLocation(visitId, position);
    }
  }

  private void saveVisit() {
    DialogUtil.showCustomDialog(mainActivity, mainActivity.getString(R.string.send_data),
        getString(R.string.message_confirm_send_visit_data_instantly), "نمایش جزئیات ارسال",
        (dialog, which) -> {
          visitService.finishVisiting(visitId, 0L);
          FragmentTransaction ft = mainActivity.getSupportFragmentManager().beginTransaction();
          Bundle args = new Bundle();
          args.putLong(Constants.VISIT_ID, visitId);
          SingleDataTransferDialogFragment singleDataTransferDialogFragment = SingleDataTransferDialogFragment
              .newInstance(args);
          singleDataTransferDialogFragment.show(ft, "data_transfer");
          dialog.dismiss();
        }, mainActivity.getString(R.string.cancel_btn), (dialog, which) -> doFinishVisiting(),
        Constants.ICON_MESSAGE);
  }

  public Customer getCustomer() {
    return customer;
  }

  public long getCustomerId() {
    return customerId;
  }

  public long getCustomerBackendId() {
    return customer.getBackendId();
  }

  public long getVisitId() {
    return visitId;
  }

  @Override
  public int getFragmentId() {
    return MainActivity.PHONE_VISIT_DETAIL_FRAGMENT_ID;
  }


  /**
   * @param statusID Could be DRAFT for both AddInvoice/AddOrder or REJECTED_DRAFT for
   * ReturnedOrder
   */
  public void openOrderDetailFragment(Long statusID, boolean isCashOrder) {

    orderDto = saleOrderService
        .findOrderDtoByCustomerBackendIdAndStatus(customer.getBackendId(), statusID);
    if (Empty.isEmpty(orderDto) || statusID.equals(SaleOrderStatus.REJECTED_DRAFT.getId())) {
      orderDto = createDraftOrder(customer, statusID);
    }

    if (Empty.isNotEmpty(orderDto) && Empty.isNotEmpty(orderDto.getId())) {

      Bundle args = new Bundle();
      args.putLong(Constants.ORDER_ID, orderDto.getId());
      args.putLong(Constants.VISIT_ID, visitId);
      args.putBoolean(Constants.READ_ONLY, false);
      args.putString(Constants.PAGE_STATUS, Constants.NEW);
      args.putBoolean(Constants.CASH_ORDER, isCashOrder);
      mainActivity.changeFragment(MainActivity.GOODS_LIST_FRAGMENT_ID, args, true);

    } else {
      if (statusID.equals(SaleOrderStatus.REJECTED_DRAFT.getId())) {
        ToastUtil.toastError(mainActivity, R.string.message_cannot_create_rejected_right_now);
      } else if (statusID.equals(SaleOrderStatus.DRAFT.getId()) && PreferenceHelper.isVisitor()) {
        ToastUtil.toastError(mainActivity, R.string.message_cannot_create_order_right_now);
      } else {
        ToastUtil.toastError(mainActivity, R.string.message_cannot_create_factor_right_now);
      }
    }
  }

  private SaleOrderDto createDraftOrder(Customer customer, Long statusID) {
    try {
      SaleOrderDto orderDto = new SaleOrderDto(statusID, customer);
      Long id = saleOrderService.saveOrder(orderDto);
      orderDto.setId(id);
      return orderDto;
    } catch (Exception e) {
      Logger.sendError("Data Storage Exception", "Error in creating draft order " + e.getMessage());
      Timber.e(e);
      ToastUtil.toastError(mainActivity, new UnknownSystemException(e));
    }
    return null;
  }

  @Override
  public void onResume() {
    super.onResume();
    EventBus.getDefault().register(this);
    mainActivity.showNav();
    if (mainActivity.isPhoneVisit()) {
      mainActivity.setPhoneVisit(false);
      checkAddOrder();
    } else {
      finishVisiting();
    }
  }

  private void checkAddOrder() {
    String checCredit = settingService.getSettingValue(ApplicationKeys.SETTING_CHECK_CREDIT_ENABLE);
    boolean checkCreditEnabled = Empty.isEmpty(checCredit) || "null".equals(checCredit) ? false
        : Boolean.valueOf(checCredit);
    if (checkCreditEnabled && customer.getRemainedCredit() != null
        && customer.getRemainedCredit().longValue() <= 0) {
      DialogUtil.showCustomDialog(mainActivity, "هشدار",
          "ثبت سفارش فقط با پرداخت نقدی امکان پذیر است", "ثبت سفارش",
          (dialogInterface, i) -> openOrderDetailFragment(SaleOrderStatus.DRAFT.getId(), true),
          "انصراف", (dialog, which) -> doFinishVisiting(), Constants.ICON_WARNING);
    } else {
      openOrderDetailFragment(SaleOrderStatus.DRAFT.getId(), false);
    }
  }

  @Override
  public void onPause() {
    super.onPause();
    EventBus.getDefault().unregister(this);
  }

  @Subscribe
  public void getMessage(ActionEvent event) {
    if (event.getStatusCode() == StatusCodes.SUCCESS) {
      doFinishVisiting();
    }
  }

  @Subscribe
  public void getMessage(ErrorEvent errorEvent) {
    if (!errorEvent.getMessage().equals("reject")) {
      DialogUtil.dismissProgressDialog();
      ToastUtil.toastError(mainActivity, getString(R.string.error_unknown_system_exception));
    }
  }
}
