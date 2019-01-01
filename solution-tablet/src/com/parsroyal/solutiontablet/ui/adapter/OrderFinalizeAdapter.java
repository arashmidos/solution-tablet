package com.parsroyal.solutiontablet.ui.adapter;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.bumptech.glide.Glide;
import com.parsroyal.solutiontablet.R;
import com.parsroyal.solutiontablet.constants.Constants;
import com.parsroyal.solutiontablet.constants.SaleOrderStatus;
import com.parsroyal.solutiontablet.constants.StatusCodes;
import com.parsroyal.solutiontablet.data.entity.Goods;
import com.parsroyal.solutiontablet.data.event.ErrorEvent;
import com.parsroyal.solutiontablet.data.event.SuccessEvent;
import com.parsroyal.solutiontablet.data.model.GoodsDtoList;
import com.parsroyal.solutiontablet.data.model.SaleOrderDto;
import com.parsroyal.solutiontablet.data.model.SaleOrderItemDto;
import com.parsroyal.solutiontablet.exception.BusinessException;
import com.parsroyal.solutiontablet.exception.UnknownSystemException;
import com.parsroyal.solutiontablet.service.impl.SaleOrderServiceImpl;
import com.parsroyal.solutiontablet.ui.activity.MainActivity;
import com.parsroyal.solutiontablet.ui.adapter.OrderFinalizeAdapter.ViewHolder;
import com.parsroyal.solutiontablet.ui.fragment.bottomsheet.AddOrderBottomSheet;
import com.parsroyal.solutiontablet.ui.fragment.dialogFragment.AddOrderDialogFragment;
import com.parsroyal.solutiontablet.ui.fragment.dialogFragment.FinalizeOrderDialogFragment;
import com.parsroyal.solutiontablet.util.DialogUtil;
import com.parsroyal.solutiontablet.util.Empty;
import com.parsroyal.solutiontablet.util.Logger;
import com.parsroyal.solutiontablet.util.MediaUtil;
import com.parsroyal.solutiontablet.util.MultiScreenUtility;
import com.parsroyal.solutiontablet.util.NumberUtil;
import com.parsroyal.solutiontablet.util.SaleUtil;
import com.parsroyal.solutiontablet.util.ToastUtil;
import java.util.List;
import org.greenrobot.eventbus.EventBus;
import timber.log.Timber;

/**
 * Created by ShakibIsTheBest on 8/4/2017.
 */

public class OrderFinalizeAdapter extends Adapter<ViewHolder> {

  private final SaleOrderDto order;
  private final List<Goods> rejectedGoodsList;
  private final SaleOrderServiceImpl saleOrderService;
  private final FinalizeOrderDialogFragment parent;
  private final GoodsDtoList goodsDtoList;
  private LayoutInflater inflater;
  private MainActivity mainActivity;
  private String pageStatus;
  private List<SaleOrderItemDto> orderItems;
  private AddOrderDialogFragment addOrderDialogFragment;

  public OrderFinalizeAdapter(FinalizeOrderDialogFragment finalizeOrderDialogFragment,
      MainActivity mainActivity, SaleOrderDto order, GoodsDtoList goodsDtoList, String pageStatus) {
    this.parent = finalizeOrderDialogFragment;
    this.mainActivity = mainActivity;
    this.order = order;
    this.pageStatus = pageStatus;
    this.orderItems = order.getOrderItems();
    inflater = LayoutInflater.from(mainActivity);
    this.goodsDtoList = goodsDtoList;
    this.rejectedGoodsList = Empty.isNotEmpty(goodsDtoList) ? goodsDtoList.getGoodsDtoList() : null;
    this.saleOrderService = new SaleOrderServiceImpl(mainActivity);
  }

  @NonNull
  @Override
  public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

