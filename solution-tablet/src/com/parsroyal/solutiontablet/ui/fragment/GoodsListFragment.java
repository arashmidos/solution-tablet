package com.parsroyal.solutiontablet.ui.fragment;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.crashlytics.android.Crashlytics;
import com.marshalchen.ultimaterecyclerview.UltimateRecyclerView;
import com.parsroyal.solutiontablet.R;
import com.parsroyal.solutiontablet.constants.Constants;
import com.parsroyal.solutiontablet.constants.SaleOrderStatus;
import com.parsroyal.solutiontablet.data.entity.Goods;
import com.parsroyal.solutiontablet.data.entity.SaleOrderItem;
import com.parsroyal.solutiontablet.data.model.GoodsDtoList;
import com.parsroyal.solutiontablet.data.model.SaleOrderDto;
import com.parsroyal.solutiontablet.data.searchobject.GoodsSo;
import com.parsroyal.solutiontablet.exception.BusinessException;
import com.parsroyal.solutiontablet.exception.SaleOrderItemCountExceedExistingException;
import com.parsroyal.solutiontablet.exception.UnknownSystemException;
import com.parsroyal.solutiontablet.service.GoodsService;
import com.parsroyal.solutiontablet.service.SaleOrderService;
import com.parsroyal.solutiontablet.service.impl.GoodsServiceImpl;
import com.parsroyal.solutiontablet.service.impl.SaleOrderServiceImpl;
import com.parsroyal.solutiontablet.ui.adapter.GoodListAdapter;
import com.parsroyal.solutiontablet.util.Analytics;
import com.parsroyal.solutiontablet.util.CharacterFixUtil;
import com.parsroyal.solutiontablet.util.DateUtil;
import com.parsroyal.solutiontablet.util.Empty;
import com.parsroyal.solutiontablet.util.ToastUtil;
import java.util.ArrayList;
import java.util.List;
import jp.wasabeef.recyclerview.animators.FadeInRightAnimator;

/**
 * Created by mahyar on 2/12/16.
 */
public class GoodsListFragment extends BaseFragment {

  public static final String TAG = GoodsListFragment.class.getSimpleName();
  @BindView(R.id.list)
  UltimateRecyclerView list;
  @BindView(R.id.tvPaymentTime)
  TextView tvPaymentTime;
  @BindView(R.id.searchTxt)
  EditText searchTxt;

  private List<Goods> goodsList;
  private GoodsService goodsService;
  private GoodsSo goodsSo = new GoodsSo();
  private Long orderId;
  private SaleOrderDto order;

  private SaleOrderService saleOrderService;

  private GoodListAdapter adapter;
  private Long orderStatus;
  private GoodsDtoList rejectedGoodsList;
  private String constraint;
  private boolean isViewAll;
  private boolean readOnly;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    try {
      View view = getActivity().getLayoutInflater().inflate(R.layout.fragment_goods_list, null);
      ButterKnife.bind(this, view);

      goodsService = new GoodsServiceImpl(getActivity());
      saleOrderService = new SaleOrderServiceImpl(getActivity());

      isViewAll = getArguments().getBoolean("view_all", false);
      if (!isViewAll) {

        orderId = getArguments().getLong(Constants.ORDER_ID, -1);

        if (orderId != -1) {
          order = saleOrderService.findOrderDtoById(orderId);
          orderStatus = order.getStatus();
          readOnly = false;
        } else {
          readOnly = true;
        }
      } else {
        readOnly = true;
      }

      list.setHasFixedSize(true);
      list.setLayoutManager(new LinearLayoutManager(getActivity()));
      list.setItemAnimator(new FadeInRightAnimator());

      if (SaleOrderStatus.REJECTED_DRAFT.getId().equals(orderStatus)) {
        tvPaymentTime.setVisibility(View.GONE);
        rejectedGoodsList = (GoodsDtoList) getArguments().getSerializable(Constants.REJECTED_LIST);

        adapter = new GoodListAdapter(this, rejectedGoodsList.getGoodsDtoList(), readOnly);
      } else {
        goodsList = goodsService.searchForGoodsList(goodsSo);
        adapter = new GoodListAdapter(this, goodsList, readOnly);
      }

      list.setAdapter(adapter);

      searchTxt.addTextChangedListener(new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
          constraint = s.toString();

          if (Empty.isNotEmpty(constraint)) {
            goodsSo.setConstraint(CharacterFixUtil.fixString("%" + constraint + "%"));
            Analytics.logSearch(constraint, "Type", "Goods");
            updateGoodsDataTb();
          } else {
            goodsSo.setConstraint(null);
            updateGoodsDataTb();
          }
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
      });

