package com.parsroyal.solutiontablet.ui.adapter;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.parsroyal.solutiontablet.R;
import com.parsroyal.solutiontablet.constants.Constants;
import com.parsroyal.solutiontablet.data.listmodel.PaymentListModel;
import com.parsroyal.solutiontablet.ui.MainActivity;
import java.util.List;

/**
 * Created by ShakibIsTheBest on 8/27/2017.
 */

public class PaymentAdapter extends RecyclerView.Adapter<PaymentAdapter.ViewHolder> {

  private LayoutInflater inflater;
  private Context context;
  private long visitId;
  private MainActivity mainActivity;
  private List<PaymentListModel> payments;

  public PaymentAdapter(Context context, List<PaymentListModel> payments, long visitId) {
    this.context = context;
    this.visitId = visitId;
    this.payments = payments;
    mainActivity = (MainActivity) context;
    inflater = LayoutInflater.from(context);
  }

  @Override
  public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view = inflater.inflate(R.layout.item_payment_list, parent, false);
    return new ViewHolder(view);
  }

  @Override
  public void onBindViewHolder(ViewHolder holder, int position) {
    PaymentListModel payment = payments.get(position);
    holder.paymentMethodTv.setText(getPaymentType(payment.getType()));
    holder.paymentDateTv.setText(payment.getDate());
    long amount = Long.parseLong(payment.getAmount()) / 1000;
    holder.paymentTv.setText(String.valueOf(amount));
    //TODO:add bank and branch
//    holder.bankDetailTv.setText(payment.get);
    holder.mainLay.setOnClickListener(v -> goToRegisterPaymentFragment(payment));
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
    mainActivity.changeFragment(mainActivity.REGISTER_PAYMENT_FRAGMENT, args, false);
  }

  public class ViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.payment_method_tv)
    TextView paymentMethodTv;
    @BindView(R.id.payment_date_tv)
    TextView paymentDateTv;
    @BindView(R.id.payment_tv)
    TextView paymentTv;
    @BindView(R.id.bank_detail_tv)
    TextView bankDetailTv;
    @BindView(R.id.main_lay)
    RelativeLayout mainLay;

    public ViewHolder(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);
    }
  }
}
