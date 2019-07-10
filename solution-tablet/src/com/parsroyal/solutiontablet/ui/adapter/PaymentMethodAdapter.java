package com.parsroyal.solutiontablet.ui.adapter;

import android.content.Context;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.parsroyal.solutiontablet.R;
import com.parsroyal.solutiontablet.data.model.LabelValue;

import com.parsroyal.solutiontablet.ui.fragment.OrderInfoFragment;
import com.parsroyal.solutiontablet.util.MultiScreenUtility;
import com.parsroyal.solutiontablet.util.NumberUtil;
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
  private OrderInfoFragment orderInfoFragment;
  private boolean isEditable;

  public PaymentMethodAdapter(Context context, List<LabelValue> paymentMethods,
      LabelValue selectedItem, OrderInfoFragment orderInfoFragment, boolean isEditable) {
    this.context = context;
    this.isEditable = isEditable;
    this.selectedItem = selectedItem;
    this.paymentMethods = paymentMethods;
    this.orderInfoFragment = orderInfoFragment;
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
    holder.setData(paymentMethod, position);

    if (!MultiScreenUtility.isTablet(context)) {
      lastItem(position == paymentMethods.size() - 1, holder);
    }
  }

  private void lastItem(boolean isLastItem, ViewHolder holder) {
    LinearLayout.LayoutParams parameter = new LinearLayout.LayoutParams(
        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
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

    @BindView(R.id.payment_method_tv)
    TextView paymentMethodTv;
    @BindView(R.id.main_lay)
    RelativeLayout mainLay;
    @BindView(R.id.bottom_line)
    View bottomLine;
    private LabelValue model;
    private int position;

    public ViewHolder(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);
    }

    public void setData(LabelValue model, int position) {
      this.model = model;
      this.position = position;
      paymentMethodTv.setText(NumberUtil.digitsToPersian(model.getLabel()));
      if (selectedItem != null && model.getLabel().equals(selectedItem.getLabel())) {
        mainLay.setBackgroundColor(ContextCompat.getColor(context, R.color.primary_dark));
        paymentMethodTv.setTextColor(ContextCompat.getColor(context, android.R.color.white));
      } else {
        mainLay.setBackgroundColor(ContextCompat.getColor(context, android.R.color.white));
        paymentMethodTv.setTextColor(ContextCompat.getColor(context, R.color.black_85));
      }

      mainLay.setOnClickListener(v -> {
        if (isEditable) {
          selectedItem = model;
          notifyDataSetChanged();
          if (orderInfoFragment != null) {
            orderInfoFragment.setPaymentMethod(selectedItem);
          }
        }
      });
    }
  }
}
