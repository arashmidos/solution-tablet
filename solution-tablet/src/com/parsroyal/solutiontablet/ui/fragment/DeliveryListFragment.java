package com.parsroyal.solutiontablet.ui.fragment;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import com.parsroyal.solutiontablet.R;
import com.parsroyal.solutiontablet.constants.Constants;
import com.parsroyal.solutiontablet.constants.SaleOrderStatus;
import com.parsroyal.solutiontablet.constants.StatusCodes;
import com.parsroyal.solutiontablet.data.event.ActionEvent;
import com.parsroyal.solutiontablet.data.listmodel.SaleOrderListModel;
import com.parsroyal.solutiontablet.data.searchobject.SaleOrderSO;
import com.parsroyal.solutiontablet.service.impl.SaleOrderServiceImpl;
import com.parsroyal.solutiontablet.service.impl.SettingServiceImpl;
import com.parsroyal.solutiontablet.ui.MainActivity;
import com.parsroyal.solutiontablet.ui.adapter.DeliveryAdapter;
import com.parsroyal.solutiontablet.util.NumberUtil;
import com.parsroyal.solutiontablet.util.constants.ApplicationKeys;
import java.util.List;
import java.util.Locale;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

/**
 * @author arash
 */
public class DeliveryListFragment extends BaseFragment {

  @BindView(R.id.recycler_view)
  RecyclerView recyclerView;
  @BindView(R.id.fab_add_order)
  FloatingActionButton fabAddOrder;
  @BindView(R.id.total_order_sale_amount)
  TextView totalOrderSaleAmount;
  @BindView(R.id.total_order_sale)
  TextView totalOrderSale;
  @BindView(R.id.total_sale)
  LinearLayout totalSale;
  @BindView(R.id.total_order_sale_amount_tv)
  TextView totalOrderSaleAmountTv;
  @BindView(R.id.total_order_sale_tv)
  TextView totalOrderSaleTv;

  private Long visitId;
  private DeliveryAdapter adapter;
  private MainActivity mainActivity;
  private SaleOrderServiceImpl saleOrderService;
  private SaleOrderSO saleOrderSO = new SaleOrderSO();
  private VisitDetailFragment parent;
  private SettingServiceImpl settingService;
  private String saleType;
  private List<SaleOrderListModel> model;
  private Unbinder unbinder;
  private long visitlineBackendId;

  public DeliveryListFragment() {
    // Required empty public constructor
  }

  public static DeliveryListFragment newInstance(Bundle arguments, VisitDetailFragment parent) {
    DeliveryListFragment orderListFragment = new DeliveryListFragment();
    orderListFragment.parent = parent;
    orderListFragment.setArguments(arguments);
    return orderListFragment;
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    View view = inflater.inflate(R.layout.fragment_new_order_list, container, false);
    mainActivity = (MainActivity) getActivity();
    unbinder = ButterKnife.bind(this, view);
    Bundle args = getArguments();
    saleOrderService = new SaleOrderServiceImpl(mainActivity);
    settingService = new SettingServiceImpl(mainActivity);

    saleType = settingService.getSettingValue(ApplicationKeys.SETTING_SALE_TYPE);

    if (args != null) {
      visitId = args.getLong(Constants.VISIT_ID, -1);
      visitlineBackendId = args.getLong(Constants.VISITLINE_BACKEND_ID);
    }
    setUpRecyclerView();
    fabAddOrder.setVisibility(View.GONE);
    if (parent == null) {
      //Report
      totalSale.setVisibility(View.VISIBLE);
      displayTotalSale();//TODO:
    }

    return view;
  }

  private void displayTotalSale() {

    long total = 0;
    for (int i = 0; i < model.size(); i++) {
      total += model.get(i).getAmount();
    }

    totalOrderSale.setText(NumberUtil.digitsToPersian(model.size()));
    String number = String
        .format(Locale.US, "%,d %s", total / 1000, getString(R.string.common_irr_currency));
    totalOrderSaleAmount.setText(NumberUtil.digitsToPersian(number));
    totalOrderSaleAmountTv.setText(R.string.total_invoice_sale_tv);
    totalOrderSaleTv.setText(R.string.total_invoice_sale_count_tv);
  }

  //set up recycler view
  private void setUpRecyclerView() {
    model = getOrderList();
    adapter = new DeliveryAdapter(mainActivity, model, parent == null, visitId, saleType,
        visitlineBackendId);
    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
    recyclerView.setLayoutManager(linearLayoutManager);
    recyclerView.setAdapter(adapter);
  }

  private List<SaleOrderListModel> getOrderList() {
    if (parent != null) {
      saleOrderSO.setCustomerBackendId(parent.getCustomer().getBackendId());
      saleOrderSO.setStatusId(SaleOrderStatus.DELIVERABLE.getId());
    } else {
      saleOrderSO.setStatusId(SaleOrderStatus.DELIVERED.getId());
    }
    saleOrderSO.setIgnoreDraft(true);
    return saleOrderService.findOrders(saleOrderSO);
  }

  @Override
  public int getFragmentId() {
    return MainActivity.DELIVERY_FRAGMENT_ID;
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
  public void getMessage(ActionEvent event) {
    if (event.getStatusCode().equals(StatusCodes.ACTION_REFRESH_DATA)) {
      model = getOrderList();
      adapter.update(model);
    }
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    unbinder.unbind();
  }
}
