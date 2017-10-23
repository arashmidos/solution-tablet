package com.parsroyal.solutiontablet.ui.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TextView;
import com.crashlytics.android.Crashlytics;
import com.parsroyal.solutiontablet.R;
import com.parsroyal.solutiontablet.constants.Constants;
import com.parsroyal.solutiontablet.constants.SaleOrderStatus;
import com.parsroyal.solutiontablet.data.entity.Goods;
import com.parsroyal.solutiontablet.data.model.GoodsDtoList;
import com.parsroyal.solutiontablet.data.model.SaleOrderDto;
import com.parsroyal.solutiontablet.data.model.SaleOrderItemDto;
import com.parsroyal.solutiontablet.exception.BusinessException;
import com.parsroyal.solutiontablet.exception.UnknownSystemException;
import com.parsroyal.solutiontablet.service.SaleOrderService;
import com.parsroyal.solutiontablet.service.impl.SaleOrderServiceImpl;
import com.parsroyal.solutiontablet.util.DialogUtil;
import com.parsroyal.solutiontablet.util.Empty;
import com.parsroyal.solutiontablet.util.Logger;
import com.parsroyal.solutiontablet.util.NumberUtil;
import com.parsroyal.solutiontablet.util.ToastUtil;
import java.util.List;

/**
 * Created by mahyar on 2/12/16.
 */
public class OldOrderItemsFragment extends BaseFragment {

  public static final String TAG = OldOrderItemsFragment.class.getSimpleName();

  private TableLayout dataTb;
  private List<SaleOrderItemDto> orderItems;
  private Long orderId;
  private boolean disabled;

