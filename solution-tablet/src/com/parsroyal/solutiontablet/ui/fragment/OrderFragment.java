package com.parsroyal.solutiontablet.ui.fragment;

import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.LayoutManager;
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
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.parsroyal.solutiontablet.R;
import com.parsroyal.solutiontablet.constants.Constants;
import com.parsroyal.solutiontablet.constants.SaleOrderStatus;
import com.parsroyal.solutiontablet.constants.StatusCodes;
import com.parsroyal.solutiontablet.data.entity.Goods;
import com.parsroyal.solutiontablet.data.entity.GoodsGroup;
import com.parsroyal.solutiontablet.data.entity.SaleOrderItem;
import com.parsroyal.solutiontablet.data.event.ErrorEvent;
import com.parsroyal.solutiontablet.data.event.SuccessEvent;
import com.parsroyal.solutiontablet.data.event.UpdateListEvent;
import com.parsroyal.solutiontablet.data.model.GoodsDtoList;
import com.parsroyal.solutiontablet.data.model.GoodsGroupExpand;
import com.parsroyal.solutiontablet.data.model.SaleOrderDto;
import com.parsroyal.solutiontablet.data.searchobject.GoodsSo;
import com.parsroyal.solutiontablet.exception.BusinessException;
import com.parsroyal.solutiontablet.exception.UnknownSystemException;
import com.parsroyal.solutiontablet.service.GoodsService;
import com.parsroyal.solutiontablet.service.impl.GoodsServiceImpl;
import com.parsroyal.solutiontablet.service.impl.SaleOrderServiceImpl;
import com.parsroyal.solutiontablet.ui.MainActivity;
import com.parsroyal.solutiontablet.ui.adapter.BreadCrumbAdapter;
import com.parsroyal.solutiontablet.ui.adapter.GoodsAdapter;
import com.parsroyal.solutiontablet.ui.adapter.GoodsCategoryAdapter;
import com.parsroyal.solutiontablet.ui.adapter.GoodsExpandAdapter;
import com.parsroyal.solutiontablet.ui.fragment.bottomsheet.AddOrderBottomSheet;
import com.parsroyal.solutiontablet.ui.fragment.dialogFragment.AddOrderDialogFragment;
import com.parsroyal.solutiontablet.ui.fragment.dialogFragment.FinalizeOrderDialogFragment;
import com.parsroyal.solutiontablet.util.Analytics;
import com.parsroyal.solutiontablet.util.CharacterFixUtil;
import com.parsroyal.solutiontablet.util.Empty;
import com.parsroyal.solutiontablet.util.Logger;
import com.parsroyal.solutiontablet.util.MultiScreenUtility;
import com.parsroyal.solutiontablet.util.NumberUtil;
import com.parsroyal.solutiontablet.util.RtlGridLayoutManager;
import com.parsroyal.solutiontablet.util.ToastUtil;
import com.parsroyal.solutiontablet.util.constants.ApplicationKeys;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

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
  @BindView(R.id.order_count_tv)
  TextView orderCountTv;
  @BindView(R.id.bottom_bar_text)
  TextView bottomBarText;
  @BindView(R.id.goods_cart_image)
  ImageView goodsCartImage;
  @BindView(R.id.close_search_img)
  ImageView closeSearchImg;
  @BindView(R.id.expandable_recycler_view)
  RecyclerView expandableRecyclerView;
  @BindView(R.id.category_recycler_view)
  RecyclerView categoryRecyclerView;
  @BindView(R.id.bread_crumb_recycler_view)
  RecyclerView breadCrumbRecyclerView;
  @BindView(R.id.app_bar)
  AppBarLayout appBarLayout;
  @BindView(R.id.bread_crumb_lay)
  LinearLayout breadCrumbLay;

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
  private Long orderStatus = -1L;
  private GoodsDtoList rejectedGoodsList;
  private String constraint;
  private String pageStatus;
  private long currentGoodGroupId;
  private GoodsExpandAdapter goodsExpandAdapter;
  private BreadCrumbAdapter breadCrumbAdapter;
  private GoodsCategoryAdapter goodsCategoryAdapter;
  private List<GoodsGroup> breadCrumbList = new ArrayList<>();
  private long visitlineBackendId;
  private boolean isCashOrder;
  private HashMap<String,Long> titleIdes;

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
    goodsService = new GoodsServiceImpl(mainActivity);
    saleOrderService = new SaleOrderServiceImpl(mainActivity);
    try {
      Bundle args = getArguments();
      if (Empty.isEmpty(args)) {
        return inflater.inflate(R.layout.view_error_page, container, false);
      }
      readOnly = args.getBoolean(Constants.READ_ONLY);
      if (readOnly) {
        //It comes from FeatureList->Goods
        bottomBar.setVisibility(View.GONE);
        mainActivity.changeTitle(getString(R.string.title_goods_list));
      } else {
        //Comes from Report or CustomerVisit, to add Order or Reject
        orderId = args.getLong(Constants.ORDER_ID, -1);
        order = saleOrderService.findOrderDtoById(orderId);
        if (Empty.isEmpty(order)) {
          return inflater.inflate(R.layout.empty_view, container, false);
        }
        orderStatus = order.getStatus();
        saleType = args.getString(Constants.SALE_TYPE, "");
        visitId = args.getLong(Constants.VISIT_ID, -1);
        pageStatus = args.getString(Constants.PAGE_STATUS, "");
        visitlineBackendId = args.getLong(Constants.VISITLINE_BACKEND_ID);
        isCashOrder = args.getBoolean(Constants.CASH_ORDER, false);
        mainActivity.changeTitle(getProperTitle());
      }

      setData();
      addSearchListener();
      if (!TextUtils.isEmpty(pageStatus) && (pageStatus.equals(Constants.EDIT) || pageStatus
          .equals(Constants.VIEW))) {
        showFinalizeOrderDialog();
      }
    } catch (Exception ex) {
      ex.printStackTrace();
      return inflater.inflate(R.layout.empty_view, container, false);
    }

    return view;
  }

  private List<GoodsGroupExpand> getExpandList() {
    Map<GoodsGroup, List<GoodsGroup>> goodsGroups = goodsService.getCategories();
    titleIdes = new HashMap<>();
    List<GoodsGroupExpand> goodsGroupExpands = new ArrayList<>();
    goodsGroupExpands
        .add(new GoodsGroupExpand(getString(R.string.show_all_goods), new ArrayList<>()));
    for (Map.Entry<GoodsGroup, List<GoodsGroup>> entry : goodsGroups.entrySet()) {
      goodsGroupExpands.add(new GoodsGroupExpand(entry.getKey().getTitle(), entry.getValue()));
      titleIdes.put(entry.getKey().getTitle(), entry.getKey().getBackendId());
    }
    return goodsGroupExpands;
  }

  public void setUpExpandRecyclerView() {
    currentGoodGroupId = -1L;
    breadCrumbList.clear();
    breadCrumbList.add(new GoodsGroup(getString(R.string.categories), 0));
    mainActivity.changeTitle(mainActivity.getString(R.string.categories));
    if (goodsExpandAdapter == null) {
      goodsExpandAdapter = new GoodsExpandAdapter(mainActivity, getExpandList(),titleIdes, this);
      LayoutManager layoutManager = new LinearLayoutManager(mainActivity);
      expandableRecyclerView.setAdapter(goodsExpandAdapter);
      expandableRecyclerView.setLayoutManager(layoutManager);
    }

    recyclerView.setVisibility(View.GONE);
    categoryRecyclerView.setVisibility(View.GONE);
    expandableRecyclerView.setVisibility(View.VISIBLE);
    appBarLayout.setVisibility(View.GONE);
    breadCrumbLay.setVisibility(View.GONE);
    mainActivity.searchImageVisibility(View.GONE);
  }

  public void onBreadCrumbClicked(GoodsGroup goodsGroup) {
    if (goodsGroup.getLevel() == 0 || goodsGroup.getLevel() == 1) {
      setUpExpandRecyclerView();
    } else {
      setUpCategoryRecyclerView(goodsService.getChilds(goodsGroup.getBackendId()));
    }
  }

  public void setUpBreadCrumbRecyclerView(GoodsGroup goodsGroup) {
    if (breadCrumbList.size() > 0 && breadCrumbList.contains(goodsGroup)) {
      int pos = breadCrumbList.indexOf(goodsGroup);
      int size = breadCrumbList.size();
      for (int i = size - 1; i > pos; i--) {
        breadCrumbList.remove(i);
      }
    } else {
      breadCrumbList.add(goodsGroup);
    }
    if (breadCrumbAdapter == null) {
      breadCrumbAdapter = new BreadCrumbAdapter(breadCrumbList, mainActivity, this);
      LayoutManager layoutManager = new LinearLayoutManager(mainActivity,
          LinearLayoutManager.HORIZONTAL, true);
      breadCrumbRecyclerView.setAdapter(breadCrumbAdapter);
      breadCrumbRecyclerView.setLayoutManager(layoutManager);
    } else {
      breadCrumbAdapter.update(breadCrumbList);
    }
    if (breadCrumbList.size() > 0) {
      breadCrumbRecyclerView.smoothScrollToPosition(breadCrumbList.size() - 1);
    }
  }

  public void setUpCategoryRecyclerView(List<GoodsGroup> goodsGroups) {
    if (goodsGroups.size() > 0) {
      currentGoodGroupId = goodsGroups.get(0).getBackendId();
      GoodsGroup goodsGroup = goodsService.getParent(currentGoodGroupId);
      mainActivity.changeTitle(goodsGroup.getTitle());
      setUpBreadCrumbRecyclerView(goodsGroup);
    }
    if (goodsCategoryAdapter == null) {
      goodsCategoryAdapter = new GoodsCategoryAdapter(goodsGroups, mainActivity, this);
      LayoutManager layoutManager = new LinearLayoutManager(mainActivity);
      categoryRecyclerView.setLayoutManager(layoutManager);
      categoryRecyclerView.setAdapter(goodsCategoryAdapter);
    } else {
      goodsCategoryAdapter.updateAll(goodsGroups);
    }
    recyclerView.setVisibility(View.GONE);
    expandableRecyclerView.setVisibility(View.GONE);
    categoryRecyclerView.setVisibility(View.VISIBLE);
    appBarLayout.setVisibility(View.GONE);
    noGoodLay.setVisibility(View.GONE);
    breadCrumbLay.setVisibility(View.VISIBLE);
    mainActivity.searchImageVisibility(View.GONE);
  }

  public void setUpGoodsRecyclerView(long backendId) {
    goodsSo = new GoodsSo();
    if (adapter == null) {
      if (isRejected()) {
        rejectedGoodsList = (GoodsDtoList) getArguments().getSerializable(Constants.REJECTED_LIST);

        adapter = new GoodsAdapter(mainActivity, this, rejectedGoodsList.getGoodsDtoList(),
            readOnly,
            true);
        bottomBar.setBackgroundColor(ContextCompat.getColor(mainActivity, R.color.register_return));
        bottomBarText.setText(R.string.reject_goods);
        goodsCartImage.setVisibility(View.GONE);
      } else {
        goodsSo.setConstraint("");
        if (backendId != -1L) {
          goodsSo.setGoodsGroupBackendId(backendId);
        }
        adapter = new GoodsAdapter(mainActivity, this, goodsService.searchForGoodsList(goodsSo),
            readOnly, false);
      }
      if (MultiScreenUtility.isTablet(mainActivity)) {
        RtlGridLayoutManager rtlGridLayoutManager = new RtlGridLayoutManager(mainActivity, 2);
        recyclerView.setLayoutManager(rtlGridLayoutManager);
      } else {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mainActivity);
        recyclerView.setLayoutManager(linearLayoutManager);
      }
      recyclerView.setAdapter(adapter);
    } else {
      goodsSo.setConstraint("");
      if (backendId != -1L) {
        goodsSo.setGoodsGroupBackendId(backendId);
      }
      adapter.update(goodsService.searchForGoodsList(goodsSo));
    }
    if (adapter.getItemCount() > 0) {
      currentGoodGroupId = backendId;
      if (backendId != -1) {
        GoodsGroup goodsGroup = goodsService.getCurrent(currentGoodGroupId);
        mainActivity.changeTitle(goodsGroup.getTitle());
        setUpBreadCrumbRecyclerView(goodsGroup);
      } else {
        mainActivity.changeTitle(mainActivity.getString(R.string.goods));
      }
      recyclerView.setVisibility(View.VISIBLE);
      expandableRecyclerView.setVisibility(View.GONE);
      categoryRecyclerView.setVisibility(View.GONE);
      appBarLayout.setVisibility(View.GONE);
      breadCrumbLay.setVisibility(View.VISIBLE);
      mainActivity.searchImageVisibility(View.VISIBLE);
      if (backendId == -1) {
        mainActivity.searchImageVisibility(View.GONE);
        breadCrumbLay.setVisibility(View.GONE);
        appBarLayout.setVisibility(View.VISIBLE);
        closeSearchImg.setVisibility(View.GONE);
      } else {
        mainActivity.searchImageVisibility(View.VISIBLE);
        breadCrumbLay.setVisibility(View.VISIBLE);
        appBarLayout.setVisibility(View.GONE);
        closeSearchImg.setVisibility(View.VISIBLE);
      }
    } else {
      ToastUtil.toastMessage(getActivity(), R.string.no_goods);
    }
  }

  //set up recycler view
  private void setData() {
    setUpExpandRecyclerView();
//    if (isRejected()) {
//      rejectedGoodsList = (GoodsDtoList) getArguments().getSerializable(Constants.REJECTED_LIST);
//
//      adapter = new GoodsAdapter(mainActivity, this, rejectedGoodsList.getGoodsDtoList(), readOnly,
//          true);
//      bottomBar.setBackgroundColor(ContextCompat.getColor(mainActivity, R.color.register_return));
//      bottomBarText.setText(R.string.reject_goods);
//      goodsCartImage.setVisibility(View.GONE);
//
//    } else {
//      goodsSo.setConstraint("");
//      adapter = new GoodsAdapter(mainActivity, this, goodsService.searchForGoodsList(goodsSo),
//          readOnly, false);
//    }

    if (!readOnly) {
      orderCountTv
          .setText(NumberUtil.digitsToPersian(String.valueOf(order.getOrderItems().size())));
    }

  }

  /*
   @return Proper title for which could be "Rejected", "Order" or "Invoice"
   */
  private String getProperTitle() {
    if (isRejected()) {
      return getString(R.string.title_reject_list);
    } else if (isCold()) {
      return getString(R.string.title_goods_list);
    } else {
      return getString(R.string.title_factor);
    }
  }

  private boolean isCold() {
    return saleType.equals(ApplicationKeys.SALE_COLD);
  }

  /*
  @return true if it's one of the REJECTED states
   */
  private boolean isRejected() {
    return (orderStatus.equals(SaleOrderStatus.REJECTED_DRAFT.getId()) ||
        orderStatus.equals(SaleOrderStatus.REJECTED.getId()) ||
        orderStatus.equals(SaleOrderStatus.REJECTED_SENT.getId()));
  }

  private void addSearchListener() {
    searchEdt.addTextChangedListener(new TextWatcher() {
      @Override
      public void beforeTextChanged(CharSequence s, int start, int count, int after) {
      }

      @Override
      public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (TextUtils.isEmpty(s.toString())) {
          searchImg.setImageResource(R.drawable.ic_search);
        } else {
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
  public void onResume() {
    super.onResume();
    EventBus.getDefault().register(this);
  }

  @Override
  public void onPause() {
    super.onPause();
    EventBus.getDefault().unregister(this);
  }

  @Subscribe
  public void getMessage(UpdateListEvent event) {
    order = saleOrderService.findOrderDtoById(orderId);
    orderCountTv.setText(NumberUtil.digitsToPersian(String.valueOf(order.getOrderItems().size())));
  }

  @OnClick({R.id.search_img, R.id.bottom_bar, R.id.cancel_bread_crumb_btn, R.id.close_search_img})
  public void onClick(View view) {
    switch (view.getId()) {
      case R.id.search_img:
        searchEdt.setText("");
        break;
      case R.id.close_search_img:
        onDismissSearchClicked();
        break;
      case R.id.cancel_bread_crumb_btn:
        setUpExpandRecyclerView();
        break;
      case R.id.bottom_bar:
        if (order.getOrderItems().size() == 0) {
          return;
        }
        showFinalizeOrderDialog();
        break;
    }
  }

  public void goToOrderInfoFragment(Long rejectType) {
    Bundle args = new Bundle();
    args.putLong(Constants.ORDER_ID, orderId);
    args.putString(Constants.SALE_TYPE, saleType);
    args.putString(Constants.PAGE_STATUS, pageStatus);
    args.putLong(Constants.VISIT_ID, visitId);
    args.putSerializable(Constants.REJECTED_LIST, rejectedGoodsList);
    args.putLong(Constants.VISITLINE_BACKEND_ID, visitlineBackendId);
    args.putBoolean(Constants.CASH_ORDER, isCashOrder);
    if (rejectType != null) {
      args.putLong(Constants.REJECT_TYPE_ID, rejectType);
    }

    mainActivity.changeFragment(MainActivity.ORDER_INFO_FRAGMENT, args, true);
  }

  public void showOrderDialog(Goods goods) {
    try {
      AddOrderDialogFragment addOrderDialogFragment;
      FragmentTransaction ft = mainActivity.getSupportFragmentManager().beginTransaction();
      if (MultiScreenUtility.isTablet(mainActivity)) {
        addOrderDialogFragment = AddOrderBottomSheet.newInstance();
      } else {
        addOrderDialogFragment = AddOrderDialogFragment.newInstance();
      }

      Long invoiceBackendId = 0L;
      if (isRejected()) {
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
      Logger.sendError("Data Storage Exception",
          "Error in confirming handling GoodsList " + e.getMessage());
      Log.e(TAG, e.getMessage(), e);
      ToastUtil.toastError(getActivity(), new UnknownSystemException(e));
    }
  }

  public void showFinalizeOrderDialog() {
    FragmentTransaction ft = mainActivity.getSupportFragmentManager().beginTransaction();
    FinalizeOrderDialogFragment finalizeOrderDialogFragment = FinalizeOrderDialogFragment
        .newInstance(this);

    Bundle bundle = new Bundle();
    bundle.putLong(Constants.ORDER_ID, orderId);
    bundle.putLong(Constants.ORDER_STATUS, orderStatus);
    bundle.putString(Constants.PAGE_STATUS, pageStatus);
    bundle.putSerializable(Constants.REJECTED_LIST, rejectedGoodsList);
    bundle.putLong(Constants.VISIT_ID, visitId);
    bundle.putLong(Constants.VISITLINE_BACKEND_ID, visitlineBackendId);
    bundle.putBoolean(Constants.CASH_ORDER, isCashOrder);
    finalizeOrderDialogFragment.setArguments(bundle);

    finalizeOrderDialogFragment.show(ft, "order");
  }

  private void updateGoodsDataTb() {

    if (isRejected()) {
      if (Empty.isNotEmpty(rejectedGoodsList)) {
        List<Goods> goodsList = rejectedGoodsList.getGoodsDtoList();
        List<Goods> filteredList = new ArrayList<>();
        for (int i = 0; i < goodsList.size(); i++) {
          Goods good = goodsList.get(i);
          if (Empty.isNotEmpty(constraint) && !good.getTitle().contains(constraint) && !good
              .getCode().contains(constraint)) {
            continue;
          }
          filteredList.add(good);
        }
        if (filteredList.size() > 0) {//Found something
          showGoodsList();
          adapter.update(filteredList);
        } else {
          showEmptyList();
        }
      } else {
        showEmptyList();
      }
    } else {
      goodsList = goodsService.searchForGoodsList(goodsSo);
      if (goodsList.size() > 0) {
        showGoodsList();
        if (adapter == null) {
          adapter = new GoodsAdapter(mainActivity, this, goodsList, readOnly, false);
          recyclerView.setAdapter(adapter);
        }
        adapter.update(goodsList);

      } else {
        showEmptyList();
      }
    }
  }

  private void showGoodsList() {
    recyclerView.setVisibility(View.VISIBLE);
    noGoodLay.setVisibility(View.GONE);
  }

  private void showEmptyList() {
    recyclerView.setVisibility(View.GONE);
    noGoodLay.setVisibility(View.VISIBLE);
  }

  private void handleGoodsDialogConfirmBtn(Double count, Long selectedUnit, SaleOrderItem item,
      Goods goods) {
    try {
      if (Empty.isEmpty(item)) {
        if (count * 1000L > Double.valueOf(String.valueOf(goods.getExisting()))) {
//          ToastUtil.toastError(getActivity(), new SaleOrderItemCountExceedExistingException());
          EventBus.getDefault().post(new ErrorEvent(getString(R.string.exceed_count_exception),
              StatusCodes.DATA_STORE_ERROR));
          return;
        }
        item = createOrderItem(goods);
      }

      saleOrderService.updateOrderItemCount(item.getId(), count, selectedUnit, orderStatus, goods);
      Long orderAmount = saleOrderService.updateOrderAmount(order.getId());
      order.setOrderItems(saleOrderService.getOrderItemDtoList(order.getId()));
      order.setAmount(orderAmount);
      orderCountTv
          .setText(NumberUtil.digitsToPersian(String.valueOf(order.getOrderItems().size())));
      EventBus.getDefault().post(new SuccessEvent(StatusCodes.SUCCESS));
    } catch (BusinessException ex) {
      Log.e(TAG, ex.getMessage(), ex);
//      ToastUtil.toastError(getActivity(), ex);
      EventBus.getDefault().post(new ErrorEvent(getString(R.string.exceed_count_exception),
          StatusCodes.DATA_STORE_ERROR));
    } catch (Exception ex) {
      Logger.sendError("Data storage Exception",
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

  @Override
  public int getFragmentId() {
    return MainActivity.GOODS_LIST_FRAGMENT_ID;
  }

  public boolean isExpandableVisible() {
    return expandableRecyclerView.getVisibility() == View.VISIBLE;
  }

  public void onBackPressed() {
    try {
      if (expandableRecyclerView.getVisibility() == View.VISIBLE) {
        mainActivity.onBackPressed();
      } else if (categoryRecyclerView.getVisibility() == View.VISIBLE) {
        if (goodsCategoryAdapter.getItemByPosition(0).getLevel() == 3) {
          setUpExpandRecyclerView();
        } else {
          setUpCategoryRecyclerView(goodsService.getUpLevel(currentGoodGroupId));
        }
      } else if (recyclerView.getVisibility() == View.VISIBLE) {
        List<GoodsGroup> goodsGroups = goodsService.getCurrentLevel(currentGoodGroupId);
        if ((goodsGroups != null && goodsGroups.size() > 0 && goodsGroups.get(0).getLevel() < 3)
            || goodsGroups == null) {
          setUpExpandRecyclerView();
        } else {
          setUpCategoryRecyclerView(goodsGroups);
        }
      } else {
        mainActivity.onBackPressed();
        mainActivity.searchImageVisibility(View.GONE);
      }
    } catch (Exception e) {
      mainActivity.onBackPressed();
      mainActivity.searchImageVisibility(View.GONE);
    }
  }

  public void onSearchClicked() {
    mainActivity.searchImageVisibility(View.GONE);
    breadCrumbLay.setVisibility(View.GONE);
    appBarLayout.setVisibility(View.VISIBLE);
  }

  public void onDismissSearchClicked() {
    searchEdt.setText("");
    mainActivity.hideKeyboard();
    mainActivity.searchImageVisibility(View.VISIBLE);
    breadCrumbLay.setVisibility(View.VISIBLE);
    appBarLayout.setVisibility(View.GONE);
  }
}
