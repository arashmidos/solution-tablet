package com.parsroyal.solutiontablet.ui.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import com.crashlytics.android.Crashlytics;
import com.parsroyal.solutiontablet.R;
import com.parsroyal.solutiontablet.constants.Constants;
import com.parsroyal.solutiontablet.constants.SaleOrderStatus;
import com.parsroyal.solutiontablet.constants.StatusCodes;
import com.parsroyal.solutiontablet.data.event.ErrorEvent;
import com.parsroyal.solutiontablet.data.event.Event;
import com.parsroyal.solutiontablet.data.event.SendOrderEvent;
import com.parsroyal.solutiontablet.data.listmodel.SaleOrderListModel;
import com.parsroyal.solutiontablet.data.model.GoodsDtoList;
import com.parsroyal.solutiontablet.data.searchobject.SaleOrderSO;
import com.parsroyal.solutiontablet.exception.BusinessException;
import com.parsroyal.solutiontablet.exception.UnknownSystemException;
import com.parsroyal.solutiontablet.service.DataTransferService;
import com.parsroyal.solutiontablet.service.SaleOrderService;
import com.parsroyal.solutiontablet.service.impl.DataTransferServiceImpl;
import com.parsroyal.solutiontablet.service.impl.SaleOrderServiceImpl;
import com.parsroyal.solutiontablet.service.impl.SettingServiceImpl;
import com.parsroyal.solutiontablet.ui.OldMainActivity;
import com.parsroyal.solutiontablet.ui.adapter.SaleOrderListAdapter;
import com.parsroyal.solutiontablet.ui.component.ParsRoyalTab;
import com.parsroyal.solutiontablet.ui.observer.ResultObserver;
import com.parsroyal.solutiontablet.util.Empty;
import com.parsroyal.solutiontablet.util.ToastUtil;
import com.parsroyal.solutiontablet.util.constants.ApplicationKeys;
import java.util.List;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

/**
 * Created by Mahyar on 8/25/2015.
 */
