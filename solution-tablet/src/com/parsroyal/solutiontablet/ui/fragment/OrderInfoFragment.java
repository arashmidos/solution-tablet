package com.parsroyal.solutiontablet.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.parsroyal.solutiontablet.R;
import com.parsroyal.solutiontablet.biz.impl.GiftDataTransferBizImpl;
import com.parsroyal.solutiontablet.biz.impl.OrdersDataTransferBizImpl;
import com.parsroyal.solutiontablet.constants.BaseInfoTypes;
import com.parsroyal.solutiontablet.constants.Constants;
import com.parsroyal.solutiontablet.constants.SaleOrderStatus;
import com.parsroyal.solutiontablet.constants.StatusCodes;
import com.parsroyal.solutiontablet.constants.VisitInformationDetailType;
import com.parsroyal.solutiontablet.data.entity.Customer;
import com.parsroyal.solutiontablet.data.entity.VisitInformationDetail;
import com.parsroyal.solutiontablet.data.event.DataTransferSuccessEvent;
import com.parsroyal.solutiontablet.data.event.ErrorEvent;
import com.parsroyal.solutiontablet.data.event.Event;
import com.parsroyal.solutiontablet.data.event.SendOrderEvent;
import com.parsroyal.solutiontablet.data.model.BaseSaleDocument;
import com.parsroyal.solutiontablet.data.model.LabelValue;
import com.parsroyal.solutiontablet.data.model.SaleOrderDto;
import com.parsroyal.solutiontablet.exception.BusinessException;
import com.parsroyal.solutiontablet.exception.UnknownSystemException;
import com.parsroyal.solutiontablet.service.BaseInfoService;
import com.parsroyal.solutiontablet.service.CustomerService;
import com.parsroyal.solutiontablet.service.SaleOrderService;
import com.parsroyal.solutiontablet.service.SettingService;
import com.parsroyal.solutiontablet.service.VisitService;
import com.parsroyal.solutiontablet.service.impl.BaseInfoServiceImpl;
import com.parsroyal.solutiontablet.service.impl.CustomerServiceImpl;
import com.parsroyal.solutiontablet.service.impl.SaleOrderServiceImpl;
import com.parsroyal.solutiontablet.service.impl.SettingServiceImpl;
import com.parsroyal.solutiontablet.service.impl.VisitServiceImpl;
import com.parsroyal.solutiontablet.ui.MainActivity;
import com.parsroyal.solutiontablet.ui.adapter.PaymentMethodAdapter;
import com.parsroyal.solutiontablet.ui.fragment.dialogFragment.PaymentMethodDialogFragment;
import com.parsroyal.solutiontablet.util.DialogUtil;
import com.parsroyal.solutiontablet.util.Empty;
import com.parsroyal.solutiontablet.util.Logger;
import com.parsroyal.solutiontablet.util.MultiScreenUtility;
import com.parsroyal.solutiontablet.util.NumberUtil;
import com.parsroyal.solutiontablet.util.ToastUtil;
import com.parsroyal.solutiontablet.util.constants.ApplicationKeys;
import java.util.List;
import java.util.Locale;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

/**
 * @author Shakib
 */
public class OrderInfoFragment extends BaseFragment {

  private static final String TAG = OrderInfoFragment.class.getName();
  @BindView(R.id.customer_name_tv)
  TextView customerNameTv;
  @BindView(R.id.cost_tv)
  TextView costTv;
  @BindView(R.id.date_tv)
  TextView dateTv;
  @BindView(R.id.payment_method_tv)
  TextView paymentMethodTv;
  @BindView(R.id.order_code_tv)
  TextView orderCodeTv;
  @BindView(R.id.date_title_tv)
  TextView dateTitleTv;
  @BindView(R.id.order_code_title_tv)
  TextView orderCodeTitleTv;
  @BindView(R.id.submit_order_btn)
  Button submitOrderBtn;
  @BindView(R.id.payment_type_tv)
  TextView paymentTypeTv;
  @Nullable
  @BindView(R.id.recycler_view)
  RecyclerView recyclerView;
  @BindView(R.id.amount_tv)
  TextView amountTv;
  @Nullable
  @BindView(R.id.payment_type_title)
  TextView paymentTypeTitle;
  @BindView(R.id.payment_type_layout)
  RelativeLayout paymentTypeLayout;
  @BindView(R.id.description_edt)
  EditText descriptionEdt;
  @BindView(R.id.description_lay)
  TextInputLayout descriptionLayout;
  @BindView(R.id.register_gift_tv)
  TextView registerGiftTv;
  @BindView(R.id.order_gift_layout)
  LinearLayout orderGiftLayout;

