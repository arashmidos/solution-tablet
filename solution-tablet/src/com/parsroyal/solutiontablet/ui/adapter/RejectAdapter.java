package com.parsroyal.solutiontablet.ui.adapter;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.Adapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Optional;
import com.mikepenz.crossfader.util.UIUtils;
import com.parsroyal.solutiontablet.R;
import com.parsroyal.solutiontablet.biz.impl.RejectedGoodsDataTransferBizImpl;
import com.parsroyal.solutiontablet.constants.Constants;
import com.parsroyal.solutiontablet.constants.SaleOrderStatus;
import com.parsroyal.solutiontablet.data.entity.Goods;
import com.parsroyal.solutiontablet.data.listmodel.SaleOrderListModel;
import com.parsroyal.solutiontablet.data.model.GoodsDtoList;
import com.parsroyal.solutiontablet.service.impl.SaleOrderServiceImpl;
import com.parsroyal.solutiontablet.ui.activity.MainActivity;
import com.parsroyal.solutiontablet.ui.adapter.RejectAdapter.ViewHolder;
import com.parsroyal.solutiontablet.util.DateUtil;
import com.parsroyal.solutiontablet.util.DialogUtil;
import com.parsroyal.solutiontablet.util.Empty;
import com.parsroyal.solutiontablet.util.MultiScreenUtility;
import com.parsroyal.solutiontablet.util.NumberUtil;
import com.parsroyal.solutiontablet.util.ToastUtil;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by Arash on 11/27/2017.
 */

public class RejectAdapter extends Adapter<ViewHolder> {

  private final SaleOrderServiceImpl saleOrderService;
  private LayoutInflater inflater;
  private Context context;
  private boolean isFromReport;
  private MainActivity mainActivity;
  private List<SaleOrderListModel> orders;
  private Long visitId;
  private int currentReject = -1;

  public RejectAdapter(Context context, List<SaleOrderListModel> orders, boolean isFromReport,
      Long visitId) {
    this.context = context;
    this.visitId = visitId == null ? 0 : visitId;
    this.orders = orders;
    this.mainActivity = (MainActivity) context;
    this.isFromReport = isFromReport;
    inflater = LayoutInflater.from(context);
    saleOrderService = new SaleOrderServiceImpl(mainActivity);
  }

  @NonNull
  @Override
  public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    View view = inflater.inflate(R.layout.item_return_list, parent, false);

