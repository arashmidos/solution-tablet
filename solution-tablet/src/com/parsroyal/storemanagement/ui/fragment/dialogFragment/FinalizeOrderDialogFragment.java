package com.parsroyal.storemanagement.ui.fragment.dialogFragment;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.parsroyal.storemanagement.R;
import com.parsroyal.storemanagement.constants.Constants;
import com.parsroyal.storemanagement.constants.SaleOrderStatus;
import com.parsroyal.storemanagement.constants.VisitInformationDetailType;
import com.parsroyal.storemanagement.data.entity.VisitInformationDetail;
import com.parsroyal.storemanagement.data.event.UpdateListEvent;
import com.parsroyal.storemanagement.data.model.GoodsDtoList;
import com.parsroyal.storemanagement.data.model.SaleOrderDto;
import com.parsroyal.storemanagement.data.model.SaleOrderItemDto;
import com.parsroyal.storemanagement.exception.BusinessException;
import com.parsroyal.storemanagement.exception.UnknownSystemException;
import com.parsroyal.storemanagement.service.SaleOrderService;
import com.parsroyal.storemanagement.service.impl.SaleOrderServiceImpl;
import com.parsroyal.storemanagement.service.impl.VisitServiceImpl;
import com.parsroyal.storemanagement.ui.activity.MainActivity;
import com.parsroyal.storemanagement.ui.adapter.OrderFinalizeAdapter;
import com.parsroyal.storemanagement.ui.fragment.OrderFragment;
import com.parsroyal.storemanagement.util.DialogUtil;
import com.parsroyal.storemanagement.util.MultiScreenUtility;
import com.parsroyal.storemanagement.util.NumberUtil;
import com.parsroyal.storemanagement.util.RtlGridLayoutManager;
import com.parsroyal.storemanagement.util.ToastUtil;
import java.util.List;
import org.greenrobot.eventbus.EventBus;
import timber.log.Timber;

public class FinalizeOrderDialogFragment extends DialogFragment {

  @BindView(R.id.recycler_view)
  RecyclerView recyclerView;
  @BindView(R.id.total_amount_title)
  TextView totalAmountTitle;
  @BindView(R.id.total_amount_tv)
  TextView totalAmountTv;
  @BindView(R.id.submit_tv)
  TextView submitTv;
  @BindView(R.id.toolbar_title)
  TextView toolbarTitle;
  @BindView(R.id.bottom_layout)
  ViewGroup bottomLayout;
  @BindView(R.id.cancel_order)
  Button cancelButton;
  @BindView(R.id.root)
  LinearLayout root;
  @BindView(R.id.total_sale)
  LinearLayout totalSaleLayout;
  @BindView(R.id.total_count1_tv)
  TextView totalCount1;
  @BindView(R.id.total_count2_tv)
  TextView totalCount2;

  private OrderFinalizeAdapter adapter;
  private MainActivity mainActivity;
  private SaleOrderService saleOrderService;
  private SaleOrderDto order;
  private OrderFragment orderFragment;
  private long orderId;
  private Long orderStatus;
  private String pageStatus;
  private GoodsDtoList rejectedGoodsList;
  private long visitId;
  private boolean orderChanged = false;
  private Long rejectType;


  public FinalizeOrderDialogFragment() {
    // Required empty public constructor
  }

  public static FinalizeOrderDialogFragment newInstance(OrderFragment orderFragment) {
    FinalizeOrderDialogFragment fragment = new FinalizeOrderDialogFragment();
    fragment.orderFragment = orderFragment;
    return fragment;
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setStyle(DialogFragment.STYLE_NORMAL, R.style.myDialog);
    setRetainInstance(true);
  }

