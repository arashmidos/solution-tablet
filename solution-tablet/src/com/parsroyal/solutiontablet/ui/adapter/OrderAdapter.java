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
import android.widget.ImageView;
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
  private Long status;

  public OrderAdapter(Context context, List<SaleOrderListModel> orders, boolean isFromOrder,
      Long visitId, String saleType, Long status) {
    this.context = context;
    this.visitId = visitId == null ? 0 : visitId;
    this.orders = orders;
    this.saleType = saleType;
    this.mainActivity = (MainActivity) context;
    this.isFromOrder = isFromOrder;
    this.status = status;
    inflater = LayoutInflater.from(context);
  }

  @Override
  public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view;
    if (status.equals(SaleOrderStatus.REJECTED.getId())) {
      view = inflater.inflate(R.layout.item_return_list, parent, false);
    } else if (isFromOrder) {
      view = inflater.inflate(R.layout.item_order_report_list, parent, false);
    } else {
      view = inflater.inflate(R.layout.item_order_list, parent, false);
    }
    return new ViewHolder(view);
  }

  @Override
  public void onBindViewHolder(ViewHolder holder, int position) {
    SaleOrderListModel order = orders.get(position);
    if (status.equals(SaleOrderStatus.REJECTED.getId())) {
      holder.setReturnData(position, order);
    } else {
      holder.setOrderData(position, order);
    }
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

    @Nullable
    @BindView(R.id.order_code_tv)
    TextView orderCodeTv;
    @Nullable
    @BindView(R.id.order_date_tv)
    TextView orderDateTv;
    @Nullable
    @BindView(R.id.order_total_price)
    TextView orderTotalPrice;
    @Nullable
    @BindView(R.id.order_count_tv)
    TextView orderCountTv;
    @Nullable
    @BindView(R.id.order_payment_method_tv)
    TextView orderPaymentMethodTv;
    @Nullable
    @BindView(R.id.main_lay_rel)
    RelativeLayout mainLayRel;
    @Nullable
    @BindView(R.id.main_lay_linear)
    LinearLayout mainLayLin;
    @Nullable
    @BindView(R.id.order_status_tv)
    TextView orderStatusTv;
    @Nullable
    @BindView(R.id.delete_img)
    ImageView deleteImg;
    @Nullable
    @BindView(R.id.edit_img)
    ImageView editImg;
    @Nullable
    @BindView(R.id.return_reason_tv)
    TextView returnReasonTv;
    @Nullable
    @BindView(R.id.total_amount_tv)
    TextView totalAmountTv;
    @Nullable
    @BindView(R.id.return_count_tv)
    TextView returnCountTv;
    @Nullable
    @BindView(R.id.return_date_tv)
    TextView returnDateTv;

    private int position;
    private SaleOrderListModel order;

    public ViewHolder(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);
      if (deleteImg != null) {
        deleteImg.setOnClickListener(this);
        editImg.setOnClickListener(this);
      }
      if (mainLayLin != null) {
        mainLayLin.setOnClickListener(this);
      } else if (mainLayRel != null) {
        mainLayRel.setOnClickListener(this);
      }
    }

    public void setOrderData(int position, SaleOrderListModel order) {
      this.position = position;
      this.order = order;
      if (isFromOrder) {
        orderStatusTv.setText(SaleOrderStatus.getDisplayTitle(context, order.getStatus()));
      }
      String orderCode = "کد سفارش : " + String.valueOf(order.getId());
      orderCodeTv.setText(orderCode);
      orderCountTv.setText("--");

      Date createdDate = DateUtil
          .convertStringToDate(order.getDate(), DateUtil.GLOBAL_FORMATTER, "FA");
      String dateString = DateUtil.getFullPersianDate(createdDate);

      orderDateTv.setText(dateString);
      String number = String
          .format(Locale.US, "%,d %s", order.getAmount() / 1000, context.getString(
              R.string.common_irr_currency));
      orderTotalPrice.setText(number);
      orderPaymentMethodTv.setText(order.getPaymentTypeTitle());
    }

    public void setReturnData(int position, SaleOrderListModel order) {
      this.position = position;
      this.order = order;
      String returnCode = "کد مرجوعی : " + String.valueOf(order.getId());
      returnCountTv.setText(String.valueOf(order.getAmount()));
      Date createdDate = DateUtil
          .convertStringToDate(order.getDate(), DateUtil.GLOBAL_FORMATTER, "FA");
      String dateString = DateUtil.getFullPersianDate(createdDate);

      returnDateTv.setText(dateString);
      String number = String
          .format(Locale.US, "%,d %s", order.getAmount() / 1000, context.getString(
              R.string.common_irr_currency));
      totalAmountTv.setText(number);
      returnReasonTv.setText(order.getDescription());
    }

    @Override
    public void onClick(View v) {
      switch (v.getId()) {
        case R.id.delete_img:
          //TODO:DELET ACTION
          break;
        case R.id.edit_img:
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
