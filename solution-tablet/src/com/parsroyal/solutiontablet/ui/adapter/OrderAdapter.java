package com.parsroyal.solutiontablet.ui.adapter;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Optional;
import com.parsroyal.solutiontablet.R;
import com.parsroyal.solutiontablet.constants.Constants;
import com.parsroyal.solutiontablet.constants.SaleOrderStatus;
import com.parsroyal.solutiontablet.data.listmodel.SaleOrderListModel;
import com.parsroyal.solutiontablet.service.impl.SaleOrderServiceImpl;
import com.parsroyal.solutiontablet.ui.activity.MainActivity;
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
 * Created by Shakib on 8/27/2017.
 */

public class OrderAdapter extends Adapter<ViewHolder> {

  private final SaleOrderServiceImpl saleOrderService;
  private final boolean isComplimentary;
  private LayoutInflater inflater;
  private Context context;
  private boolean isFromReport;
  private MainActivity mainActivity;
  private List<SaleOrderListModel> orders;

  public OrderAdapter(Context context, List<SaleOrderListModel> orders, boolean isFromReport,
      boolean isComplimentary) {
    this.context = context;
    this.orders = orders;
    this.mainActivity = (MainActivity) context;
    this.isFromReport = isFromReport;
    this.isComplimentary = isComplimentary;
    inflater = LayoutInflater.from(context);
    saleOrderService = new SaleOrderServiceImpl(mainActivity);
  }

  @NonNull
  @Override
  public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    View view = inflater.inflate(R.layout.item_order_list, parent, false);

    return new ViewHolder(view);
  }

  @Override
  public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
    SaleOrderListModel order = orders.get(position);
    holder.setOrderData(position, order);
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
    @BindView(R.id.main_lay_rel)
    RelativeLayout mainLayRel;
    @Nullable
    @BindView(R.id.main_lay)
    LinearLayout mainLayLin;
    @BindView(R.id.order_status_tv)
    TextView orderStatusTv;
    @BindView(R.id.delete_img)
    ImageView deleteImg;
    @BindView(R.id.edit_img)
    ImageView editImg;
    @BindView(R.id.upload_img)
    ImageView uploadImg;
    @BindView(R.id.delete_img_layout)
    LinearLayout deleteImageLayout;
    @BindView(R.id.edit_img_layout)
    LinearLayout editImageLayout;
    @BindView(R.id.upload_img_layout)
    LinearLayout uploadImageLayout;
    @BindView(R.id.customer_name_tv)
    TextView customerNameTv;
    @BindView(R.id.customer_code_tv)
    TextView customerCodeTv;
    @Nullable
    @BindView(R.id.customer_layout)
    LinearLayout customerLayout;

    private int position;
    private SaleOrderListModel order;

    public ViewHolder(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);
    }

    void setOrderData(int position, SaleOrderListModel order) {
      this.position = position;
      this.order = order;

      String orderCode = "کد سفارش : " + NumberUtil.digitsToPersian(String.valueOf(order.getId()));
      orderCodeTv.setText(orderCode);
      customerNameTv.setText(order.getCustomerName());
      customerCodeTv
          .setText(NumberUtil.digitsToPersian(String.format("(%s)", order.getCustomerCode())));
      orderCountTv.setText(NumberUtil.digitsToPersian(String.valueOf(order.getOrderCount())));

      Date createdDate = DateUtil
          .convertStringToDate(order.getDate(), DateUtil.GLOBAL_FORMATTER, "FA");
      String dateString = DateUtil.getFullPersianDate(createdDate);

      orderDateTv.setText(NumberUtil.digitsToPersian(dateString));
      String number = String
          .format(Locale.US, "%,d %s", order.getAmount() / 1000, context.getString(
              R.string.common_irr_currency));
      orderTotalPrice.setText(NumberUtil.digitsToPersian(number));
      orderPaymentMethodTv.setText(
          isComplimentary ? "--" : NumberUtil.digitsToPersian(order.getPaymentTypeTitle()));

      changeVisibility();
    }

    private void changeVisibility() {
      if (order.getStatus().equals(SaleOrderStatus.SENT.getId()) ||
          order.getStatus().equals(SaleOrderStatus.FREE_ORDER_SENT.getId())) {
        if (!MultiScreenUtility.isTablet(context)) {
          editImageLayout.setVisibility(View.GONE);
          deleteImageLayout.setVisibility(View.GONE);
          uploadImageLayout.setVisibility(View.GONE);
        } else {
          editImageLayout.setVisibility(View.INVISIBLE);
          deleteImageLayout.setVisibility(View.INVISIBLE);
          uploadImageLayout.setVisibility(View.INVISIBLE);
        }

        orderStatusTv.setVisibility(View.VISIBLE);
      }

      if (isFromReport) {

        customerNameTv.setVisibility(View.VISIBLE);
        customerCodeTv.setVisibility(View.VISIBLE);
        if (customerLayout != null) {
          customerLayout.setVisibility(View.VISIBLE);
        }
      } else {
        uploadImageLayout.setVisibility(View.GONE);
        customerNameTv.setVisibility(View.GONE);
        customerCodeTv.setVisibility(View.GONE);
        if (customerLayout != null) {
          customerLayout.setVisibility(View.GONE);
        }
      }
    }

    @OnClick({R.id.delete_img_layout, R.id.edit_img_layout,
        R.id.main_lay, R.id.main_lay_rel, R.id.upload_img_layout})
    @Optional
    public void onClick(View v) {

      switch (v.getId()) {
        case R.id.delete_img_layout:
          if (!order.getStatus().equals(SaleOrderStatus.SENT.getId()) && !order.getStatus()
              .equals(SaleOrderStatus.FREE_ORDER_SENT.getId())) {
            deleteOrder();
          }
          break;
        case R.id.edit_img_layout:
        case R.id.main_lay_rel:
        case R.id.main_lay:
          Bundle args = new Bundle();
          args.putLong(Constants.ORDER_ID, order.getId());
          args.putLong(Constants.VISIT_ID, order.getVisitId());
          args.putBoolean(Constants.READ_ONLY, false);
          args.putBoolean(Constants.COMPLIMENTARY, isComplimentary);
          setPageStatus(args);
          mainActivity.changeFragment(MainActivity.GOODS_LIST_FRAGMENT_ID, args, false);
          break;
        case R.id.upload_img_layout:
          if (!order.getStatus().equals(SaleOrderStatus.SENT.getId()) && !order.getStatus()
              .equals(SaleOrderStatus.FREE_ORDER_SENT.getId())) {
            openSendDataDialog();
          }
          break;
      }
    }

    private void deleteOrder() {
      boolean isRejected = order.getStatus().equals(SaleOrderStatus.REJECTED.getId());
      DialogUtil.showConfirmDialog(mainActivity, mainActivity.getString(R.string.title_attention),
          mainActivity.getString(isRejected ? R.string.message_return_delete_confirm
              : R.string.message_order_delete_confirm), (dialog, which) -> {
            saleOrderService.deleteOrder(order.getId());
            orders.remove(position);
            notifyDataSetChanged();
          });
    }

    private void setPageStatus(Bundle args) {
      if (SaleOrderStatus.findById(order.getStatus()) == SaleOrderStatus.SENT
          || SaleOrderStatus.findById(order.getStatus()) == SaleOrderStatus.FREE_ORDER_SENT
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