  @NonNull
  @Override
  public Dialog onCreateDialog(Bundle savedInstanceState) {
    return new Dialog(getActivity(), getTheme()) {
      @Override
      public void onBackPressed() {
        if (pageStatus.equals(Constants.VIEW) || isDelivery()) {
          if (SaleOrderStatus.DELIVERABLE.getId().equals(orderStatus)
              && !validateOrderForDeliver()) {
          } else {
            getDialog().dismiss();
            mainActivity.navigateToFragment(OrderFragment.class.getSimpleName());
          }
        } else {
          getDialog().dismiss();
        }
      }
    };
  }

  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    View view = inflater.inflate(R.layout.fragment_finalize_order_dialog, container, false);
    ButterKnife.bind(this, view);
    mainActivity = (MainActivity) getActivity();
    saleOrderService = new SaleOrderServiceImpl(mainActivity);
    Bundle arguments = getArguments();
    orderId = arguments.getLong(Constants.ORDER_ID);
    orderStatus = arguments.getLong(Constants.ORDER_STATUS);
    pageStatus = arguments.getString(Constants.PAGE_STATUS);
    order = saleOrderService.findOrderDtoById(orderId);
    visitId = arguments.getLong(Constants.VISIT_ID, -1);
    rejectedGoodsList = (GoodsDtoList) arguments.getSerializable(Constants.REJECTED_LIST);

