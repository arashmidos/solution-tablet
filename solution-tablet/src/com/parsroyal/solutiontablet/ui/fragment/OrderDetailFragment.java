package com.parsroyal.solutiontablet.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import com.parsroyal.solutiontablet.R;
import com.parsroyal.solutiontablet.constants.Constants;
import com.parsroyal.solutiontablet.constants.SaleOrderStatus;
import com.parsroyal.solutiontablet.constants.VisitInformationDetailType;
import com.parsroyal.solutiontablet.data.entity.VisitInformationDetail;
import com.parsroyal.solutiontablet.data.model.GoodsDtoList;
import com.parsroyal.solutiontablet.data.model.SaleOrderDto;
import com.parsroyal.solutiontablet.exception.BusinessException;
import com.parsroyal.solutiontablet.exception.UnknownSystemException;
import com.parsroyal.solutiontablet.service.SaleOrderService;
import com.parsroyal.solutiontablet.service.SettingService;
import com.parsroyal.solutiontablet.service.impl.SaleOrderServiceImpl;
import com.parsroyal.solutiontablet.service.impl.SettingServiceImpl;
import com.parsroyal.solutiontablet.service.impl.VisitServiceImpl;
import com.parsroyal.solutiontablet.ui.activity.OldMainActivity;
import com.parsroyal.solutiontablet.ui.component.ParsRoyalTab;
import com.parsroyal.solutiontablet.ui.component.TabContainer;
import com.parsroyal.solutiontablet.util.DialogUtil;
import com.parsroyal.solutiontablet.util.Logger;
import com.parsroyal.solutiontablet.util.ToastUtil;
import com.parsroyal.solutiontablet.util.constants.ApplicationKeys;
import java.util.Locale;

/**
 * Created by Mahyar on 8/25/2015.
 */
public class OrderDetailFragment extends BaseFragment {

  public static final String TAG = OrderDetailFragment.class.getSimpleName();
  private OldMainActivity context;
  private SaleOrderService saleOrderService;
  private VisitServiceImpl visitService;
  private SettingService settingService;
  private SaleOrderDto order;

  private Long orderId;
  private long visitId;
  private String saleType;

  private TabContainer tabContainer;
  private LinearLayout actionsLayout;
  private Button deliverOrderBtn;
  private Button cancelOrderBtn;
  private Button saveOrderBtn;