    View view = inflater.inflate(SaleUtil.isDelivery(order.getStatus()) ?
        R.layout.item_deliver_list_finalize :
        R.layout.item_order_list_finilize, parent, false);
    return new ViewHolder(view);
  }

  @Override
  public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
    SaleOrderItemDto item = orderItems.get(position);
    if (isRejected(order.getStatus())) {
      item.setGoods(findGoods(item));
    }
    holder.setData(item, position);
  }

  private boolean isRejected(Long orderStatus) {
    return (orderStatus.equals(SaleOrderStatus.REJECTED_DRAFT.getId()) ||
        orderStatus.equals(SaleOrderStatus.REJECTED.getId()) ||
        orderStatus.equals(SaleOrderStatus.REJECTED_SENT.getId()));
  }

  private Goods findGoods(SaleOrderItemDto item) {
    Long goodsBackendId = item.getGoodsBackendId();
    for (int i = 0; i < rejectedGoodsList.size(); i++) {//TODOO:
      Goods goods = rejectedGoodsList.get(i);
      if (goods.getBackendId().equals(goodsBackendId)) {
        return goods;
      }
    }
    return null;
  }

  @Override
  public int getItemCount() {
    return orderItems.size();
  }

  public void update(List<SaleOrderItemDto> orderItems) {
    this.orderItems = orderItems;
    notifyDataSetChanged();
  }

  private boolean isDelivery() {
    return SaleUtil.isDelivery(order.getStatus());
  }

  public class ViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.good_img)
    ImageView goodImg;
    @BindView(R.id.good_title_tv)
    TextView goodTitleTv;
    @BindView(R.id.amount_tv)
    TextView amountTv;
    @BindView(R.id.total_amount_tv)
    TextView totalAmountTv;
    @BindView(R.id.delete_img)
    ImageView deleteImg;
    @BindView(R.id.edit_img)
    ImageView editImg;
    @BindView(R.id.count_tv)
    TextView countTv;
    @BindView(R.id.unit2_count_tv)
    TextView unit2CountTv;
    @BindView(R.id.unit1_title_tv)
    TextView unit1TitleTv;
    @BindView(R.id.unit2_title_tv)
    TextView unit2TitleTv;
    @BindView(R.id.root_layout)
    RelativeLayout rootLayout;
    @Nullable
    @BindView(R.id.inc_amount_tv)
    TextView incAmountTv;
    @Nullable
    @BindView(R.id.dec_amount_tv)
    TextView decAmountTv;

    private SaleOrderItemDto item;
    private int position;
    private Goods goods;

    public ViewHolder(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);
    }

    public void setData(SaleOrderItemDto item, int position) {
      if (item.getGoods() == null) {
        ToastUtil.toastError(mainActivity, R.string.error_order_incomplete_not_deliverable);
        parent.close();
        return;
      }
      Glide.with(mainActivity)
          .load(MediaUtil.getGoodImage(item.getGoods().getCode()))
          .error(Glide.with(mainActivity).load(R.drawable.goods_default))
          .into(goodImg);
      goodTitleTv.setText(NumberUtil.digitsToPersian(item.getGoods().getTitle()));
      Double goodsAmount = Double.valueOf(item.getGoods().getPrice()) / 1000D;
      amountTv.setText(String.format("%s %s", NumberUtil.digitsToPersian(
          NumberUtil.getCommaSeparated(goodsAmount)),
          mainActivity.getString(R.string.common_irr_currency)));

      Double totalAmount = Double.valueOf(item.getAmount()) / 1000D;
      if (isDelivery() && totalAmount == 0.0) {
        editImg.setVisibility(View.GONE);
        deleteImg.setVisibility(View.GONE);
        rootLayout.setBackgroundColor(ContextCompat.getColor(mainActivity, R.color.gift));
      } else {
        editImg.setVisibility(View.VISIBLE);
        deleteImg.setVisibility(View.VISIBLE);
        rootLayout.setBackgroundColor(ContextCompat.getColor(mainActivity, R.color.white));
      }
      totalAmountTv.setText(String.format("%s %s",
          NumberUtil.digitsToPersian(NumberUtil.getCommaSeparated(totalAmount)),
          mainActivity.getString(R.string.common_irr_currency)));
      countTv.setText(NumberUtil.formatPersian3DecimalPlaces(item.getGoodsCount() / 1000.0));
      unit2CountTv
          .setText(NumberUtil.formatPersian3DecimalPlaces(item.getGoodsUnit2Count() / 1000.0));
      String unit1Title = item.getGoods().getUnit1Title();
      unit1TitleTv
          .setText(Empty.isEmpty(unit1Title) ? "--" : NumberUtil.digitsToPersian(unit1Title));
      String unit2Title = item.getGoods().getUnit2Title();
      unit2TitleTv
          .setText(Empty.isEmpty(unit2Title) ? "--" : NumberUtil.digitsToPersian(unit2Title));
      this.item = item;
      this.goods = item.getGoods();
      this.position = position;
      if (pageStatus.equals(Constants.VIEW)) {
        editImg.setVisibility(View.GONE);
        deleteImg.setVisibility(View.GONE);
      }
      if (isDelivery() && incAmountTv != null && decAmountTv != null) {
        incAmountTv.setText(String.format("%s %s",
            NumberUtil.digitsToPersian(NumberUtil.getCommaSeparated(item.getOrdInc())),
            mainActivity.getString(R.string.common_irr_currency)));
        decAmountTv.setText(String.format("%s %s",
            NumberUtil.digitsToPersian(NumberUtil.getCommaSeparated(item.getOrdDec())),
            mainActivity.getString(R.string.common_irr_currency)));
      }
    }

    @OnClick({R.id.edit_img, R.id.delete_img})
    public void onClick(View view) {
      switch (view.getId()) {
        case R.id.edit_img:
          if (isDelivery()) {
            showEditConfirm();
          } else {
            editItem();
          }
          break;
        case R.id.delete_img:
          showConfirmDelete();
          break;
      }
    }

    private void showEditConfirm() {
      DialogUtil
          .showConfirmDialog(mainActivity, mainActivity.getString(R.string.message_are_you_sure),
              mainActivity.getString(R.string.message_edit_removes_gift),
              (dialog, which) -> {
                removeAllGifts();
                parent.setOrderChanged();
                editItem();
              });
    }

    private void removeAllGifts() {
      try {
        for (SaleOrderItemDto orderItem : orderItems) {
          if (orderItem.getAmount() == 0) {
            saleOrderService.deleteOrderItem(orderItem.getId(), false);
          }
        }

        orderItems = saleOrderService.getOrderItemDtoList(order.getId());

        saleOrderService.updateOrderAmount(order.getId());

        notifyDataSetChanged();
        parent.updateList();

      } catch (BusinessException ex) {
        Timber.e(ex);
        ToastUtil.toastError(mainActivity, ex);
      } catch (Exception ex) {
        Timber.e(ex);
        ToastUtil.toastError(mainActivity, new UnknownSystemException(ex));
      }
    }

    private void showConfirmDelete() {
      DialogUtil
          .showConfirmDialog(mainActivity, mainActivity.getString(R.string.title_delete_order_item),
              mainActivity.getString(SaleOrderStatus.DELIVERABLE.getId().equals(order.getStatus()) ?
                  R.string.message_are_you_sure_not_recoverable :
                  R.string.message_are_you_sure), (dialog, which) -> {
                if (isDelivery()) {
                  removeAllGifts();
                  parent.setOrderChanged();
                }
                deleteItem();
              });
    }

    private void deleteItem() {
      try {
        saleOrderService.deleteOrderItem(item.getId(), isRejected(order.getStatus()));

        if (isRejected(order.getStatus())) {
          goods.setExisting(goods.getExisting() + item.getGoodsCount());
          orderItems = saleOrderService
              .getLocalOrderItemDtoList(order.getId(), goodsDtoList);
        } else {
          orderItems = saleOrderService.getOrderItemDtoList(order.getId());
        }
        saleOrderService.updateOrderAmount(order.getId());
        notifyDataSetChanged();
        parent.updateList();

      } catch (BusinessException ex) {
        Timber.e(ex);
        ToastUtil.toastError(mainActivity, ex);
      } catch (Exception ex) {
        Timber.e(ex);
        ToastUtil.toastError(mainActivity, new UnknownSystemException(ex));
      }
    }

    /*
  @return true if it's one of the REJECTED states
   */
    private boolean isRejected(Long orderStatus) {
      return (orderStatus.equals(SaleOrderStatus.REJECTED_DRAFT.getId()) ||
          orderStatus.equals(SaleOrderStatus.REJECTED.getId()) ||
          orderStatus.equals(SaleOrderStatus.REJECTED_SENT.getId()));

    }

    private void editItem() {

      FragmentTransaction ft = mainActivity.getSupportFragmentManager().beginTransaction();
      if (MultiScreenUtility.isTablet(mainActivity)) {
        addOrderDialogFragment = AddOrderBottomSheet.newInstance();
      } else {
        addOrderDialogFragment = AddOrderDialogFragment.newInstance();
      }
      Bundle bundle = new Bundle();
      bundle.putLong(Constants.GOODS_BACKEND_ID, goods.getBackendId());
      Double count = Double.valueOf(item.getGoodsCount()) / 1000D;
      bundle.putLong(Constants.ORDER_STATUS, order.getStatus());
      bundle.putSerializable(Constants.REJECTED_LIST, goodsDtoList);
      Long defaultUnit = item.getSelectedUnit();
      if (Empty.isNotEmpty(defaultUnit)) {
        bundle.putLong(Constants.SELECTED_UNIT, defaultUnit);
        if (defaultUnit == 2) {
          count = count / goods.getUnit1Count();
        }
      }
      bundle.putLong(Constants.DISCOUNT, item.getDiscount());

      bundle.putDouble(Constants.COUNT, count);
//      bundle.putLong(Constants.GOODS_INVOICE_ID, invoiceBackendId);

      addOrderDialogFragment.setArguments(bundle);
      addOrderDialogFragment.setOnClickListener(
          (goodsCount, selectedUnit, discount) -> handleGoodsDialogConfirmBtn(goodsCount,
              selectedUnit, item, goods, discount));

      addOrderDialogFragment.show(ft, "order");
    }

    private void handleGoodsDialogConfirmBtn(Double count, Long selectedUnit, SaleOrderItemDto item,
        Goods goods, Long discount) {
      try {

        saleOrderService.updateOrderItemCount(
            item.getId(), count, selectedUnit, order.getStatus(), goods, discount);
        Long orderAmount = saleOrderService.updateOrderAmount(order.getId());
        order.setOrderItems(saleOrderService.getOrderItemDtoList(order.getId()));
        order.setAmount(orderAmount);

        if (order.getStatus().equals(SaleOrderStatus.REJECTED_DRAFT.getId())) {
          orderItems = saleOrderService.getLocalOrderItemDtoList(order.getId(), goodsDtoList);
        } else {
          orderItems = saleOrderService.getOrderItemDtoList(order.getId());
        }

        item = orderItems.get(position);
        setData(item, position);
        notifyItemChanged(position);
        parent.updateList();
        EventBus.getDefault().post(new SuccessEvent(StatusCodes.SUCCESS));
      } catch (BusinessException ex) {
        EventBus.getDefault()
            .post(new ErrorEvent(mainActivity.getString(R.string.exceed_count_exception),
                StatusCodes.DATA_STORE_ERROR));
        Timber.e(ex);
        ToastUtil.toastError(mainActivity, ex);
      } catch (Exception ex) {
        Logger.sendError("Data storage Exception",
            "Error in confirming GoodsList " + ex.getMessage());
        Timber.e(ex);
        ToastUtil.toastError(mainActivity, new UnknownSystemException(ex));
      }
    }
  }
}