    setData();
    setUpRecyclerView();
    updateCountLayout();
    return view;
  }

  public void updateCountLayout() {
    List<SaleOrderItemDto> items = order.getOrderItems();
    double count1 = 0.0, count2 = 0.0;

    for (SaleOrderItemDto item : items) {
      count1 += (item.getGoodsCount() / 1000.0);
      count2 += (item.getGoodsUnit2Count() / 1000.0);
    }

    totalCount1.setText(NumberUtil.formatPersian3DecimalPlaces(count1));
    totalCount2.setText(NumberUtil.formatPersian3DecimalPlaces(count2));
  }


  private void setData() {
    totalAmountTv.setText(NumberUtil.digitsToPersian(NumberUtil.getCommaSeparated(
        order.getAmount() / 1000) + " " + getString(R.string.common_irr_currency)));
    if (pageStatus.equals(Constants.VIEW)) {
      submitTv.setText(R.string.payment_detail);
    } else if (orderStatus.equals(SaleOrderStatus.DELIVERABLE.getId())) {
      cancelButton.setVisibility(View.VISIBLE);
      submitTv.setText(R.string.title_deliver_sale_order);
    }

    if (isRejected()) {
      toolbarTitle.setText(R.string.reject_goods);
      submitTv.setText(R.string.finalize_return);
      bottomLayout
          .setBackgroundColor(ContextCompat.getColor(mainActivity, R.color.register_return));
    }
  }

  /*
  @return true if it's one of the REJECTED states
 */
  protected boolean isRejected() {
    return (orderStatus.equals(SaleOrderStatus.REJECTED_DRAFT.getId()) ||
        orderStatus.equals(SaleOrderStatus.REJECTED.getId()) ||
        orderStatus.equals(SaleOrderStatus.REJECTED_SENT.getId()));
  }

  public void updateList() {
    order = saleOrderService.findOrderDtoById(orderId);
    setData();
    updateCountLayout();
    EventBus.getDefault().post(new UpdateListEvent());
  }

  //set up recycler view
  private void setUpRecyclerView() {
    adapter = new OrderFinalizeAdapter(this, mainActivity, order, rejectedGoodsList, pageStatus);
    if (MultiScreenUtility.isTablet(mainActivity)) {
      RtlGridLayoutManager rtlGridLayoutManager = new RtlGridLayoutManager(mainActivity, 2);
      recyclerView.setLayoutManager(rtlGridLayoutManager);
    } else {
      LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
      recyclerView.setLayoutManager(linearLayoutManager);
    }
    recyclerView.setAdapter(adapter);
  }

  @OnClick({R.id.close, R.id.submit_btn, R.id.bottom_layout, R.id.cancel_order})
  public void onClick(View view) {
    switch (view.getId()) {
      case R.id.close:
        if (pageStatus.equals(Constants.VIEW) || isDelivery()) {
          if (SaleOrderStatus.DELIVERABLE.getId().equals(orderStatus)
              && !validateOrderForDeliver()) {
          } else {
            getDialog().dismiss();
            mainActivity.navigateToFragment(OrderFragment.class.getSimpleName());
          }
        } else {
          getDialog().dismiss();
        }
        break;
      case R.id.bottom_layout:
      case R.id.submit_btn:
        if (SaleOrderStatus.DELIVERABLE.getId().equals(orderStatus)) {
          if (validateOrderForDeliver()) {
            if (orderChanged) {
              showRejectTypeDialog(false);
            } else {
              orderFragment.goToOrderInfoFragment(rejectType);
              getDialog().dismiss();
            }
          }
        } else {
          if (orderFragment != null) {
            orderFragment.goToOrderInfoFragment(null);
            getDialog().dismiss();
          }
        }
        break;
      case R.id.cancel_order:
        showSaveOrderConfirmDialog(getString(R.string.title_cancel_sale_order));
        break;
    }
  }

  private boolean validateOrderForDeliver() {
    if (order.getOrderItems().size() == 0) {
      ToastUtil.toastError(root, R.string.message_order_has_no_item);
      return false;
    }
    return true;
  }

  private boolean isDelivery() {
    return orderStatus.equals(SaleOrderStatus.DELIVERABLE.getId()) ||
        orderStatus.equals(SaleOrderStatus.DELIVERED.getId()) ||
        orderStatus.equals(SaleOrderStatus.DELIVERABLE_SENT.getId());
  }

  private void showSaveOrderConfirmDialog(String title) {
    DialogUtil.showConfirmDialog(mainActivity, title,
        mainActivity.getString(R.string.message_are_you_sure),
        (dialog, which) -> showRejectTypeDialog(true));
  }

  private void showRejectTypeDialog(boolean isCanceled) {
    FragmentTransaction ft = mainActivity.getSupportFragmentManager().beginTransaction();
    DeliveryRejectDialogFragment deliveryRejectDialogFragment = DeliveryRejectDialogFragment
        .newInstance(mainActivity, this, isCanceled);
    deliveryRejectDialogFragment.show(ft, "delivery reject");
  }

  public void saveOrder(Long psn) {
    try {
      order.setStatus(SaleOrderStatus.CANCELED.getId());
      order.setRejectType(psn);

//      String description = "";//orderInfoFrg.getDescription();
//      order.setDescription(description);

      long typeId = saleOrderService.saveOrder(order);

      VisitInformationDetail visitDetail = new VisitInformationDetail(visitId,
          VisitInformationDetailType.DELIVER_ORDER, typeId);
      new VisitServiceImpl(mainActivity).saveVisitDetail(visitDetail);
      getDialog().dismiss();
      mainActivity.navigateToFragment(OrderFragment.class.getSimpleName());
    } catch (BusinessException ex) {
      Timber.e(ex);
      ToastUtil.toastError(root, ex);
    } catch (Exception ex) {
      Timber.e(ex);
      ToastUtil.toastError(root, new UnknownSystemException(ex));
    }
  }

  @Override
  public void onDestroyView() {
    //workaround for this issue: https://code.google.com/p/android/issues/detail?id=17423 (unable to retain instance after configuration change)
    if (getDialog() != null && getRetainInstance()) {
      getDialog().setDismissMessage(null);
    }
    super.onDestroyView();
  }

  public void setOrderChanged() {
    orderChanged = true;
  }

  public void setRejectType(Long value) {
    this.rejectType = value;
    orderFragment.goToOrderInfoFragment(rejectType);
    getDialog().dismiss();
  }

  public void close() {
    getDialog().dismiss();
    mainActivity.navigateToFragment(OrderFragment.class.getSimpleName());
  }
}
