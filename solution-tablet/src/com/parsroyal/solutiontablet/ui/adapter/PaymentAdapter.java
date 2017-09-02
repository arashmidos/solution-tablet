package com.parsroyal.solutiontablet.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.parsroyal.solutiontablet.R;
import com.parsroyal.solutiontablet.data.listmodel.PaymentListModel;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by ShakibIsTheBest on 8/27/2017.
 */

public class PaymentAdapter extends RecyclerView.Adapter<PaymentAdapter.ViewHolder> {

  private LayoutInflater inflater;
  private Context context;
  private List<PaymentListModel> payments;

  public PaymentAdapter(Context context, List<PaymentListModel> payments) {
    this.context = context;
    this.payments = payments;
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
    holder.paymentMethodTv.setText(payment.getType());
    holder.paymentDateTv.setText(payment.getDate());
    holder.paymentTv.setText(payment.getAmount());
    //TODO:add bank and branch
//    holder.bankDetailTv.setText(payment.get);
    holder.mainLay.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        Toast.makeText(context, "clicked", Toast.LENGTH_SHORT).show();
      }
    });
  }

  @Override
  public int getItemCount() {
    return payments.size();
  }

  public void update(List<PaymentListModel> payments) {
    this.payments = payments;
    notifyDataSetChanged();
  }

  public class ViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.payment_method_tv) TextView paymentMethodTv;
    @BindView(R.id.payment_date_tv) TextView paymentDateTv;
    @BindView(R.id.payment_tv) TextView paymentTv;
    @BindView(R.id.bank_detail_tv) TextView bankDetailTv;
    @BindView(R.id.main_lay) RelativeLayout mainLay;

    public ViewHolder(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);
    }
  }
}