  private SaleOrderService saleOrderService;
  private long orderStatus;
  private GoodsDtoList rejectedGoodsList;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    try {

      orderId = getArguments().getLong(Constants.ORDER_ID);
      disabled = getArguments().getBoolean(Constants.DISABLED);
      orderStatus = getArguments().getLong(Constants.ORDER_STATUS);
      rejectedGoodsList = (GoodsDtoList) getArguments().getSerializable(Constants.REJECTED_LIST);

      saleOrderService = new SaleOrderServiceImpl(getActivity());

      View view = getActivity().getLayoutInflater().inflate(R.layout.fragment_order_items, null);
      dataTb = (TableLayout) view.findViewById(R.id.orderItemsDataTable);
      if (isRejected()) {
        orderItems = saleOrderService.getLocalOrderItemDtoList(orderId, rejectedGoodsList);

      } else {
        orderItems = saleOrderService.getOrderItemDtoList(orderId);
      }

      updateItemsTable();

      return view;

    } catch (Exception e) {
      Crashlytics
          .log(Log.ERROR, "UI Exception", "Error in creating OldOrderItemsFragment " + e.getMessage());
      Log.e(TAG, e.getMessage(), e);
      ToastUtil.toastError(getActivity(), new UnknownSystemException(e));
      return inflater.inflate(R.layout.view_error_page, null);
    }
  }

  private void updateItemsTable() {
    dataTb.removeAllViews();
    for (final SaleOrderItemDto item : orderItems) {
      dataTb.addView(createOrderItemView(item));
    }
  }

  private View createOrderItemView(final SaleOrderItemDto item) {
    final Goods goods = item.getGoods();

    View itemView = getActivity().getLayoutInflater().inflate(R.layout.row_layout_order_item, null);
    TextView goodNameTv = (TextView) itemView.findViewById(R.id.goodNameTv);
    TextView goodAmountTv = (TextView) itemView.findViewById(R.id.goodAmountTv);
    TextView unit1CountTv = (TextView) itemView.findViewById(R.id.unit1CountTv);
    TextView unit2CountTv = (TextView) itemView.findViewById(R.id.unit2CountTv);
    TextView itemAmountTv = (TextView) itemView.findViewById(R.id.itemAmountTv);
    ImageButton editItemIb = (ImageButton) itemView.findViewById(R.id.editItemIb);
    ImageButton deleteItemIb = (ImageButton) itemView.findViewById(R.id.deleteItemIb);

    {
      if (Empty.isNotEmpty(goods)) {
        goodNameTv.setText(Empty.isNotEmpty(goods.getTitle()) ? goods.getTitle() : "--");

        Double price = Double.valueOf(goods.getPrice()) / 1000D;
        goodAmountTv.setText(NumberUtil.getCommaSeparated(price));
      }
    }
    {
      Double count = Double.valueOf(item.getGoodsCount()) / 1000D;
      unit1CountTv.setText(NumberUtil.formatDoubleWith2DecimalPlaces(count) + (
          Empty.isNotEmpty(goods.getUnit1Title()) ?
              goods.getUnit1Title() :
              isRejected() ? "" : "--"));
    }
    {
      if (Empty.isNotEmpty(goods.getUnit1Count()) && !goods.getUnit1Count().equals(0L)) {
        Double secondUnitCount =
            Double.valueOf(item.getGoodsCount()) / Double.valueOf(goods.getUnit1Count());
        secondUnitCount = secondUnitCount / 1000D;
        unit2CountTv.setText(
            NumberUtil.formatDoubleWith2DecimalPlaces(secondUnitCount) + (
                Empty.isNotEmpty(goods.getUnit2Title()) ?
                    goods.getUnit2Title() :
                    isRejected() ? "" : "--"));
      }
    }
    {
      Double itemAmount = Double.valueOf(item.getAmount()) / 1000D;
      itemAmountTv.setText(NumberUtil.getCommaSeparated(itemAmount));
    }

    if (disabled) {
      editItemIb.setVisibility(View.INVISIBLE);
      deleteItemIb.setVisibility(View.INVISIBLE);
    }

    editItemIb.setOnClickListener(v -> handleOnEditItemBtnClick(goods, item));

    deleteItemIb.setOnClickListener(v -> hadnleOnDeleteItemBtnClick(goods, item));

    return itemView;
  }

  private boolean isRejected() {

    return (orderStatus == SaleOrderStatus.REJECTED_DRAFT.getId() ||
        orderStatus == SaleOrderStatus.REJECTED.getId() ||
        orderStatus == SaleOrderStatus.REJECTED_SENT.getId());
  }

  private void hadnleOnDeleteItemBtnClick(final Goods goods, final SaleOrderItemDto item) {
    DialogUtil
        .showConfirmDialog(getActivity(), getActivity().getString(R.string.title_delete_order_item),
            getActivity().getString(R.string.message_are_you_sure), (dialog, which) -> {
              try {
                saleOrderService.deleteOrderItem(item.getId(), isRejected());

                if (orderStatus == SaleOrderStatus.REJECTED_DRAFT.getId()) {
                  goods.setExisting(goods.getExisting() + item.getGoodsCount());
                  orderItems = saleOrderService
                      .getLocalOrderItemDtoList(orderId, rejectedGoodsList);

                } else {
                  orderItems = saleOrderService.getOrderItemDtoList(orderId);
                }
                saleOrderService.updateOrderAmount(orderId);
                updateItemsTable();
              } catch (BusinessException ex) {
                Log.e(TAG, ex.getMessage(), ex);
                ToastUtil.toastError(getActivity(), ex);
              } catch (Exception ex) {
                Logger.sendError("Data Storage Exception",
                    "Error in deleting order items " + ex.getMessage());
                Log.e(TAG, ex.getMessage(), ex);
                ToastUtil.toastError(getActivity(), new UnknownSystemException(ex));
              }
            });
  }

  private void handleOnEditItemBtnClick(Goods goods, final SaleOrderItemDto item) {
    GoodsDetailDialogFragment goodsDetailDialog = new GoodsDetailDialogFragment();

    Bundle bundle = new Bundle();
    bundle.putLong("goodsBackendId", goods.getBackendId());
    Double count = Double.valueOf(item.getGoodsCount()) / 1000D;
    bundle.putLong("orderStatus", orderStatus);
    bundle.putSerializable("rejectedList", rejectedGoodsList);
    Long defaultUnit = item.getSelectedUnit();
    if (Empty.isNotEmpty(defaultUnit)) {
      bundle.putLong("selectedUnit", defaultUnit);
      if (defaultUnit == 2) {
        count = count / goods.getUnit1Count();
      }
    }

    bundle.putDouble("count", count);
    goodsDetailDialog.setArguments(bundle);
    goodsDetailDialog
        .setOnClickListener(
            (count1, selectedUnit) -> handleGoodsDialogConfirmBtn(count1, selectedUnit, item));

    goodsDetailDialog.show(getActivity().getSupportFragmentManager(), "GoodsDetailDialog");
  }

  private void handleGoodsDialogConfirmBtn(Double count, Long selectedUnit, SaleOrderItemDto item) {
    SaleOrderDto saleOrderDto = saleOrderService.findOrderDtoById(orderId);
    boolean isDeliverable = saleOrderDto.getStatus().equals(SaleOrderStatus.DELIVERABLE.getId());
    if (!disabled) {
      if (isDeliverable && Double.valueOf(count * 1000D).longValue() > item.getGoodsCount()) {
        ToastUtil.toastError(getActivity(), R.string.message_count_is_over_than_item_count);
      } else {
        try {
          saleOrderService
              .updateOrderItemCount(item.getId(), count, selectedUnit, saleOrderDto.getStatus(),
                  item.getGoods());
          saleOrderService.getOrderItemDtoList(orderId);
          saleOrderService.updateOrderAmount(orderId);

          if (orderStatus == SaleOrderStatus.REJECTED_DRAFT.getId()) {
            orderItems = saleOrderService.getLocalOrderItemDtoList(orderId, rejectedGoodsList);

          } else {
            orderItems = saleOrderService.getOrderItemDtoList(orderId);
          }

          updateItemsTable();
        } catch (BusinessException ex) {
          Log.e(TAG, ex.getMessage(), ex);
          ToastUtil.toastError(getActivity(), ex);
        } catch (Exception ex) {
          Logger.sendError("UI Exception",
              "Error in confirming order item detail " + ex.getMessage());
          Log.e(TAG, ex.getMessage(), ex);
          ToastUtil.toastError(getActivity(), new UnknownSystemException(ex));
        }
      }
    }
  }

  @Override
  public int getFragmentId() {
    return 0;
  }

}