  private Long orderStatus;
  private GoodsDtoList rejectedGoodsList;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    try {
      context = (OldMainActivity) getActivity();
      saleOrderService = new SaleOrderServiceImpl(context);
      visitService = new VisitServiceImpl(context);
      settingService = new SettingServiceImpl(context);

      Bundle arguments = getArguments();
      orderId = arguments.getLong(Constants.ORDER_ID);
      saleType = arguments.getString(Constants.SALE_TYPE);
      visitId = arguments.getLong(Constants.VISIT_ID);

      order = saleOrderService.findOrderDtoById(orderId);
      orderStatus = order.getStatus();
      View view = context.getLayoutInflater().inflate(R.layout.fragment_order_detail, null);
      tabContainer = view.findViewById(R.id.tabContainer);
      actionsLayout = view.findViewById(R.id.actionsLayout);

      if (isRejected()) {
        rejectedGoodsList = (GoodsDtoList) arguments.getSerializable(Constants.REJECTED_LIST);
      }

      ParsRoyalTab tab = new ParsRoyalTab(context);

      tab.setText(String.format(Locale.US, getString(R.string.title_items_x), getProperTitle()));

      tab.setOnClickListener(v -> {
        FragmentManager childFragMan = getChildFragmentManager();
        FragmentTransaction childFragTrans = childFragMan.beginTransaction();
        OldOrderItemsFragment oldOrderItemsFragment = new OldOrderItemsFragment();

        Bundle args = new Bundle();
        args.putLong(Constants.ORDER_ID, order.getId());
        args.putBoolean(Constants.DISABLED, isDisable());
        args.putLong(Constants.ORDER_STATUS, orderStatus);
        args.putSerializable(Constants.REJECTED_LIST, rejectedGoodsList);
        oldOrderItemsFragment.setArguments(args);

        childFragTrans.replace(R.id.orderDetailContentFrame, oldOrderItemsFragment);
        childFragTrans.commit();

      });
      tabContainer.addTab(tab);

      if (orderStatus.equals(SaleOrderStatus.DRAFT.getId())
          || orderStatus.equals(SaleOrderStatus.READY_TO_SEND.getId())
          || orderStatus.equals(SaleOrderStatus.REJECTED_DRAFT.getId())) {
        ParsRoyalTab tab2 = new ParsRoyalTab(context);
        tab2.setText(getString(R.string.title_goods_list));
        tab2.setActivated(true);
        tab2.setOnClickListener(v -> {
          FragmentManager childFragMan = getChildFragmentManager();
          FragmentTransaction childFragTrans = childFragMan.beginTransaction();

          OldGoodsListFragment oldGoodsListFragment = new OldGoodsListFragment();
          Bundle args = new Bundle();
          args.putLong(Constants.ORDER_ID, orderId);
          args.putSerializable(Constants.REJECTED_LIST, rejectedGoodsList);
          oldGoodsListFragment.setArguments(args);

          childFragTrans.replace(R.id.orderDetailContentFrame, oldGoodsListFragment);
          childFragTrans.commit();
        });
        tabContainer.addTab(tab2);
        tabContainer.activeTab(tab2);
      }

      actionsLayout.addView(createActionButton(context.getString(R.string.title_cancel),
          v -> context.removeFragment(OrderDetailFragment.this)));

      if (orderStatus.equals(SaleOrderStatus.DELIVERABLE.getId())) {

        cancelOrderBtn = createActionButton(
            String.format(Locale.US, getString(R.string.title_cancel_sale_x), getProperTitle()),
            v -> showSaveOrderConfirmDialog(
                String.format(Locale.US, getString(R.string.title_cancel_sale_x),
                    getProperTitle()), SaleOrderStatus.CANCELED.getId()));

        deliverOrderBtn = createActionButton(getString(R.string.title_deliver_sale_x), v -> {
          if (validateOrderForDeliver()) {
            showSaveOrderConfirmDialog(getString(R.string.title_deliver_sale_x)
                + getProperTitle(), SaleOrderStatus.INVOICED.getId());
          }
        });
        actionsLayout.addView(cancelOrderBtn);
        actionsLayout.addView(deliverOrderBtn);
      }

      if (SaleOrderStatus.READY_TO_SEND.getId().equals(orderStatus)
          || SaleOrderStatus.DRAFT.getId().equals(orderStatus)
          || SaleOrderStatus.REJECTED_DRAFT.getId().equals(orderStatus)) {
        saveOrderBtn = createActionButton(context.getString(R.string.title_save_order), v -> {
          order = saleOrderService.findOrderDtoById(orderId);
          if (validateOrderForSave()) {
            if (orderStatus.equals(SaleOrderStatus.REJECTED_DRAFT.getId())) {
              showSaveOrderConfirmDialog(getString(R.string.title_save_order),
                  SaleOrderStatus.REJECTED.getId());
            } else {
              if (isCold()) {
                showSaveOrderConfirmDialog(getString(R.string.title_save_order),
                    SaleOrderStatus.READY_TO_SEND.getId());
              } else {
                showSaveOrderConfirmDialog(getString(R.string.title_save_order),
                    SaleOrderStatus.INVOICED.getId());
              }
            }
          }
        });
        actionsLayout.addView(saveOrderBtn);
      }

      return view;
    } catch (Exception e) {
      ToastUtil.toastError(getActivity(), new UnknownSystemException(e));
      return inflater.inflate(R.layout.view_error_page, null);
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

  /*
   @return Proper title for which could be "Rejected", "Order" or "Invoice"
   */
  private String getProperTitle() {
    if (orderStatus.equals(SaleOrderStatus.REJECTED_DRAFT.getId()) ||
        orderStatus.equals(SaleOrderStatus.REJECTED.getId()) ||
        orderStatus.equals(SaleOrderStatus.REJECTED_SENT.getId())) {
      return getString(R.string.title_reject);
    } else if (isCold()) {
      return getString(R.string.title_order);
    } else {
      return getString(R.string.title_factor);
    }
  }

  private boolean validateOrderForSave() {
    if (order.getOrderItems().size() == 0) {
      ToastUtil.toastError(context,
          getProperTitle() + getString(R.string.message_x_has_no_item_for_save));
      return false;
    }
    return true;
  }

  private boolean validateOrderForDeliver() {
    if (order.getOrderItems().size() == 0) {
      ToastUtil.toastError(context, R.string.message_order_has_no_item);
      return false;
    }
    return true;
  }

  private void showSaveOrderConfirmDialog(String title, final Long statusId) {
    DialogUtil.showConfirmDialog(context, title,
        context.getString(R.string.message_are_you_sure), (dialog, which) -> {
          saveOrder(statusId);
          context.removeFragment(OrderDetailFragment.this);
        });
  }

  private void saveOrder(Long statusId) {
    try {
      order.setStatus(statusId);

      long selectedPaymentType = 0;//orderInfoFrg.getSelectedPaymentType();
      if (isRejected()) {
        //Add reason or reject to orders
      } else {
        order.setPaymentTypeBackendId(selectedPaymentType);
      }

      String description = "";//orderInfoFrg.getDescription();
      order.setDescription(description);

      //Distributer should not enter his salesmanId.
      if (!SaleOrderStatus.DELIVERABLE.getId().equals(orderStatus)) {
        order.setSalesmanId(
            Long.valueOf(settingService.getSettingValue(ApplicationKeys.SALESMAN_ID)));
      }
      long typeId = saleOrderService.saveOrder(order);

      VisitInformationDetail visitDetail = new VisitInformationDetail(visitId, getDetailType(),
          typeId);
      visitService.saveVisitDetail(visitDetail);
    } catch (BusinessException ex) {
      Log.e(TAG, ex.getMessage(), ex);
      ToastUtil.toastError(context, ex);
    } catch (Exception ex) {
      Logger.sendError("Data Storage Exception",
          "Error in saving new order detail " + ex.getMessage());
      Log.e(TAG, ex.getMessage(), ex);
      ToastUtil.toastError(context, new UnknownSystemException(ex));
    }
  }

  private VisitInformationDetailType getDetailType() {
    if (isRejected()) {
      return VisitInformationDetailType.CREATE_REJECT;
    }
    switch (saleType) {
      case ApplicationKeys.SALE_COLD:
        return VisitInformationDetailType.CREATE_ORDER;
      case ApplicationKeys.SALE_HOT:
        return VisitInformationDetailType.CREATE_INVOICE;
      case ApplicationKeys.SALE_DISTRIBUTER:
        return VisitInformationDetailType.DELIVER_ORDER;
    }
    //Should not happen
    return null;
  }

  private Button createActionButton(String buttonTitle, View.OnClickListener onClickListener) {
    Button button = new Button(context);
    button.setText(buttonTitle);
    button.setOnClickListener(onClickListener);
    return button;
  }

  @Override
  public int getFragmentId() {
    return OldMainActivity.ORDER_DETAIL_FRAGMENT_ID;
  }

  private boolean isDisable() {
    if (orderStatus.equals(SaleOrderStatus.CANCELED.getId())
        || orderStatus.equals(SaleOrderStatus.INVOICED.getId())
        || orderStatus.equals(SaleOrderStatus.SENT.getId())
        || orderStatus.equals(SaleOrderStatus.SENT_INVOICE.getId())
        || orderStatus.equals(SaleOrderStatus.REJECTED_SENT.getId())
        || orderStatus.equals(SaleOrderStatus.REJECTED.getId())
        ) {
      return true;
    }
    return false;
  }
}
