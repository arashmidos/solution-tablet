package com.parsroyal.solutiontablet.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.parsroyal.solutiontablet.R;
import com.parsroyal.solutiontablet.data.listmodel.SaleOrderListModel;
import com.parsroyal.solutiontablet.util.DateUtil;
import com.parsroyal.solutiontablet.util.NumberUtil;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by ShakibIsTheBest on 8/27/2017.
 */

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.ViewHolder> {

  private LayoutInflater inflater;
  private Context context;
  private List<SaleOrderListModel> orders;

  public OrderAdapter(Context context, List<SaleOrderListModel> orders) {
    this.context = context;
    this.orders = orders;
    inflater = LayoutInflater.from(context);
  }

  @Override
  public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view = inflater.inflate(R.layout.item_order_list, parent, false);
    return new ViewHolder(view);
  }

  @Override
  public void onBindViewHolder(ViewHolder holder, int position) {
    SaleOrderListModel order = orders.get(position);
    String orderCode = "کد سفارش : " + String.valueOf(order.getId());
    holder.orderCodeTv.setText(orderCode);
    holder.orderCountTv.setText("--");
    holder.orderDateTv.setText(order.getDate());

    String number = String.format(Locale.US, "%,d %s", order.getAmount() / 1000, context.getString(
        R.string.common_irr_currency));
    holder.orderTotalPrice.setText(number);
    holder.orderPaymentMethodTv.setText(order.getPaymentTypeTitle());
  }

  @Override
  public int getItemCount() {
    return orders.size();
  }

  public void update(List<SaleOrderListModel> orders) {
    this.orders = orders;
    notifyDataSetChanged();
  }

  public class ViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.order_code_tv)
    TextView orderCodeTv;
    @BindView(R.id.order_date_tv)
    TextView orderDateTv;
    @BindView(R.id.order_total_price)
    TextView orderTotalPrice;
    @BindView(R.id.order_count_tv)
    TextView orderCountTv;
    @BindView(R.id.order_payment_method_tv)
    TextView orderPaymentMethodTv;

    public ViewHolder(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);
    }
  }
}
