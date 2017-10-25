package com.parsroyal.solutiontablet.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
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
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.parsroyal.solutiontablet.R;
import com.parsroyal.solutiontablet.constants.Constants;
import com.parsroyal.solutiontablet.constants.SaleOrderStatus;
import com.parsroyal.solutiontablet.constants.StatusCodes;
import com.parsroyal.solutiontablet.data.entity.Goods;
import com.parsroyal.solutiontablet.data.entity.SaleOrderItem;
import com.parsroyal.solutiontablet.data.event.ErrorEvent;
import com.parsroyal.solutiontablet.data.event.SuccessEvent;
import com.parsroyal.solutiontablet.data.event.UpdateListEvent;
import com.parsroyal.solutiontablet.data.model.GoodsDtoList;
import com.parsroyal.solutiontablet.data.model.SaleOrderDto;
import com.parsroyal.solutiontablet.data.searchobject.GoodsSo;
import com.parsroyal.solutiontablet.exception.BusinessException;
import com.parsroyal.solutiontablet.exception.UnknownSystemException;
import com.parsroyal.solutiontablet.service.GoodsService;
import com.parsroyal.solutiontablet.service.impl.GoodsServiceImpl;
import com.parsroyal.solutiontablet.service.impl.SaleOrderServiceImpl;
import com.parsroyal.solutiontablet.ui.MainActivity;
import com.parsroyal.solutiontablet.ui.adapter.GoodsAdapter;
import com.parsroyal.solutiontablet.ui.fragment.bottomsheet.AddOrderBottomSheet;
import com.parsroyal.solutiontablet.ui.fragment.dialogFragment.AddOrderDialogFragment;
import com.parsroyal.solutiontablet.ui.fragment.dialogFragment.FinalizeOrderDialogFragment;
import com.parsroyal.solutiontablet.util.Analytics;
import com.parsroyal.solutiontablet.util.CharacterFixUtil;
import com.parsroyal.solutiontablet.util.Empty;
import com.parsroyal.solutiontablet.util.Logger;
import com.parsroyal.solutiontablet.util.MultiScreenUtility;
import com.parsroyal.solutiontablet.util.RtlGridLayoutManager;
import com.parsroyal.solutiontablet.util.ToastUtil;
import com.parsroyal.solutiontablet.util.constants.ApplicationKeys;
import java.util.ArrayList;
import java.util.List;
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
  private Long orderStatus = -1L;
  private GoodsDtoList rejectedGoodsList;
  private String constraint;
  private String pageStatus;

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

    Bundle args = getArguments();
    readOnly = args.getBoolean(Constants.READ_ONLY);
    if (readOnly) {
      bottomBar.setVisibility(View.GONE);
      mainActivity.changeTitle(getString(R.string.title_goods_list));
    } else {
      orderId = args.getLong(Constants.ORDER_ID, -1);
      order = saleOrderService.findOrderDtoById(orderId);
      orderStatus = order.getStatus();
      saleType = args.getString(Constants.SALE_TYPE, "");
      visitId = args.getLong(Constants.VISIT_ID, -1);
      pageStatus = args.getString(Constants.PAGE_STATUS, "");
      orderCountTv.setText(String.valueOf(order.getOrderItems().size()));
      mainActivity.changeTitle(getProperTitle());
    }

    setData();
    addSearchListener();
    if (!TextUtils.isEmpty(pageStatus) && (pageStatus.equals(Constants.EDIT) || pageStatus
        .equals(Constants.VIEW))) {
      showFinalizeOrderDialog();
    }

    return view;
  }

  //set up recycler view
  private void setData() {
    if (isRejected()) {
      rejectedGoodsList = (GoodsDtoList) getArguments().getSerializable(Constants.REJECTED_LIST);

      adapter = new GoodsAdapter(mainActivity, this, rejectedGoodsList.getGoodsDtoList(), readOnly,
          true);
      bottomBar.setBackgroundColor(ContextCompat.getColor(mainActivity, R.color.register_return));
      bottomBarText.setText(R.string.reject_goods);
      goodsCartImage.setVisibility(View.GONE);

    } else {
      goodsSo.setConstraint("");
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
    orderCountTv.setText(String.valueOf(order.getOrderItems().size()));
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
        if (order.getOrderItems().size() == 0) {
          return;
        }
        showFinalizeOrderDialog();
        break;
    }
  }

  public void goToOrderInfoFragment() {
    Bundle args = new Bundle();
    args.putLong(Constants.ORDER_ID, orderId);
    args.putString(Constants.SALE_TYPE, saleType);
    args.putString(Constants.PAGE_STATUS, pageStatus);
    args.putLong(Constants.VISIT_ID, visitId);
    args.putSerializable(Constants.REJECTED_LIST, rejectedGoodsList);

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
      orderCountTv.setText(String.valueOf(order.getOrderItems().size()));
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
}
