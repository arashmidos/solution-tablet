package com.parsroyal.solutiontablet.ui.adapter;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.text.TextUtils;
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
import butterknife.OnClick;
import butterknife.Optional;
import com.mikepenz.crossfader.util.UIUtils;
import com.parsroyal.solutiontablet.R;
import com.parsroyal.solutiontablet.constants.Constants;
import com.parsroyal.solutiontablet.constants.SaleOrderStatus;
import com.parsroyal.solutiontablet.data.listmodel.SaleOrderListModel;
import com.parsroyal.solutiontablet.service.impl.SaleOrderServiceImpl;
import com.parsroyal.solutiontablet.ui.MainActivity;
import com.parsroyal.solutiontablet.ui.adapter.OrderAdapter.ViewHolder;
import com.parsroyal.solutiontablet.ui.fragment.dialogFragment.SingleDataTransferDialogFragment;
import com.parsroyal.solutiontablet.util.DateUtil;
import com.parsroyal.solutiontablet.util.DialogUtil;
import com.parsroyal.solutiontablet.util.MultiScreenUtility;
import com.parsroyal.solutiontablet.util.NumberUtil;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by ShakibIsTheBest on 8/27/2017.
 */

public class OrderAdapter extends Adapter<ViewHolder> {

  private final SaleOrderServiceImpl saleOrderService;
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
    saleOrderService = new SaleOrderServiceImpl(mainActivity);

  }

  @Override
  public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view;
    if (isRejected()) {
      view = inflater.inflate(R.layout.item_return_list, parent, false);
    } else {
      view = inflater.inflate(R.layout.item_order_report_list, parent, false);
    }
    return new ViewHolder(view);
  }

  protected boolean isRejected() {
    return (status.equals(SaleOrderStatus.REJECTED_DRAFT.getId()) ||
        status.equals(SaleOrderStatus.REJECTED.getId()) ||
        status.equals(SaleOrderStatus.REJECTED_SENT.getId()));
  }

  @Override
  public void onBindViewHolder(ViewHolder holder, int position) {
    SaleOrderListModel order = orders.get(position);
    if (isRejected()) {
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
    @BindView(R.id.upload_img)
    ImageView uploadImg;
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
    @Nullable
    @BindView(R.id.customer_name_tv)
    TextView customerNameTv;
    @Nullable
    @BindView(R.id.return_status_tv)
    TextView returnStatusTv;

    private int position;
    private SaleOrderListModel order;

    public ViewHolder(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);

      if (mainLayLin != null) {
        mainLayLin.setOnClickListener(this);
      } else if (mainLayRel != null) {
        mainLayRel.setOnClickListener(this);
      }
    }

    public void setOrderData(int position, SaleOrderListModel order) {
      this.position = position;
      this.order = order;
      if (!isFromOrder) {
        orderStatusTv.setVisibility(View.GONE);
        if (customerNameTv != null) {//TODO SHAKIB: FIX THIS
          customerNameTv.setVisibility(View.GONE);
        }
      } else {
        if (customerNameTv != null) {//TODO SHAKIB: FIX THIS
          customerNameTv.setVisibility(View.VISIBLE);
        }
      }
      String orderCode = "کد سفارش : " + NumberUtil.digitsToPersian(String.valueOf(order.getId()));
      orderCodeTv.setText(orderCode);
      if (customerNameTv != null) {//TODO SHAKIB: FIX THIS
        customerNameTv.setText(order.getCustomerName());
      }
      orderCountTv.setText(NumberUtil.digitsToPersian(String.valueOf(order.getOrderCount())));

      Date createdDate = DateUtil
          .convertStringToDate(order.getDate(), DateUtil.GLOBAL_FORMATTER, "FA");
      String dateString = DateUtil.getFullPersianDate(createdDate);

      orderDateTv.setText(NumberUtil.digitsToPersian(dateString));
      String number = String
          .format(Locale.US, "%,d %s", order.getAmount() / 1000, context.getString(
              R.string.common_irr_currency));
      orderTotalPrice.setText(NumberUtil.digitsToPersian(number));
      orderPaymentMethodTv.setText(NumberUtil.digitsToPersian(order.getPaymentTypeTitle()));
      if (order.getStatus().equals(SaleOrderStatus.SENT.getId())) {
        if (isFromOrder) {
          orderStatusTv.setVisibility(View.VISIBLE);
        } else {
          orderStatusTv.setVisibility(View.GONE);
        }
        if (!MultiScreenUtility.isTablet(context)) {
          editImg.setVisibility(View.GONE);
          deleteImg.setVisibility(View.GONE);
          uploadImg.setVisibility(View.GONE);
        } else {
          editImg.setVisibility(View.INVISIBLE);
          deleteImg.setVisibility(View.INVISIBLE);
          uploadImg.setVisibility(View.INVISIBLE);
        }
      }
    }

    public void setReturnData(int position, SaleOrderListModel order) {
      this.position = position;
      this.order = order;

      Date createdDate = DateUtil
          .convertStringToDate(order.getDate(), DateUtil.GLOBAL_FORMATTER, "FA");
      String dateString = DateUtil.getFullPersianDate(createdDate);
      if (isFromOrder && !MultiScreenUtility.isTablet(context)) {
        returnReasonTv
            .setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_return_grey_18_dp, 0);
        returnReasonTv.setCompoundDrawablePadding((int) UIUtils.convertDpToPixel(8, context));
        returnReasonTv.setTextColor(ContextCompat.getColor(context, R.color.black_de));
        returnReasonTv.setText("--");//TODO: Add Return items count
        returnCountTv.setText(order.getCustomerName());
      } else {
        if (isFromOrder) {
          customerNameTv.setText(order.getCustomerName());
          customerNameTv.setVisibility(View.VISIBLE);
        }
        if (TextUtils.isEmpty(order.getDescription())) {
          returnReasonTv.setVisibility(View.GONE);
        } else {
          returnReasonTv.setText(order.getDescription());
        }
        returnCountTv
            .setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_return_grey_18_dp, 0);
        returnCountTv.setCompoundDrawablePadding((int) UIUtils.convertDpToPixel(8, context));
        returnCountTv.setText("--");//TODO: Add Return items count
      }
      returnDateTv.setText(NumberUtil.digitsToPersian(dateString));
      String number = String
          .format(Locale.US, "%,d %s", order.getAmount() / 1000, context.getString(
              R.string.common_irr_currency));
      totalAmountTv.setText(NumberUtil.digitsToPersian(number));

      if (order.getStatus().equals(SaleOrderStatus.REJECTED_SENT.getId())) {
        if (isFromOrder) {
          returnStatusTv.setVisibility(View.VISIBLE);
        } else {
          returnStatusTv.setVisibility(View.GONE);
        }
        if (!MultiScreenUtility.isTablet(context)) {
          editImg.setVisibility(View.GONE);
          deleteImg.setVisibility(View.GONE);
        } else {
          editImg.setVisibility(View.INVISIBLE);
          deleteImg.setVisibility(View.INVISIBLE);
        }
      }
    }

    @OnClick({R.id.delete_img, R.id.delete_img_layout, R.id.edit_img, R.id.edit_img_layout,
        R.id.main_lay, R.id.main_lay_linear, R.id.upload_img})
    @Optional
    public void onClick(View v) {

      switch (v.getId()) {
        case R.id.delete_img_layout:
        case R.id.delete_img:
          if (!order.getStatus().equals(SaleOrderStatus.SENT.getId())) {
            deleteOrder();
          }
          break;
        case R.id.edit_img_layout:
        case R.id.edit_img:
        case R.id.main_lay_linear:
        case R.id.main_lay:
          if (!order.getStatus().equals(SaleOrderStatus.SENT.getId())) {
            Bundle args = new Bundle();
            args.putLong(Constants.ORDER_ID, order.getId());
            args.putString(Constants.SALE_TYPE, saleType);
            args.putLong(Constants.VISIT_ID, order.getVisitId());
            args.putBoolean(Constants.READ_ONLY, false);
            setPageStatus(args);
            mainActivity.changeFragment(MainActivity.GOODS_LIST_FRAGMENT_ID, args, false);
          }
          break;
        case R.id.upload_img:
          openSendDataDialog();
          break;
      }
    }

    private void deleteOrder() {
      boolean isRejected = order.getStatus().equals(SaleOrderStatus.REJECTED.getId());
      DialogUtil.showConfirmDialog(mainActivity, mainActivity.getString(R.string.title_attention),
          mainActivity.getString(isRejected ? R.string.message_return_delete_confirm
              : R.string.message_order_delete_confirm), (dialog, which) ->
          {
            saleOrderService.deleteOrder(order.getId());
            orders.remove(position);
            notifyDataSetChanged();
          });
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


    private void openSendDataDialog() {
      FragmentTransaction ft = mainActivity.getSupportFragmentManager().beginTransaction();
      Bundle args = new Bundle();
      args.putLong(Constants.VISIT_ID, order.getVisitId());
      SingleDataTransferDialogFragment singleDataTransferDialogFragment = SingleDataTransferDialogFragment
          .newInstance(args);
      singleDataTransferDialogFragment.show(ft, "data_transfer");
    }
  }
}
