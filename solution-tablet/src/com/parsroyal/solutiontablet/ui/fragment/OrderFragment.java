package com.parsroyal.solutiontablet.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.crashlytics.android.Crashlytics;
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
import com.parsroyal.solutiontablet.service.impl.GoodsServiceImpl;
import com.parsroyal.solutiontablet.service.impl.SaleOrderServiceImpl;
import com.parsroyal.solutiontablet.ui.MainActivity;
import com.parsroyal.solutiontablet.ui.adapter.GoodsAdapter;
import com.parsroyal.solutiontablet.ui.fragment.dialogFragment.AddOrderDialogFragment;
import com.parsroyal.solutiontablet.util.Analytics;
import com.parsroyal.solutiontablet.util.CharacterFixUtil;
import com.parsroyal.solutiontablet.util.Empty;
import com.parsroyal.solutiontablet.util.ToastUtil;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Shakib
 */
public class OrderFragment extends BaseFragment {

  private static final String TAG = OrderFragment.class.getName();
  @BindView(R.id.search_img)
  ImageView searchImg;
  @BindView(R.id.search_edt)
  EditText searchEdt;
  @BindView(R.id.recycler_view)
  RecyclerView recyclerView;
  @BindView(R.id.no_good_lay)
  LinearLayout noGoodLay;
  @BindView(R.id.bottom_bar)
  LinearLayout bottomBar;

  private boolean isClose = false;
  private List<Goods> goodsList;
  private GoodsService goodsService;
  private GoodsAdapter adapter;
  private GoodsSo goodsSo = new GoodsSo();
  private MainActivity mainActivity;
  private boolean readOnly;
  private long orderId;
  private String saleType;
  private long visitId;
  private SaleOrderServiceImpl saleOrderService;
  private SaleOrderDto order;
  private Long orderStatus;
  private GoodsDtoList rejectedGoodsList;
  private String constraint;
  private int orderCount = 0;

  public OrderFragment() {
    // Required empty public constructor
  }

  public static OrderFragment newInstance() {
    return new OrderFragment();
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    View view = inflater.inflate(R.layout.fragment_order, container, false);
    ButterKnife.bind(this, view);
    mainActivity = (MainActivity) getActivity();
    mainActivity.changeTitle(getString(R.string.title_goods_list));
    goodsService = new GoodsServiceImpl(mainActivity);
    saleOrderService = new SaleOrderServiceImpl(mainActivity);

    Bundle args = getArguments();
    readOnly = args.getBoolean(Constants.READ_ONLY);
    if (readOnly) {
      bottomBar.setVisibility(View.GONE);
    } else {
      orderId = args.getLong(Constants.ORDER_ID, -1);
      order = saleOrderService.findOrderDtoById(orderId);
      orderStatus = order.getStatus();
      saleType = args.getString(Constants.SALE_TYPE, "");
      visitId = args.getLong(Constants.VISIT_ID, -1);
    }
    setUpRecyclerView();
    addSearchListener();
    return view;
  }

  //set up recycler view
  private void setUpRecyclerView() {
    if (SaleOrderStatus.REJECTED_DRAFT.getId().equals(orderStatus)) {
      rejectedGoodsList = (GoodsDtoList) getArguments().getSerializable(Constants.REJECTED_LIST);

      adapter = new GoodsAdapter(mainActivity, this, rejectedGoodsList.getGoodsDtoList(), readOnly,
          true);

    }
    goodsSo.setConstraint("");
    adapter = new GoodsAdapter(mainActivity, this, goodsService.searchForGoodsList(goodsSo),
        readOnly,
        false);
    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mainActivity);
    recyclerView.setLayoutManager(linearLayoutManager);
    recyclerView.setAdapter(adapter);
  }

  private void addSearchListener() {
    searchEdt.addTextChangedListener(new TextWatcher() {
      @Override
      public void beforeTextChanged(CharSequence s, int start, int count, int after) {
      }

      @Override
      public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (TextUtils.isEmpty(s.toString())) {
          isClose = false;
          searchImg.setImageResource(R.drawable.ic_search);
        } else {
          isClose = true;
          searchImg.setImageResource(R.drawable.ic_close_24dp);
        }
      }

      @Override
      public void afterTextChanged(Editable s) {
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
    });
  }

  @Override
  public int getFragmentId() {
    return MainActivity.GOODS_LIST_FRAGMENT_ID;
  }

  @OnClick({R.id.search_img, R.id.bottom_bar})
  public void onClick(View view) {
    switch (view.getId()) {
      case R.id.search_img:
        if (isClose) {
          searchEdt.setText("");
        }
        break;
      case R.id.bottom_bar:
        mainActivity.changeFragment(MainActivity.ORDER_INFO_FRAGMENT, true);
        break;
    }
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
  }

  public void showOrderDialog(Goods goods) {
    try {
      FragmentTransaction ft = mainActivity.getSupportFragmentManager().beginTransaction();
      AddOrderDialogFragment addOrderDialogFragment = AddOrderDialogFragment.newInstance();

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

      addOrderDialogFragment.setArguments(bundle);
      addOrderDialogFragment.setOnClickListener((count, selectedUnit) -> {
        handleGoodsDialogConfirmBtn(count, selectedUnit, item, goods);
        updateGoodsDataTb();
      });

      addOrderDialogFragment.show(ft, "order");
    } catch (Exception e) {
      Crashlytics.log(Log.ERROR, "Data Storage Exception",
          "Error in confirming handling GoodsList " + e.getMessage());
      Log.e(TAG, e.getMessage(), e);
      ToastUtil.toastError(getActivity(), new UnknownSystemException(e));
    }
  }

  private void updateGoodsDataTb() {

    if (SaleOrderStatus.REJECTED_DRAFT.getId().equals(orderStatus)) {
      if (Empty.isNotEmpty(rejectedGoodsList)) {
        List<Goods> goodsList = rejectedGoodsList.getGoodsDtoList();
        List<Goods> filteredList = new ArrayList<>();
        for (int i = 0; i < goodsList.size(); i++) {
          Goods good = goodsList.get(i);
          if (Empty.isNotEmpty(constraint) && !good.getTitle().equals(constraint) && !good.getCode()
              .equals(constraint)) {
            continue;
          }
          filteredList.add(good);
        }
        recyclerView.setVisibility(View.VISIBLE);
        noGoodLay.setVisibility(View.GONE);
        adapter.update(filteredList);
      } else {
        recyclerView.setVisibility(View.GONE);
        noGoodLay.setVisibility(View.VISIBLE);
      }
    } else {
      goodsList = goodsService.searchForGoodsList(goodsSo);
      if (goodsList.size() > 0) {
        recyclerView.setVisibility(View.VISIBLE);
        noGoodLay.setVisibility(View.GONE);
        adapter.update(goodsList);
      } else {
        recyclerView.setVisibility(View.GONE);
        noGoodLay.setVisibility(View.VISIBLE);
      }
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
    SaleOrderItem saleOrderItem = new SaleOrderItem(goods.getBackendId(), order.getId(),
        goods.getInvoiceBackendId());
    Long orderItemId = saleOrderService.saveOrderItem(saleOrderItem);
    saleOrderItem.setId(orderItemId);
    return saleOrderItem;
  }
}