public class OldOrdersListFragment extends
    BaseListFragment<SaleOrderListModel, SaleOrderListAdapter> implements ResultObserver {

  private static final String TAG = OldOrdersListFragment.class.getSimpleName();

  private OldMainActivity context;
  private SaleOrderSO saleOrderSO;
  private SaleOrderService saleOrderService;
  private long visitId;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    context = (OldMainActivity) getActivity();
    this.saleOrderService = new SaleOrderServiceImpl(context);
    String saleType = new SettingServiceImpl(context)
        .getSettingValue(ApplicationKeys.SETTING_SALE_TYPE);

    //Set default list to show

    saleOrderSO = new SaleOrderSO();
    Bundle args = getArguments();
    if (Empty.isNotEmpty(args)) {
      Object statusId = args.get(Constants.STATUS_ID);
      saleOrderSO.setStatusId(Empty.isNotEmpty(statusId) ? (Long) statusId : null);
      Object customerBackendId = args.get(Constants.CUSTOMER_BACKEND_ID);
      saleOrderSO.setCustomerBackendId(
          Empty.isNotEmpty(customerBackendId) ? (Long) customerBackendId : null);
      visitId = args.getLong(Constants.VISIT_ID);
    }
    if (Empty.isEmpty(saleOrderSO.getStatusId())) {
      if (ApplicationKeys.SALE_COLD.equals(saleType)) {
        saleOrderSO.setStatusId(SaleOrderStatus.READY_TO_SEND.getId());
      } else {
        saleOrderSO.setStatusId(SaleOrderStatus.INVOICED.getId());
      }
    }

    dataModel = saleOrderService.findOrders(saleOrderSO);

    View view = super.onCreateView(inflater, container, savedInstanceState);

    setupGeneralTabs();
    if (ApplicationKeys.SALE_COLD.equals(saleType) || ApplicationKeys.SALE_DISTRIBUTER
        .equals(saleType)) {
      setupColdSaleTabs();
    }

    return view;
  }

  private void setupColdSaleTabs() {
    {
      ParsRoyalTab tab = new ParsRoyalTab(context);
      tab.setText(SaleOrderStatus.getDisplayTitle(context, SaleOrderStatus.DELIVERABLE.getId()));
      tab.setOnClickListener(v ->
      {
        saleOrderSO.setStatusId(SaleOrderStatus.DELIVERABLE.getId());
        dataModel = saleOrderService.findOrders(saleOrderSO);
        updateList();
      });
      tabContainer.addTab(tab);
    }

    {
      ParsRoyalTab tab = new ParsRoyalTab(context);
      tab.setText(SaleOrderStatus.getDisplayTitle(context, SaleOrderStatus.SENT.getId()));
      tab.setOnClickListener(v ->
      {
        saleOrderSO.setStatusId(SaleOrderStatus.SENT.getId());
        dataModel = saleOrderService.findOrders(saleOrderSO);
        updateList();
      });
      tabContainer.addTab(tab);
    }

    {
      ParsRoyalTab tab = new ParsRoyalTab(context);
      tab.setText(SaleOrderStatus.getDisplayTitle(context, SaleOrderStatus.READY_TO_SEND.getId()));
      tab.setActivated(true);
      tab.setOnClickListener(v ->
      {
        saleOrderSO.setStatusId(SaleOrderStatus.READY_TO_SEND.getId());
        dataModel = saleOrderService.findOrders(saleOrderSO);
        updateList();
      });
      tabContainer.addTab(tab);
    }
  }

  private void setupGeneralTabs() {
    //Rejected and sent Order
    {
      ParsRoyalTab tab = new ParsRoyalTab(context);
      tab.setText(SaleOrderStatus.getDisplayTitle(context, SaleOrderStatus.REJECTED_SENT.getId()));
      tab.setOnClickListener(v ->
      {
        saleOrderSO.setStatusId(SaleOrderStatus.REJECTED_SENT.getId());
        updateList();
      });
      tabContainer.addTab(tab);
    }

    //Rejected Order
    {
      ParsRoyalTab tab = new ParsRoyalTab(context);
      tab.setText(SaleOrderStatus.getDisplayTitle(context, SaleOrderStatus.REJECTED.getId()));
      tab.setOnClickListener(v ->
      {
        saleOrderSO.setStatusId(SaleOrderStatus.REJECTED.getId());
        updateList();
      });
      tabContainer.addTab(tab);
    }

    {
      ParsRoyalTab tab = new ParsRoyalTab(context);
      tab.setText(SaleOrderStatus.getDisplayTitle(context, SaleOrderStatus.CANCELED.getId()));
      tab.setOnClickListener(v ->
      {
        saleOrderSO.setStatusId(SaleOrderStatus.CANCELED.getId());
        updateList();
      });
      tabContainer.addTab(tab);
    }
    {
      ParsRoyalTab tab = new ParsRoyalTab(context);
      tab.setText(SaleOrderStatus.getDisplayTitle(context, SaleOrderStatus.SENT_INVOICE.getId()));
      tab.setOnClickListener(v ->
      {
        saleOrderSO.setStatusId(SaleOrderStatus.SENT_INVOICE.getId());
        updateList();
      });
      tabContainer.addTab(tab);
    }

    {
      ParsRoyalTab tab = new ParsRoyalTab(context);
      tab.setText(SaleOrderStatus.getDisplayTitle(context, SaleOrderStatus.INVOICED.getId()));
      tab.setOnClickListener(v ->
      {
        saleOrderSO.setStatusId(SaleOrderStatus.INVOICED.getId());
        updateList();
      });
      tabContainer.addTab(tab);
    }
  }

  protected void updateList() {
    dataModel = saleOrderService.findOrders(saleOrderSO);
    adapter = getAdapter();
    dataModelLv.setAdapter(adapter);
  }

  @Override
  protected List<SaleOrderListModel> getDataModel() {
    return dataModel;
  }

  @Override
  protected View getHeaderView() {
    return null;
  }

  @Override
  protected SaleOrderListAdapter getAdapter() {
    return new SaleOrderListAdapter(context, getDataModel(), saleOrderSO);
  }

  @Override
  protected AdapterView.OnItemClickListener getOnItemClickListener() {
    return (parent, view, position, id) ->
    {
      SaleOrderListModel selectedOrder = dataModel.get(position);
      String saleType = new SettingServiceImpl(getActivity())
          .getSettingValue(ApplicationKeys.SETTING_SALE_TYPE);
      Bundle args = new Bundle();
      args.putLong(Constants.ORDER_ID, selectedOrder.getId());
      args.putString(Constants.SALE_TYPE, saleType);
      args.putLong(Constants.VISIT_ID, visitId);
      Long orderStatus = selectedOrder.getStatus();
      if (orderStatus.equals(SaleOrderStatus.REJECTED.getId()) ||
          orderStatus.equals(SaleOrderStatus.REJECTED_SENT.getId())) {
        requestLiveData(selectedOrder.getId(), selectedOrder.getCustomerBackendId(), orderStatus,
            saleType);
      } else {
        context.changeFragment(OldMainActivity.ORDER_DETAIL_FRAGMENT_ID, args, false);
      }
    };
  }

  private void requestLiveData(final Long id, final Long customerBackendId, final Long orderStatus,
      final String saleType) {
    Thread thread = new Thread(() ->
    {
      try {
        DataTransferService dataTransferService = new DataTransferServiceImpl(getActivity());
        GoodsDtoList rejectedGoodsList = null;/*dataTransferService
            .getRejectedData(OldOrdersListFragment.this, customerBackendId);*/
        //TODO ARASH Implement it for new ui
        if (rejectedGoodsList != null) {
          final Bundle args = new Bundle();
          args.putLong(Constants.ORDER_ID, id);
          args.putLong(Constants.ORDER_STATUS, orderStatus);
          args.putString(Constants.SALE_TYPE, saleType);
          args.putSerializable(Constants.REJECTED_LIST, rejectedGoodsList);
          getActivity().runOnUiThread(() -> ((OldMainActivity) getActivity())
              .changeFragment(OldMainActivity.ORDER_DETAIL_FRAGMENT_ID, args, false));
        } else {
          runOnUiThread(() -> ToastUtil.toastError(getActivity(),
              getString(R.string.err_reject_order_detail_not_accessable)));
        }
      } catch (final BusinessException ex) {
        Log.e(TAG, ex.getMessage(), ex);
        runOnUiThread(() -> ToastUtil.toastError(getActivity(), ex));
      } catch (final Exception ex) {
        Crashlytics
            .log(Log.ERROR, "Data transfer", "Error in requesting live data " + ex.getMessage());
        Log.e(TAG, ex.getMessage(), ex);
        runOnUiThread(() -> ToastUtil.toastError(getActivity(), new UnknownSystemException(ex)));
      }
    });

    thread.start();
  }

  @Override
  protected String getClassTag() {
    return TAG;
  }

  @Override
  protected String getTitle() {
    return "Title";
  }

  @Override
  public int getFragmentId() {
    return OldMainActivity.ORDERS_LIST_FRAGMENT;
  }

  @Override
  public void publishResult(final BusinessException ex) {
    runOnUiThread(() -> ToastUtil.toastError(getActivity(), getErrorString(ex)));
  }

  @Override
  public void publishResult(final String message) {
    runOnUiThread(() -> ToastUtil.toastMessage(getActivity(), message));
  }

  @Override
  public void finished(boolean result) {
  }

  @Override
  public void onStart() {
    super.onStart();
    EventBus.getDefault().register(this);
  }

  @Override
  public void onStop() {
    super.onStop();
    EventBus.getDefault().unregister(this);
  }

  @Subscribe
  public void getMessage(Event event) {
    if (event instanceof SendOrderEvent) {
      ToastUtil.toastMessage(getActivity(), event.getMessage());

      if (event.getStatusCode().equals(StatusCodes.SUCCESS)) {
        updateList();
      }
    } else if (event instanceof ErrorEvent) {
      ToastUtil.toastError(getActivity(), event.getStatusCode().toString());
    }
  }
}