      return view;
    } catch (Exception e) {
      Crashlytics
          .log(Log.ERROR, "UI Exception", "Error in creating GoodsListFragment " + e.getMessage());
      Log.e(TAG, e.getMessage(), e);
      ToastUtil.toastError(getActivity(), new UnknownSystemException(e));
      return inflater.inflate(R.layout.view_error_page, null);
    }
  }

  private void updateGoodsDataTb() {

    if (SaleOrderStatus.REJECTED_DRAFT.getId().equals(orderStatus)) {
      if (Empty.isNotEmpty(rejectedGoodsList)) {
        List<Goods> goodsList = rejectedGoodsList.getGoodsDtoList();
        List<Goods> filteredList = new ArrayList<>();
        for (int i = 0; i < goodsList.size(); i++) {
          Goods good = goodsList.get(i);
          if (Empty.isNotEmpty(constraint) && !good.getTitle().equals(constraint)) {
            continue;
          }
          filteredList.add(good);
        }
        list.hideEmptyView();
        adapter.filter(filteredList);
      } else {
        list.showEmptyView();
      }
    } else {
      goodsList = goodsService.searchForGoodsList(goodsSo);
      if (Empty.isNotEmpty(goodsList)) {
        list.hideEmptyView();
        adapter.filter(goodsList);
      } else {
        list.showEmptyView();
      }
    }
  }

  public void handleOnGoodsItemClickListener(final Goods goods) {
    try {
      GoodsDetailDialogFragment goodsDetailDialog = new GoodsDetailDialogFragment();

      Long invoiceBackendId = 0L;
      if (SaleOrderStatus.REJECTED_DRAFT.getId().equals(orderStatus)) {
        invoiceBackendId = goods.getInvoiceBackendId();
      }
      final SaleOrderItem item = saleOrderService.findOrderItemByOrderIdAndGoodsBackendId(
          order.getId(), goods.getBackendId(), invoiceBackendId);

      Bundle bundle = new Bundle();
      bundle.putLong(Constants.GOODS_BACKEND_ID, goods.getBackendId());
      bundle.putLong(Constants.GOODS_INVOICE_ID, invoiceBackendId);
      bundle.putLong(Constants.ORDER_STATUS, orderStatus);
      bundle.putLong(Constants.GOODS_SALE_RATE, goods.getSaleRate());
      bundle.putSerializable(Constants.REJECTED_LIST, rejectedGoodsList);

      long defaultUnit = goods.getDefaultUnit() != null ? goods.getDefaultUnit() : 1;
      if (Empty.isNotEmpty(item)) {
        Double count = item.getGoodsCount() / 1000D;

        if (Empty.isNotEmpty(item.getSelectedUnit())) {
          defaultUnit = item.getSelectedUnit();
          if (defaultUnit == 2) {
            count = count / goods.getUnit1Count();
          }
        }
        bundle.putDouble(Constants.COUNT, count);
      }

      bundle.putLong(Constants.SELECTED_UNIT, defaultUnit);

      goodsDetailDialog.setArguments(bundle);
      goodsDetailDialog.setOnClickListener((count, selectedUnit) -> {
        handleGoodsDialogConfirmBtn(count, selectedUnit, item, goods);
        updateGoodsDataTb();
      });

      goodsDetailDialog.show(getActivity().getSupportFragmentManager(), "GoodsDetailDialog");
    } catch (Exception e) {
      Crashlytics.log(Log.ERROR, "Data Storage Exception",
          "Error in confirming handling GoodsList " + e.getMessage());
      Log.e(TAG, e.getMessage(), e);
      ToastUtil.toastError(getActivity(), new UnknownSystemException(e));
    }
  }

  private void handleGoodsDialogConfirmBtn(Double count, Long selectedUnit, SaleOrderItem item,
      Goods goods) {
    try {
      if (Empty.isEmpty(item)) {
        if (count * 1000L > Double.valueOf(String.valueOf(goods.getExisting()))) {
          ToastUtil.toastError(getActivity(), new SaleOrderItemCountExceedExistingException());
          return;
        }
        item = createOrderItem(goods);
      }

      saleOrderService.updateOrderItemCount(item.getId(), count, selectedUnit, orderStatus, goods);
      Long orderAmount = saleOrderService.updateOrderAmount(order.getId());
      order.setOrderItems(saleOrderService.getOrderItemDtoList(order.getId()));
      order.setAmount(orderAmount);
    } catch (BusinessException ex) {
      Log.e(TAG, ex.getMessage(), ex);
      ToastUtil.toastError(getActivity(), ex);
    } catch (Exception ex) {
      Crashlytics.log(Log.ERROR, "Data storage Exception",
          "Error in confirming GoodsList " + ex.getMessage());
      Log.e(TAG, ex.getMessage(), ex);
      ToastUtil.toastError(getActivity(), new UnknownSystemException(ex));
    }
  }

  private SaleOrderItem createOrderItem(Goods goods) {
    SaleOrderItem saleOrderItem = new SaleOrderItem();
    saleOrderItem.setGoodsBackendId(goods.getBackendId());
    saleOrderItem.setGoodsCount(0L);
    saleOrderItem.setSaleOrderId(order.getId());
    saleOrderItem.setInvoiceBackendId(goods.getInvoiceBackendId());
    saleOrderItem.setCreateDateTime(DateUtil.getCurrentGregorianFullWithTimeDate());
    saleOrderItem.setUpdateDateTime(DateUtil.getCurrentGregorianFullWithTimeDate());
    Long orderItemId = saleOrderService.saveOrderItem(saleOrderItem);
    saleOrderItem.setId(orderItemId);
    return saleOrderItem;
  }

  @Override
  public int getFragmentId() {
    return 0;
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
  }
}
