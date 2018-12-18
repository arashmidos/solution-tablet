package com.parsroyal.solutiontablet.ui.adapter;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Optional;
import com.parsroyal.solutiontablet.R;
import com.parsroyal.solutiontablet.constants.Constants;
import com.parsroyal.solutiontablet.constants.SendStatus;
import com.parsroyal.solutiontablet.data.listmodel.PaymentListModel;
import com.parsroyal.solutiontablet.service.impl.PaymentServiceImpl;
import com.parsroyal.solutiontablet.ui.activity.MainActivity;
import com.parsroyal.solutiontablet.ui.adapter.PaymentAdapter.ViewHolder;
import com.parsroyal.solutiontablet.util.DateUtil;
import com.parsroyal.solutiontablet.util.DialogUtil;
import com.parsroyal.solutiontablet.util.Empty;
import com.parsroyal.solutiontablet.util.NumberUtil;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by Shakib on 8/27/2017.
 */

public class PaymentAdapter extends Adapter<ViewHolder> {

  private final PaymentServiceImpl paymentService;
  private LayoutInflater inflater;
  private long visitId;
  private boolean isFromReport;
  private MainActivity mainActivity;
  private List<PaymentListModel> payments;

  public PaymentAdapter(MainActivity mainActivity, List<PaymentListModel> payments, long visitId,
      boolean isFromReport) {
    this.isFromReport = isFromReport;
    this.visitId = visitId;
    this.payments = payments;
    this.mainActivity = mainActivity;
    inflater = LayoutInflater.from(mainActivity);
    paymentService = new PaymentServiceImpl(mainActivity);
  }

  @NonNull
  @Override
  public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    View view = inflater.inflate(R.layout.item_payment_list, parent, false);

    return new ViewHolder(view);
  }

  @Override
  public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
    try {
      holder.setData(position);
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  @Override
  public int getItemCount() {
    return payments.size();
  }

  public void update(List<PaymentListModel> payments) {
    this.payments = payments;
    notifyDataSetChanged();
  }

  private String getPaymentType(String paymentType) {
    switch (paymentType) {
      case "6":
        return mainActivity.getString(R.string.cheque);
      case "1":
        return mainActivity.getString(R.string.cash);
      case "2":
        return mainActivity.getString(R.string.e_payment);
    }
    return null;
  }

  private void goToRegisterPaymentFragment(PaymentListModel payment) {
    Bundle args = new Bundle();
    args.putLong(Constants.VISIT_ID, visitId);
    args.putLong(Constants.CUSTOMER_BACKEND_ID, payment.getCustomerBackendId());
    args.putLong(Constants.PAYMENT_ID, payment.getPrimaryKey());
    mainActivity.changeFragment(MainActivity.REGISTER_PAYMENT_FRAGMENT, args, true);
  }

  public class ViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.payment_status_tv)
    TextView paymentStatusTv;
    @BindView(R.id.payment_method_tv)
    TextView paymentMethodTv;
    @BindView(R.id.payment_date_tv)
    TextView paymentDateTv;
    @BindView(R.id.payment_tv)
    TextView paymentTv;
    @BindView(R.id.customer_name_tv)
    TextView customerNameTv;
    @BindView(R.id.customer_code_tv)
    TextView customerCodeTv;
    @BindView(R.id.bank_detail_tv)
    TextView bankDetailTv;
    @BindView(R.id.delete_img)
    ImageView deleteImg;
    @BindView(R.id.edit_img)
    ImageView editImg;
    @Nullable
    @BindView(R.id.customer_layout)
    LinearLayout customerLayout;

    private PaymentListModel payment;
    private int position;

    public ViewHolder(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);
    }

    @Optional
    @OnClick({R.id.edit_img, R.id.edit_img_layout, R.id.delete_img_layout, R.id.delete_img,
        R.id.main_lay, R.id.main_lay_linear})
    public void onClick(View v) {
      switch (v.getId()) {
        case R.id.delete_img_layout:
        case R.id.delete_img:
          if (payment.getStatus().equals(SendStatus.SENT.getId())) {
            return;
          }
          deletePayment();
          break;
        case R.id.edit_img:
        case R.id.edit_img_layout:
        case R.id.main_lay:
        case R.id.main_lay_linear:
          goToRegisterPaymentFragment(payment);
          break;
      }
    }

    private void deletePayment() {
      DialogUtil.showConfirmDialog(mainActivity, mainActivity.getString(R.string.title_attention),
          mainActivity.getString(R.string.message_payment_delete_confirm), (dialog, which) ->
          {
            paymentService.deletePayment(payment.getPrimaryKey());
            payments.remove(position);
            notifyDataSetChanged();
          });
    }

    public void setData(int position) {
      this.position = position;
      payment = payments.get(position);

      changeVisibility();
      paymentMethodTv.setText(NumberUtil.digitsToPersian(getPaymentType(payment.getType())));
      Date createDate = DateUtil.convertStringToDate(payment.getDate(),
          DateUtil.FULL_FORMATTER_GREGORIAN_WITH_TIME, "FA");

      String dateString = DateUtil.getFullPersianDate(createDate);
      paymentDateTv.setText(NumberUtil.digitsToPersian(dateString));
      long amountValue = Long
          .parseLong(NumberUtil.digitsToEnglish(payment.getAmount().replaceAll(",", "")));
      String number = String.format(Locale.US, "%,d %s", amountValue / 1000, mainActivity.getString(
          R.string.common_irr_currency));
      paymentTv.setText(NumberUtil.digitsToPersian(number));
      if (payment.getType().equals("6")) {
        bankDetailTv.setText(String
            .format(Locale.getDefault(), "بانک %s / شعبه %s",
                Empty.isNotEmpty(payment.getBank()) ? payment.getBank() : "--",
                Empty.isNotEmpty(payment.getBranch()) ? payment.getBranch() : "--"));
      }
    }

    private void changeVisibility() {
      if (payment.getStatus().equals(SendStatus.SENT.getId())) {
        paymentStatusTv.setVisibility(View.VISIBLE);
        editImg.setVisibility(View.INVISIBLE);
        deleteImg.setVisibility(View.INVISIBLE);
      }
      if (isFromReport) {
        customerNameTv.setVisibility(View.VISIBLE);
        customerNameTv.setText(payment.getCustomerFullName());
        customerCodeTv.setVisibility(View.VISIBLE);
        customerCodeTv
            .setText(NumberUtil.digitsToPersian(String.format("(%s)", payment.getCustomerCode())));
        if (customerLayout != null) {
          customerLayout.setVisibility(View.VISIBLE);
        }
      } else {
        customerNameTv.setVisibility(View.INVISIBLE);
        customerCodeTv.setVisibility(View.INVISIBLE);
        if (customerLayout != null) {
          customerLayout.setVisibility(View.INVISIBLE);
        }
      }
    }
  }
}
