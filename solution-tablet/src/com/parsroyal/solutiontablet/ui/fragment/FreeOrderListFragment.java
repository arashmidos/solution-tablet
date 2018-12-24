package com.parsroyal.solutiontablet.ui.fragment;

import android.content.res.ColorStateList;
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
import com.parsroyal.solutiontablet.R;
import com.parsroyal.solutiontablet.constants.Constants;
import com.parsroyal.solutiontablet.constants.SaleOrderStatus;
import com.parsroyal.solutiontablet.constants.StatusCodes;
import com.parsroyal.solutiontablet.data.event.ActionEvent;
import com.parsroyal.solutiontablet.data.listmodel.SaleOrderListModel;
import com.parsroyal.solutiontablet.data.searchobject.SaleOrderSO;
import com.parsroyal.solutiontablet.service.impl.SaleOrderServiceImpl;
import com.parsroyal.solutiontablet.service.impl.SettingServiceImpl;
import com.parsroyal.solutiontablet.ui.activity.MainActivity;
import com.parsroyal.solutiontablet.ui.adapter.OrderAdapter;
import com.parsroyal.solutiontablet.util.Empty;
import com.parsroyal.solutiontablet.util.NumberUtil;
import java.util.List;
import java.util.Locale;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

/**
 * @author arash
 */
public class FreeOrderListFragment extends BaseFragment {

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

  public FreeOrderListFragment() {
    // Required empty public constructor
  }

  public static FreeOrderListFragment newInstance(Bundle arguments, VisitDetailFragment parent) {
    FreeOrderListFragment freeOrderListFragment = new FreeOrderListFragment();
    freeOrderListFragment.parent = parent;
    freeOrderListFragment.setArguments(arguments);
    return freeOrderListFragment;
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
    fabAddOrder
        .setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.free_order)));

    return view;
  }

  private void displayTotalSale() {

    long total = 0;
  /*  for (int i = 0; i < model.size(); i++) {
      total += model.get(i).getAmount();
    }*/

    totalOrderSale.setText(NumberUtil.digitsToPersian(model.size()));
    String number = String
        .format(Locale.US, "%,d %s", total / 1000, getString(R.string.common_irr_currency));
    totalOrderSaleAmount.setText(NumberUtil.digitsToPersian(number));
  }

  //set up recycler view
  private void setUpRecyclerView() {
    model = getOrderList();
    adapter = new OrderAdapter(mainActivity, model, parent == null, visitId, true);
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
    saleOrderSO.setStatusId(SaleOrderStatus.FREE_ORDER_DELIVERED.getId());
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
      parent.openFreeOrderDetailFragment();
    }
  }
}