  private LabelValue selectedItem = null;
  private MainActivity mainActivity;
  private long orderId;
  private SaleOrderService saleOrderService;
  private SaleOrderDto order;
  private Long orderStatus;
  private String saleType;
  private Long visitId;
  private CustomerService customerService;
  private Customer customer;
  private BaseInfoService baseInfoService;
  private SettingService settingService;
  private VisitService visitService;
  private String pageStatus;
  private PaymentMethodAdapter adapter;
  private boolean giftRequestSent;
  private Long orderBackendId;

  public OrderInfoFragment() {
    // Required empty public constructor
  }

  public static OrderInfoFragment newInstance() {
    return new OrderInfoFragment();
  }

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setRetainInstance(true);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    View view = inflater.inflate(R.layout.fragment_new_order_info, container, false);
    ButterKnife.bind(this, view);
    mainActivity = (MainActivity) getActivity();
    saleOrderService = new SaleOrderServiceImpl(mainActivity);
    customerService = new CustomerServiceImpl(mainActivity);
    baseInfoService = new BaseInfoServiceImpl(mainActivity);
    settingService = new SettingServiceImpl(mainActivity);
    visitService = new VisitServiceImpl(mainActivity);

    Bundle args = getArguments();

    if (Empty.isNotEmpty(args)) {
      orderId = args.getLong(Constants.ORDER_ID, -1);
      pageStatus = args.getString(Constants.PAGE_STATUS);
      order = saleOrderService.findOrderDtoById(orderId);
      customer = customerService.getCustomerByBackendId(order.getCustomerBackendId());
      orderStatus = order.getStatus();
      saleType = args.getString(Constants.SALE_TYPE, "");
      visitId = args.getLong(Constants.VISIT_ID, -1);

      setData();

      return view;
    } else {
      return inflater.inflate(R.layout.empty_view, container, false);
    }
  }

  private void setData() {
    if (selectedItem != null) {
      paymentMethodTv.setText(NumberUtil.digitsToPersian(selectedItem.getLabel()));
    } else {
      Long paymentTypeBackendId = order.getPaymentTypeBackendId();
      if (Empty.isNotEmpty(paymentTypeBackendId) && paymentTypeBackendId != 0L) {
        selectedItem = baseInfoService
            .getBaseInfoByBackendId(BaseInfoTypes.PAYMENT_TYPE.getId(), paymentTypeBackendId);
        paymentMethodTv.setText(NumberUtil.digitsToPersian(selectedItem.getLabel()));
        setPaymentMethod(selectedItem);
      }
    }

    mainActivity.changeTitle(
        isRejected() ? getString(R.string.return_info) : getString(R.string.payment_info));
    customerNameTv.setText(NumberUtil.digitsToPersian(customer.getFullName()));

    Double total = Double.valueOf(order.getAmount()) / 1000D;

    costTv.setText(NumberUtil.digitsToPersian(
        String.format(Locale.getDefault(), "%s %s", NumberUtil.getCommaSeparated(total),
            getString(R.string.common_irr_currency))));
    String orderDate = order.getDate();
    dateTv.setText(Empty.isNotEmpty(orderDate) ? NumberUtil.digitsToPersian(orderDate) : "--");
    Long number = order.getNumber();
    orderCodeTv.setText(
        Empty.isNotEmpty(number) && order.getNumber() != 0 ? NumberUtil
            .digitsToPersian(String.valueOf(number)) : "--");

    String title = getProperTitle();

    dateTitleTv.setText(String.format(Locale.US, getString(R.string.date_x), title));
    orderCodeTitleTv.setText(String.format(Locale.US, getString(R.string.number_x), title));
    submitOrderBtn.setText(String.format(Locale.US, getString(R.string.x_order_submit), title));

    if (isRejected()) {
      amountTv.setText(R.string.amount_to_return);
      paymentTypeTv.setText(R.string.reason_to_return);
      submitOrderBtn
          .setBackgroundColor(ContextCompat.getColor(mainActivity, R.color.register_return));
      if (paymentTypeTitle != null) {
        paymentTypeTitle.setText(R.string.select_reason_to_return);
      }
      descriptionLayout.setHint(getString(R.string.reject_description));
      orderGiftLayout.setVisibility(View.GONE);
    } else if (selectedItem != null) {
      setPaymentMethod(selectedItem);
    } else {
      paymentMethodTv.setText(R.string.click_to_select);
    }
    if (pageStatus.equals(Constants.VIEW)) {
      submitOrderBtn.setText(getString(R.string.close));
      descriptionEdt.setEnabled(false);
      orderGiftLayout.setVisibility(View.GONE);
    }
    if (MultiScreenUtility.isTablet(mainActivity)) {
      setUpRecyclerView();
      paymentTypeTv.setVisibility(View.GONE);
      paymentTypeLayout.setVisibility(View.GONE);
    }

    if (Empty.isNotEmpty(order.getDescription())) {
      descriptionEdt.setText(order.getDescription());
    }
  }

  public void setPaymentMethod(LabelValue paymentMethod) {
    selectedItem = paymentMethod;
    paymentMethodTv.setText(NumberUtil.digitsToPersian(selectedItem.getLabel()));
  }

  @OnClick({R.id.payment_method_tv, R.id.submit_order_btn, R.id.order_gift_layout,
      R.id.register_gift_tv})
  public void onClick(View view) {
    switch (view.getId()) {
      case R.id.payment_method_tv:
        if (!pageStatus.equals(Constants.VIEW) && !MultiScreenUtility.isTablet(mainActivity)) {
          showPaymentMethodDialog();
        }
        break;
      case R.id.order_gift_layout:
      case R.id.register_gift_tv:
        if (giftRequestSent) {
          registerGiftTv.setText("در حال دریافت اطلاعات ...");
          new GiftDataTransferBizImpl(mainActivity).exchangeData(orderBackendId);
        } else {
          order = saleOrderService.findOrderDtoById(orderId);
          if (validateOrderForSave()) {
            order.setStatus(SaleOrderStatus.GIFT.getId());
            order.setPaymentTypeBackendId(selectedItem.getValue());
            order.setDescription(descriptionEdt.getText().toString());
            order.setSalesmanId(
                Long.valueOf(settingService.getSettingValue(ApplicationKeys.SALESMAN_ID)));
            saleOrderService.saveOrder(order);
            SaleOrderService saleOrderService = new SaleOrderServiceImpl(mainActivity);
            BaseSaleDocument saleOrder = saleOrderService.findOrderDocumentByOrderId(orderId);
            registerGiftTv.setText("در حال ارسال ...");
            if (Empty.isNotEmpty(saleOrder)) {
              saleOrder.setStatusCode(SaleOrderStatus.GIFT.getId());
              OrdersDataTransferBizImpl dataTransfer = new OrdersDataTransferBizImpl(mainActivity);
              dataTransfer.sendSingleOrder(saleOrder);
            }
          }
        }
        break;
      case R.id.submit_order_btn:
        order = saleOrderService.findOrderDtoById(orderId);
        if (validateOrderForSave()) {
          if (orderStatus.equals(SaleOrderStatus.REJECTED_DRAFT.getId()) || orderStatus
              .equals(SaleOrderStatus.REJECTED.getId())) {
            showSaveOrderConfirmDialog(getString(R.string.title_save_order),
                SaleOrderStatus.REJECTED.getId());
          } else {
            if (isCold()) {
              showSaveOrderConfirmDialog(getString(R.string.title_save_order),
                  SaleOrderStatus.READY_TO_SEND.getId());
            } else {
              showSaveOrderConfirmDialog(getString(R.string.title_save_order),
                  SaleOrderStatus.INVOICED.getId());
            }
          }
        }
        break;
    }
  }

  private void showSaveOrderConfirmDialog(String title, final Long statusId) {
    DialogUtil.showConfirmDialog(mainActivity, title,
        getString(R.string.message_are_you_sure), (dialog, which) ->
        {
          if (!pageStatus.equals(Constants.VIEW)) {
            saveOrder(statusId);
          }
          if (visitId != 0l) {// WHY?
            mainActivity.navigateToFragment(OrderFragment.class.getSimpleName());
          } else {
            mainActivity.navigateToFragment(OrderFragment.class.getSimpleName());
          }
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

  private void saveOrder(Long statusId) {
    try {
      order.setStatus(statusId);

      if (isRejected()) {
        //Add reason or reject to orders
        order.setDescription(Empty.isNotEmpty(selectedItem) ? selectedItem.getLabel() : "");
      } else {
        order.setPaymentTypeBackendId(selectedItem.getValue());
        order.setDescription(descriptionEdt.getText().toString());
      }

      //Distributer should not enter his salesmanId.
      if (!SaleOrderStatus.DELIVERABLE.getId().equals(orderStatus)) {
        order.setSalesmanId(
            Long.valueOf(settingService.getSettingValue(ApplicationKeys.SALESMAN_ID)));
      }
      long typeId = saleOrderService.saveOrder(order);
      if (visitId != 0L) {
        VisitInformationDetail visitDetail = new VisitInformationDetail(visitId, getDetailType(),
            typeId);
        visitService.saveVisitDetail(visitDetail);
      }
    } catch (BusinessException ex) {
      Log.e(TAG, ex.getMessage(), ex);
      ToastUtil.toastError(mainActivity, ex);
    } catch (Exception ex) {
      Logger.sendError("Data Storage Exception",
          "Error in saving new order detail " + ex.getMessage());
      Log.e(TAG, ex.getMessage(), ex);
      ToastUtil.toastError(mainActivity, new UnknownSystemException(ex));
    }
  }

  private VisitInformationDetailType getDetailType() {
    if (isRejected()) {
      return VisitInformationDetailType.CREATE_REJECT;
    }
    switch (saleType) {
      case ApplicationKeys.SALE_COLD:
        return VisitInformationDetailType.CREATE_ORDER;
      case ApplicationKeys.SALE_HOT:
        return VisitInformationDetailType.CREATE_INVOICE;
      case ApplicationKeys.SALE_DISTRIBUTER:
        return VisitInformationDetailType.DELIVER_ORDER;
    }
    //Should not happen
    return null;
  }

  private boolean validateOrderForSave() {
    if (Empty.isEmpty(selectedItem) && !isRejected()) {
      ToastUtil.toastError(mainActivity, getString(R.string.error_no_payment_method_selected));
      return false;
    }
    if (order.getOrderItems().size() == 0) {
      ToastUtil.toastError(mainActivity,
          getProperTitle() + getString(R.string.message_x_has_no_item_for_save));
      return false;
    }
    return true;
  }

  private boolean isCold() {
    return saleType.equals(ApplicationKeys.SALE_COLD);
  }


  /*
   @return Proper title for which could be "Rejected", "Order" or "Invoice"
   */
  private String getProperTitle() {
    if (orderStatus.equals(SaleOrderStatus.REJECTED_DRAFT.getId()) ||
        orderStatus.equals(SaleOrderStatus.REJECTED.getId()) ||
        orderStatus.equals(SaleOrderStatus.REJECTED_SENT.getId())) {
      return getString(R.string.title_reject);
    } else if (saleType.equals(ApplicationKeys.SALE_COLD)) {
      return getString(R.string.title_order);
    } else {
      return getString(R.string.title_factor);
    }
  }

  /*
   @return true if it's one of the REJECTED states
    */
  private boolean isRejected() {
    return (orderStatus.equals(SaleOrderStatus.REJECTED_DRAFT.getId()) ||
        orderStatus.equals(SaleOrderStatus.REJECTED.getId()) ||
        orderStatus.equals(SaleOrderStatus.REJECTED_SENT.getId()));

  }

  private boolean isDisable() {
    return orderStatus.equals(SaleOrderStatus.CANCELED.getId())
        || orderStatus.equals(SaleOrderStatus.INVOICED.getId())
        || orderStatus.equals(SaleOrderStatus.SENT.getId())
        || orderStatus.equals(SaleOrderStatus.SENT_INVOICE.getId())
        || orderStatus.equals(SaleOrderStatus.REJECTED_SENT.getId());
  }

  private void showPaymentMethodDialog() {
    if (isDisable()) {
      return;
    }
    FragmentTransaction ft = mainActivity.getSupportFragmentManager().beginTransaction();
    PaymentMethodDialogFragment paymentMethodDialogFragment = PaymentMethodDialogFragment
        .newInstance(this, selectedItem, isRejected());
    paymentMethodDialogFragment.show(ft, "payment method");
  }

  //set up recycler view
  private void setUpRecyclerView() {
    List<LabelValue> dataModel = getModel();
    adapter = new PaymentMethodAdapter(mainActivity, dataModel, selectedItem, this,
        !pageStatus.equals(Constants.VIEW));
    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());

    if (recyclerView != null) {
      recyclerView.setLayoutManager(linearLayoutManager);
      recyclerView.setAdapter(adapter);
      recyclerView.scrollToPosition(dataModel.indexOf(selectedItem));
    }
  }

  private List<LabelValue> getModel() {
    return baseInfoService.getAllBaseInfosLabelValuesByTypeId(
        isRejected() ? BaseInfoTypes.REJECT_TYPE.getId() : BaseInfoTypes.PAYMENT_TYPE.getId());
  }

  @Override
  public int getFragmentId() {
    return MainActivity.ORDER_INFO_FRAGMENT;
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
  }

  @Subscribe
  public void getMessage(Event event) {
    if (event instanceof ErrorEvent) {
      registerGiftTv.setText("خطا در ارتباط با سرور");
    } else if (event instanceof SendOrderEvent) {
      if (event.getStatusCode() == StatusCodes.SUCCESS) {
        registerGiftTv.setText("مشاهده تخفیف و جوایز");
        giftRequestSent = true;
        orderBackendId = ((SendOrderEvent) event).getOrderId();
      }
    } else if (event instanceof DataTransferSuccessEvent) {
      if (event.getStatusCode() == StatusCodes.SUCCESS) {
        registerGiftTv.setText("مشاهده تخفیف و جوایز");
        DialogUtil.showCustomDialog(mainActivity, "تخفیف و جوایز", event.getMessage(),"تایید",
            (dialogInterface, i) -> dialogInterface.dismiss(),"",null,Constants.ICON_MESSAGE);
      } else if (event.getStatusCode() == StatusCodes.NO_DATA_ERROR) {
        registerGiftTv.setText("تلاش مجدد");

      }
    }
  }
}