    return new ViewHolder(view);
  }

  @Override
  public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
    SaleOrderListModel order = orders.get(position);

    holder.setReturnData(position, order);
  }

  @Override
  public int getItemCount() {
    return orders.size();
  }

  public void update(List<SaleOrderListModel> orders) {
    this.orders = orders;
    notifyDataSetChanged();
  }

  public void setRejectGoods(GoodsDtoList goodsDtoList) {
    DialogUtil.dismissProgressDialog();
    try {
      List<Goods> goodsList = goodsDtoList.getGoodsDtoList();
      if (currentReject != -1 && goodsList != null) {
        SaleOrderListModel tempOrder = orders.get(currentReject);
        Bundle args = new Bundle();
        args.putLong(Constants.ORDER_ID, tempOrder.getId());
        args.putSerializable(Constants.REJECTED_LIST, goodsDtoList);
        args.putLong(Constants.VISIT_ID, visitId);
        args.putBoolean(Constants.READ_ONLY, false);
        setPageStatus(tempOrder.getStatus(), args);

        mainActivity.changeFragment(MainActivity.GOODS_LIST_FRAGMENT_ID, args, false);
      }
    } catch (Exception ex) {
      ex.printStackTrace();
      ToastUtil
          .toastError(mainActivity, mainActivity.getString(R.string.err_reject_order_not_possible));
    }
  }

  private void setPageStatus(Long status, Bundle args) {
    if (SaleOrderStatus.Companion.findById(status) == SaleOrderStatus.SENT
        || SaleOrderStatus.Companion.findById(status) == SaleOrderStatus.CANCELED
        || SaleOrderStatus.Companion.findById(status) == SaleOrderStatus.SENT_INVOICE
        || SaleOrderStatus.Companion.findById(status) == SaleOrderStatus.REJECTED_SENT) {
      args.putString(Constants.PAGE_STATUS, Constants.VIEW);
    } else {
      args.putString(Constants.PAGE_STATUS, Constants.EDIT);
    }
  }

  public class ViewHolder extends RecyclerView.ViewHolder implements OnClickListener {

    @BindView(R.id.delete_img)
    ImageView deleteImg;
    @BindView(R.id.edit_img)
    ImageView editImg;
    @BindView(R.id.return_reason_tv)
    TextView returnReasonTv;
    @BindView(R.id.total_amount_tv)
    TextView totalAmountTv;
    @BindView(R.id.return_count_tv)
    TextView returnCountTv;
    @BindView(R.id.return_date_tv)
    TextView returnDateTv;
    @Nullable
    @BindView(R.id.customer_name_tv)
    TextView customerNameTv;
    @BindView(R.id.return_status_tv)
    TextView returnStatusTv;
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

    void setReturnData(int position, SaleOrderListModel order) {
      this.position = position;
      this.order = order;

      Date createdDate = DateUtil
          .convertStringToDate(order.getDate(), DateUtil.GLOBAL_FORMATTER, "FA");
      String dateString = DateUtil.getFullPersianDate(createdDate);
      if (isFromReport && !MultiScreenUtility.isTablet(context)) {//Mobile Report
        returnReasonTv // in report is really reason, in customer is reject count
            .setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_return_grey_18_dp, 0);
        returnReasonTv.setCompoundDrawablePadding((int) UIUtils.convertDpToPixel(8, context));
        returnReasonTv.setTextColor(ContextCompat.getColor(context, R.color.gray_75));
        returnReasonTv.setText(NumberUtil.digitsToPersian(String.valueOf(order.getOrderCount())));
        returnCountTv.setText(order.getCustomerName());
      } else {
        if (isFromReport && Empty.isNotEmpty(customerNameTv)) {//Tablet Report
          customerNameTv.setText(order.getCustomerName());
          customerNameTv.setVisibility(View.VISIBLE);
        }
        /*if (TextUtils.isEmpty(order.getDescription())) {//TODOO: Join with reject type
          returnReasonTv.setVisibility(View.GONE);
        } else {
          returnReasonTv.setText(order.getDescription());
        }*/
        returnCountTv
            .setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_return_grey_18_dp, 0);
        returnCountTv.setCompoundDrawablePadding((int) UIUtils.convertDpToPixel(8, context));
        returnCountTv.setText(NumberUtil.digitsToPersian(String.valueOf(order.getOrderCount())));
      }
      returnDateTv.setText(NumberUtil.digitsToPersian(dateString));
      String number = String
          .format(Locale.US, "%,d %s", order.getAmount() / 1000, context.getString(
              R.string.common_irr_currency));
      totalAmountTv.setText(NumberUtil.digitsToPersian(number));
      if (customerCodeTv != null) {
        customerCodeTv
            .setText(NumberUtil.digitsToPersian(String.format("(%s)", order.getCustomerCode())));
      }
      changeVisibility();
    }

    private void changeVisibility() {
      if (order.getStatus().equals(SaleOrderStatus.REJECTED_SENT.getId())) {
        if (!MultiScreenUtility.isTablet(context)) {
          editImg.setVisibility(View.GONE);
          deleteImg.setVisibility(View.GONE);
        } else {
          editImg.setVisibility(View.INVISIBLE);
          deleteImg.setVisibility(View.INVISIBLE);
        }

        returnStatusTv.setVisibility(View.VISIBLE);
      } else {
        returnStatusTv.setVisibility(View.GONE);
      }

      customerCodeTv.setVisibility(isFromReport ? View.VISIBLE : View.INVISIBLE);

      if (customerLayout != null) {
        customerLayout.setVisibility(isFromReport ? View.VISIBLE : View.INVISIBLE);
      }
    }

    @OnClick({R.id.delete_img, R.id.edit_img, R.id.return_main_lay, R.id.main_lay_rel})
    @Optional
    public void onClick(View v) {

      switch (v.getId()) {
        case R.id.delete_img:
          if (!order.getStatus().equals(SaleOrderStatus.REJECTED_SENT.getId())) {
            deleteOrder();
          }
          break;
        case R.id.edit_img:
        case R.id.return_main_lay:
        case R.id.main_lay_rel:
          currentReject = position;
          invokeGetRejectedData();
          break;
      }
    }

    private void invokeGetRejectedData() {
      DialogUtil
          .showProgressDialog(mainActivity, R.string.message_transferring_rejected_goods_data);

      new RejectedGoodsDataTransferBizImpl(mainActivity)
          .getAllRejectedData(order.getCustomerBackendId());
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
  }
}
