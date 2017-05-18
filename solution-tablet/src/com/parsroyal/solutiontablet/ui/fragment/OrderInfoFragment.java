package com.parsroyal.solutiontablet.ui.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.parsroyal.solutiontablet.R;
import com.parsroyal.solutiontablet.constants.BaseInfoTypes;
import com.parsroyal.solutiontablet.constants.SaleOrderStatus;
import com.parsroyal.solutiontablet.data.entity.Customer;
import com.parsroyal.solutiontablet.data.model.LabelValue;
import com.parsroyal.solutiontablet.data.model.SaleOrderDto;
import com.parsroyal.solutiontablet.exception.SaleOrderNotFoundException;
import com.parsroyal.solutiontablet.exception.UnknownSystemException;
import com.parsroyal.solutiontablet.service.BaseInfoService;
import com.parsroyal.solutiontablet.service.impl.BaseInfoServiceImpl;
import com.parsroyal.solutiontablet.service.SaleOrderService;
import com.parsroyal.solutiontablet.service.impl.SaleOrderServiceImpl;
import com.parsroyal.solutiontablet.ui.MainActivity;
import com.parsroyal.solutiontablet.ui.adapter.LabelValueArrayAdapter;
import com.parsroyal.solutiontablet.util.Empty;
import com.parsroyal.solutiontablet.util.NumberUtil;
import com.parsroyal.solutiontablet.util.ToastUtil;
import com.parsroyal.solutiontablet.util.constants.ApplicationKeys;
import java.util.List;
import java.util.Locale;

/**
 * Created by Arashmidos on 2017-03-07.
 */

public class OrderInfoFragment extends BaseFragment {

  public final String TAG = OrderInfoFragment.class.getSimpleName();
  @BindView(R.id.orderDateTv)
  TextView orderDateTv;
  @BindView(R.id.orderDateLabel)
  TextView orderDateLabel;
  @BindView(R.id.orderNumberTv)
  TextView orderNumberTv;
  @BindView(R.id.orderNoLabel)
  TextView orderNumberLabel;
  @BindView(R.id.customerNameTv)
  TextView customerNameTv;
  @BindView(R.id.paymentTypeSp)
  Spinner paymentTypeSp;
  @BindView(R.id.paymentTypeLabel)
  TextView paymentTypeLabel;
  @BindView(R.id.orderAmountTv)
  TextView orderAmountTv;
  @BindView(R.id.orderAmountLabel)
  TextView orderAmountLabel;
  @BindView(R.id.orderDescriptionTxt)
  EditText orderDescriptionTxt;
  @BindView(R.id.orderDescriptionLabel)
  TextView orderDescriptionLabel;

  private SaleOrderDto order;
  private SaleOrderService saleOrderService;
  private BaseInfoService baseInfoService;

  private Long orderId;
  private Long orderStatus;
  private MainActivity context;
  private String saleType;

  public OrderInfoFragment() {
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    try {
      context = (MainActivity) getActivity();
      saleOrderService = new SaleOrderServiceImpl(getActivity());
      baseInfoService = new BaseInfoServiceImpl(getActivity());
      try {
        orderId = getArguments().getLong("orderId");
        saleType = getArguments().getString("saleType");

      } catch (Exception ex) {
        Log.e(TAG, ex.getMessage(), ex);
      }
      if (Empty.isNotEmpty(orderId)) {
        order = saleOrderService.findOrderDtoById(orderId);
      }
      //Refresh order item
//            order = saleOrderService.findOrderDtoById(orderId);
      if (Empty.isNotEmpty(order)) {

        View view = inflater.inflate(R.layout.fragment_order_info, null);
        ButterKnife.bind(this, view);
        orderStatus = order.getStatus();

        orderDescriptionTxt.setEnabled(!isDisable());
        paymentTypeSp.setEnabled(!isDisable());

        String title = getProperTitle();

        orderNumberLabel.setText(String.format(Locale.US, getString(R.string.number_x), title));
        orderDateLabel.setText(String.format(Locale.US, getString(R.string.date_x), title));
        orderAmountLabel.setText(String.format(Locale.US, getString(R.string.amount_x), title));
        orderDescriptionLabel
            .setText(String.format(Locale.US, getString(R.string.description_x), title));

        if (isRejected()) {
          paymentTypeLabel.setText(getString(R.string.reject_reason_title));
        } else {
          paymentTypeLabel.setText(getString(R.string.payment_type));
        }

        Customer customer = order.getCustomer();
        if (Empty.isNotEmpty(customer) && Empty.isNotEmpty(customer.getFullName())) {
          customerNameTv.setText(customer.getFullName());
        }

        Long number = order.getNumber();
        orderNumberTv.setText(
            Empty.isNotEmpty(number) && order.getNumber() != 0 ? String.valueOf(number) : "--");

        String orderDate = order.getDate();
        orderDateTv.setText(Empty.isNotEmpty(orderDate) ? orderDate : "--");

        Double orderAmount = Double.valueOf(order.getAmount()) / 1000D;
        orderAmountTv.setText(
            Empty.isNotEmpty(orderAmount) ? NumberUtil.getCommaSeparated(orderAmount) : "--");

        final List<LabelValue> paymentTypeList = baseInfoService
            .getAllBaseInfosLabelValuesByTypeId(BaseInfoTypes.PAYMENT_TYPE.getId());
        if (Empty.isNotEmpty(paymentTypeList)) {
          if (isRejected()) {
            List<LabelValue> rejectReason = baseInfoService
                .getAllBaseInfosLabelValuesByTypeId(BaseInfoTypes.REJECT_TYPE.getId());
            paymentTypeSp.setAdapter(new LabelValueArrayAdapter(context, rejectReason));
          } else {
            paymentTypeSp.setAdapter(new LabelValueArrayAdapter(context, paymentTypeList));
            if (Empty.isNotEmpty(order.getPaymentTypeBackendId())) {
              int position = 0;
              for (LabelValue labelValue : paymentTypeList) {
                if (labelValue.getValue().equals(order.getPaymentTypeBackendId())) {
                  paymentTypeSp.setSelection(position);
                  break;
                }
                position++;
              }
            }
          }
        }

        String description = order.getDescription();
        orderDescriptionTxt.setText(Empty.isNotEmpty(description) ? description : "--");

        return view;

      } else {
        throw new SaleOrderNotFoundException("orderId : " + orderId);
      }

    } catch (Exception e) {
      Log.e(TAG, e.getMessage(), e);
      ToastUtil.toastError(getActivity(), new UnknownSystemException(e));
      return inflater.inflate(R.layout.view_error_page, null);
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
    return 0;
  }

  public long getSelectedPaymentType() {
    LabelValue selectedPaymentItem = (LabelValue) paymentTypeSp.getSelectedItem();
    return selectedPaymentItem.getValue();
  }

  public String getDescription() {
    return orderDescriptionTxt.getText().toString();
  }
}

