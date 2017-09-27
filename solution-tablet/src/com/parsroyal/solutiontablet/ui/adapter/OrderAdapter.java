package com.parsroyal.solutiontablet.ui.adapter;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.parsroyal.solutiontablet.R;
import com.parsroyal.solutiontablet.constants.Constants;
import com.parsroyal.solutiontablet.constants.SaleOrderStatus;
import com.parsroyal.solutiontablet.data.listmodel.SaleOrderListModel;
import com.parsroyal.solutiontablet.ui.MainActivity;
import com.parsroyal.solutiontablet.ui.adapter.OrderAdapter.ViewHolder;
import com.parsroyal.solutiontablet.util.DateUtil;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by ShakibIsTheBest on 8/27/2017.
 */

public class OrderAdapter extends Adapter<ViewHolder> {

  private LayoutInflater inflater;
  private Context context;
  private boolean isFromOrder;
  private MainActivity mainActivity;
  private List<SaleOrderListModel> orders;
  private Long visitId;
  private String saleType;

  public OrderAdapter(Context context, List<SaleOrderListModel> orders, boolean isFromOrder,
      Long visitId, String saleType) {
    this.context = context;
    this.visitId = visitId == null ? 0 : visitId;
    this.orders = orders;
    this.saleType = saleType;
    this.mainActivity = (MainActivity) context;
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
    holder.setData(position, order);
  }

  @Override
  public int getItemCount() {
    return orders.size();
  }

  public void update(List<SaleOrderListModel> orders) {
    this.orders = orders;
    notifyDataSetChanged();
  }

  public class ViewHolder extends RecyclerView.ViewHolder implements OnClickListener {

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
    @BindView(R.id.main_lay)
    RelativeLayout mainLayRel;
    @Nullable
    @BindView(R.id.main_lay_linear)
    LinearLayout mainLayLin;
    @Nullable
    @BindView(R.id.order_status_tv)
    TextView orderStatusTv;

    private int position;
    private SaleOrderListModel order;

    public ViewHolder(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);
      if (mainLayLin != null) {
        mainLayLin.setOnClickListener(this);
      } else {
        mainLayRel.setOnClickListener(this);
      }
    }

    public void setData(int position, SaleOrderListModel order) {
      this.position = position;
      this.order = order;
      if (isFromOrder) {
        orderStatusTv.setText(SaleOrderStatus.getDisplayTitle(context, order.getStatus()));
      }
      String orderCode = "کد سفارش : " + String.valueOf(order.getId());
      orderCodeTv.setText(orderCode);
      orderCountTv.setText("--");
      /*Date createDate = DateUtil
          .convertStringToDate(order.getCreatedDateTime(), DateUtil.FULL_FORMATTER_GREGORIAN_WITH_TIME,
              "FA");
      String dateString = DateUtil.getFullPersianDate(createDate);*/
      //96/7/5
      Date createdDate = DateUtil.convertStringToDate(order.getDate(), DateUtil.GLOBAL_FORMATTER, "FA");
      String dateString = DateUtil.getFullPersianDate(createdDate);

      orderDateTv.setText(dateString);
      String number = String
          .format(Locale.US, "%,d %s", order.getAmount() / 1000, context.getString(
              R.string.common_irr_currency));
      orderTotalPrice.setText(number);
      orderPaymentMethodTv.setText(order.getPaymentTypeTitle());
    }

    @Override
    public void onClick(View v) {
      switch (v.getId()) {
        case R.id.main_lay_linear:
        case R.id.main_lay:
          Bundle args = new Bundle();
          args.putLong(Constants.ORDER_ID, order.getId());
          args.putString(Constants.SALE_TYPE, saleType);
          args.putLong(Constants.VISIT_ID, visitId);
          args.putBoolean(Constants.READ_ONLY, false);
          setPageStatus(args);
          mainActivity.changeFragment(MainActivity.GOODS_LIST_FRAGMENT_ID, args, false);
          break;
      }
    }

    private void setPageStatus(Bundle args) {
      if (SaleOrderStatus.findById(order.getStatus()) == SaleOrderStatus.SENT
          || SaleOrderStatus.findById(order.getStatus()) == SaleOrderStatus.CANCELED
          || SaleOrderStatus.findById(order.getStatus()) == SaleOrderStatus.SENT_INVOICE
          || SaleOrderStatus.findById(order.getStatus()) == SaleOrderStatus.REJECTED_SENT) {
        args.putString(Constants.PAGE_STATUS, Constants.VIEW);
      } else {
        args.putString(Constants.PAGE_STATUS, Constants.EDIT);
      }
    }
  }
}
