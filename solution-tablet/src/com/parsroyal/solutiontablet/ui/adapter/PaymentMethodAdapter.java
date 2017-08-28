package com.parsroyal.solutiontablet.ui.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.parsroyal.solutiontablet.R;
import com.parsroyal.solutiontablet.data.model.LabelValue;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by ShakibIsTheBest on 8/27/2017.
 */

public class PaymentMethodAdapter extends RecyclerView.Adapter<PaymentMethodAdapter.ViewHolder> {

  private LayoutInflater inflater;
  private LabelValue selectedItem;
  private Context context;
  private List<LabelValue> paymentMethods;

  public PaymentMethodAdapter(Context context, List<LabelValue> paymentMethods, LabelValue selectedItem) {
    this.context = context;
    this.selectedItem = selectedItem;
    this.paymentMethods = paymentMethods;
    inflater = LayoutInflater.from(context);
  }

  @Override
  public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view = inflater.inflate(R.layout.item_payment_method, parent, false);
    return new ViewHolder(view);
  }

  @Override
  public void onBindViewHolder(ViewHolder holder, int position) {
    LabelValue paymentMethod = paymentMethods.get(position);
    holder.paymentMethodTv.setText(paymentMethod.getLabel());
    if (selectedItem != null && paymentMethod.getLabel().equals(selectedItem.getLabel())) {
      holder.mainLay.setBackgroundColor(ContextCompat.getColor(context, R.color.primary_dark));
      holder.paymentMethodTv.setTextColor(ContextCompat.getColor(context, android.R.color.white));
    } else {
      holder.mainLay.setBackgroundColor(ContextCompat.getColor(context, android.R.color.white));
      holder.paymentMethodTv.setTextColor(ContextCompat.getColor(context, R.color.black_85));
    }
    lastItem(position == paymentMethods.size() - 1, holder);
    holder.mainLay.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        selectedItem = paymentMethod;
        notifyDataSetChanged();
      }
    });
  }

  private void lastItem(boolean isLastItem, ViewHolder holder) {
    LinearLayout.LayoutParams parameter = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    if (isLastItem) {
      parameter.setMargins(0, 0, 0, 160);
    } else {
      parameter.setMargins(0, 0, 0, 0);
    }
    holder.bottomLine.setVisibility(isLastItem ? View.GONE : View.VISIBLE);
    holder.mainLay.setLayoutParams(parameter);
  }

  public LabelValue getSelectedItem() {
    return selectedItem;
  }

  @Override
  public int getItemCount() {
    return paymentMethods.size();
  }

  public class ViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.payment_method_tv) TextView paymentMethodTv;
    @BindView(R.id.main_lay) RelativeLayout mainLay;
    @BindView(R.id.bottom_line) View bottomLine;

    public ViewHolder(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);
    }
  }
}
