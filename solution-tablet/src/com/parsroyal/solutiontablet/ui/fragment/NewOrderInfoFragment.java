package com.parsroyal.solutiontablet.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.parsroyal.solutiontablet.R;
import com.parsroyal.solutiontablet.constants.Constants;
import com.parsroyal.solutiontablet.constants.SaleOrderStatus;
import com.parsroyal.solutiontablet.data.entity.Customer;
import com.parsroyal.solutiontablet.data.model.LabelValue;
import com.parsroyal.solutiontablet.data.model.SaleOrderDto;
import com.parsroyal.solutiontablet.service.BaseInfoService;
import com.parsroyal.solutiontablet.service.CustomerService;
import com.parsroyal.solutiontablet.service.SaleOrderService;
import com.parsroyal.solutiontablet.service.impl.BaseInfoServiceImpl;
import com.parsroyal.solutiontablet.service.impl.CustomerServiceImpl;
import com.parsroyal.solutiontablet.service.impl.SaleOrderServiceImpl;
import com.parsroyal.solutiontablet.ui.MainActivity;
import com.parsroyal.solutiontablet.ui.fragment.dialogFragment.PaymentMethodDialogFragment;
import com.parsroyal.solutiontablet.util.Empty;
import com.parsroyal.solutiontablet.util.NumberUtil;
import com.parsroyal.solutiontablet.util.constants.ApplicationKeys;
import java.util.Locale;

/**
 * @author Shakib
 */
public class NewOrderInfoFragment extends BaseFragment {

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

  public NewOrderInfoFragment() {
    // Required empty public constructor
  }

  public static NewOrderInfoFragment newInstance() {
    return new NewOrderInfoFragment();
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
    Bundle args = getArguments();
    orderId = args.getLong(Constants.ORDER_ID, -1);
    order = saleOrderService.findOrderDtoById(orderId);
    customer = customerService.getCustomerByBackendId(order.getCustomerBackendId());
    orderStatus = order.getStatus();
    saleType = args.getString(Constants.SALE_TYPE, "");
    visitId = args.getLong(Constants.VISIT_ID, -1);
    setData();
    return view;
  }

  private void setData() {
    if (selectedItem != null) {
      paymentMethodTv.setText(selectedItem.getLabel());
    } else {
      Long paymentTypeBackendId = order.getPaymentTypeBackendId();
      if (Empty.isNotEmpty(paymentTypeBackendId) && paymentTypeBackendId != 0L) {
        selectedItem = baseInfoService.getBaseInfoByBackendId(paymentTypeBackendId);
        paymentMethodTv.setText(selectedItem.getLabel());
      }
    }

    mainActivity.changeTitle(getString(R.string.payment_info));
    customerNameTv.setText(customer.getFullName());

    Double total = Double.valueOf(order.getAmount()) / 1000D;

    costTv.setText(String.format(Locale.US, "%s %s", NumberUtil.getCommaSeparated(total),
        getString(R.string.common_irr_currency)));
    String orderDate = order.getDate();
    dateTv.setText(Empty.isNotEmpty(orderDate) ? orderDate : "--");
    Long number = order.getNumber();
    orderCodeTv.setText(
        Empty.isNotEmpty(number) && order.getNumber() != 0 ? String.valueOf(number) : "--");

    String title = getProperTitle();

    dateTitleTv.setText(String.format(Locale.US, getString(R.string.date_x), title));
    orderCodeTitleTv.setText(String.format(Locale.US, getString(R.string.number_x), title));
    submitOrderBtn.setText(String.format(Locale.US, getString(R.string.x_order_submit), title));

    if (isRejected()) {
      paymentMethodTv.setText(R.string.reject_reason_title);
    } else {
      paymentMethodTv.setText(R.string.payment_type);
    }
  }

  public void setPaymentMethod(LabelValue paymentMethod) {
    selectedItem = paymentMethod;
    paymentMethodTv.setText(selectedItem.getLabel());
  }

  @OnClick({R.id.payment_method_tv, R.id.submit_order_btn})
  public void onClick(View view) {
    switch (view.getId()) {
      case R.id.payment_method_tv:
        showPaymentMethodDialog();
        break;
      case R.id.submit_order_btn:
        order.setPaymentTypeBackendId(selectedItem.getValue());
        break;
    }
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
        || orderStatus.equals(SaleOrderStatus.REJECTED_SENT.getId())
        || orderStatus.equals(SaleOrderStatus.REJECTED.getId());
  }

  @Override
  public int getFragmentId() {
    return MainActivity.ORDER_INFO_FRAGMENT;
  }

  private void showPaymentMethodDialog() {
    if (isDisable()) {
      return;
    }
    FragmentTransaction ft = mainActivity.getSupportFragmentManager().beginTransaction();
    PaymentMethodDialogFragment paymentMethodDialogFragment = PaymentMethodDialogFragment
        .newInstance(this, selectedItem);
    paymentMethodDialogFragment.show(ft, "payment method");
  }
}
