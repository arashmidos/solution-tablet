package com.parsroyal.solutiontablet.ui.adapter;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.parsroyal.solutiontablet.R;
import com.parsroyal.solutiontablet.constants.SaleOrderStatus;
import com.parsroyal.solutiontablet.data.listmodel.SaleOrderListModel;
import com.parsroyal.solutiontablet.ui.adapter.OrderAdapter.ViewHolder;
import com.parsroyal.solutiontablet.util.DateUtil;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by ShakibIsTheBest on 8/27/2017.
 */

public class OrderAdapter extends Adapter<ViewHolder> {

  @BindView(R.id.order_status_tv) TextView orderStatusTv;
  private LayoutInflater inflater;
  private Context context;
  private boolean isFromOrder;
  private List<SaleOrderListModel> orders;

  public OrderAdapter(Context context, List<SaleOrderListModel> orders, boolean isFromOrder) {
    this.context = context;
    this.orders = orders;
    this.isFromOrder = isFromOrder;
    inflater = LayoutInflater.from(context);
  }

  @Override
  public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view;
    if (isFromOrder) {
      view = inflater.inflate(R.layout.item_order_report_list, parent, false);
    } else {
      view = inflater.inflate(R.layout.item_order_list, parent, false);
    }
    return new ViewHolder(view);
  }

  @Override
  public void onBindViewHolder(ViewHolder holder, int position) {
    SaleOrderListModel order = orders.get(position);
    if (isFromOrder) {
      holder.orderStatusTv.setText(SaleOrderStatus.getDisplayTitle(context, order.getStatus()));
    }
    String orderCode = "کد سفارش : " + String.valueOf(order.getId());
    holder.orderCodeTv.setText(orderCode);
    holder.orderCountTv.setText("--");
    Date createDate = DateUtil
        .convertStringToDate(order.getDate().replace('/', '-'), DateUtil.FULL_FORMATTER_GREGORIAN,
            "FA");

    String dateString = DateUtil.getFullPersianDate(createDate);
    holder.orderDateTv.setText(dateString);
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
    @Nullable
    @BindView(R.id.order_status_tv)
    TextView orderStatusTv;

    public ViewHolder(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);
    }
  }
}
