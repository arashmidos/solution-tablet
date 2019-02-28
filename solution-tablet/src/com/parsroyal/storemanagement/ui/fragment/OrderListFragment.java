package com.parsroyal.storemanagement.ui.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.OnScrollListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.parsroyal.storemanagement.R;
import com.parsroyal.storemanagement.constants.Constants;
import com.parsroyal.storemanagement.constants.SaleOrderStatus;
import com.parsroyal.storemanagement.constants.StatusCodes;
import com.parsroyal.storemanagement.data.event.ActionEvent;
import com.parsroyal.storemanagement.data.listmodel.SaleOrderListModel;
import com.parsroyal.storemanagement.data.searchobject.SaleOrderSO;
import com.parsroyal.storemanagement.service.impl.SaleOrderServiceImpl;
import com.parsroyal.storemanagement.service.impl.SettingServiceImpl;
import com.parsroyal.storemanagement.ui.activity.MainActivity;
import com.parsroyal.storemanagement.ui.adapter.OrderAdapter;
import com.parsroyal.storemanagement.util.DialogUtil;
import com.parsroyal.storemanagement.util.Empty;
import com.parsroyal.storemanagement.util.NumberUtil;
import com.parsroyal.storemanagement.util.PreferenceHelper;
import com.parsroyal.storemanagement.util.constants.ApplicationKeys;
import java.util.List;
import java.util.Locale;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

/**
 * @author arash
 */
public class OrderListFragment extends BaseFragment {

  protected Long visitId;
  protected OrderAdapter adapter;
  protected MainActivity mainActivity;
  protected SaleOrderServiceImpl saleOrderService;
  protected SaleOrderSO saleOrderSO = new SaleOrderSO();
  protected VisitDetailFragment parent;
  protected SettingServiceImpl settingService;
  protected List<SaleOrderListModel> model;
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

  public OrderListFragment() {
    // Required empty public constructor
  }

  public static OrderListFragment newInstance(Bundle arguments, VisitDetailFragment parent) {
    OrderListFragment orderListFragment = new OrderListFragment();
    orderListFragment.parent = parent;
    orderListFragment.setArguments(arguments);
    return orderListFragment;
  }

  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    View view = inflater.inflate(R.layout.fragment_new_order_list, container, false);
    mainActivity = (MainActivity) getActivity();
    ButterKnife.bind(this, view);
    Bundle args = getArguments();
    saleOrderService = new SaleOrderServiceImpl(mainActivity);
    settingService = new SettingServiceImpl();

    if (args != null) {
      visitId = args.getLong(Constants.VISIT_ID, -1);
    }
    setUpRecyclerView();
    if (parent == null) {
      //Report
      fabAddOrder.setVisibility(View.GONE);
      totalSale.setVisibility(View.VISIBLE);
      displayTotalSale();
    }
    if (PreferenceHelper.isDistributor()) {
      fabAddOrder.setVisibility(View.GONE);
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
  }

  //set up recycler view
  private void setUpRecyclerView() {
    model = getOrderList();
    adapter = new OrderAdapter(mainActivity, model, parent == null, false);
    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
    recyclerView.setLayoutManager(linearLayoutManager);
    recyclerView.setAdapter(adapter);
    if (!Empty.isEmpty(getArguments())) {
      recyclerView.addOnScrollListener(new OnScrollListener() {
        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
          super.onScrolled(recyclerView, dx, dy);
          if (dy > 0) {
            fabAddOrder.setVisibility(View.GONE);
          } else {
            fabAddOrder.setVisibility(View.VISIBLE);
          }
        }
      });
    }
  }

  private List<SaleOrderListModel> getOrderList() {
    //If not coming from report page
    if (parent != null) {
      saleOrderSO.setCustomerBackendId(parent.getCustomer().getBackendId());
    }
    saleOrderSO.setIgnoreDraft(true);
    saleOrderSO.setStatusId(SaleOrderStatus.READY_TO_SEND.getId());
    return saleOrderService.findOrders(saleOrderSO);
  }

  @Override
  public int getFragmentId() {
    return 0;
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

  @OnClick(R.id.fab_add_order)
  public void onClick() {
    if (parent != null) {
      String checkCredit = settingService
          .getSettingValue(ApplicationKeys.SETTING_CHECK_CREDIT_ENABLE);
      boolean checkCreditEnabled = Empty.isEmpty(checkCredit) || "null".equals(checkCredit) ? false
          : Boolean.valueOf(checkCredit);
      if (checkCreditEnabled && parent.getCustomer().getRemainedCredit() != null
          && parent.getCustomer().getRemainedCredit().longValue() <= 0) {
        DialogUtil.showConfirmDialog(mainActivity, getString(R.string.warning),
            "ثبت سفارش فقط با پرداخت نقدی امکان پذیر است", getString(R.string.register_order),
            (dialogInterface, i) -> parent
                .openOrderDetailFragment(SaleOrderStatus.DRAFT.getId(), true),
            getString(R.string.cancel));
      } else {
        parent.openOrderDetailFragment(SaleOrderStatus.DRAFT.getId(), false);
      }
    }
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
  }
}
