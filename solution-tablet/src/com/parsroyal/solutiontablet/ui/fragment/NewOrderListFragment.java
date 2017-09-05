package com.parsroyal.solutiontablet.ui.fragment;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.parsroyal.solutiontablet.R;
import com.parsroyal.solutiontablet.constants.StatusCodes;
import com.parsroyal.solutiontablet.data.event.ActionEvent;
import com.parsroyal.solutiontablet.data.listmodel.SaleOrderListModel;
import com.parsroyal.solutiontablet.data.searchobject.SaleOrderSO;
import com.parsroyal.solutiontablet.service.impl.SaleOrderServiceImpl;
import com.parsroyal.solutiontablet.ui.MainActivity;
import com.parsroyal.solutiontablet.ui.adapter.OrderAdapter;
import java.util.List;
import org.greenrobot.eventbus.EventBus;

public class NewOrderListFragment extends BaseFragment {

  @BindView(R.id.recycler_view)
  RecyclerView recyclerView;
  @BindView(R.id.fab_add_order)
  FloatingActionButton fabAddOrder;
  private OrderAdapter adapter;
  private MainActivity mainActivity;
  private SaleOrderServiceImpl saleOrderService;
  private SaleOrderSO saleOrderSO = new SaleOrderSO();
  private NewVisitDetailFragment parent;

  public NewOrderListFragment() {
    // Required empty public constructor
  }


  public static NewOrderListFragment newInstance(NewVisitDetailFragment newVisitDetailFragment) {
    NewOrderListFragment newOrderListFragment = new NewOrderListFragment();
    newOrderListFragment.parent = newVisitDetailFragment;
    return newOrderListFragment;
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    View view = inflater.inflate(R.layout.fragment_new_order_list, container, false);
    mainActivity = (MainActivity) getActivity();
    ButterKnife.bind(this, view);
    this.saleOrderService = new SaleOrderServiceImpl(mainActivity);
    if (parent == null) {
      fabAddOrder.setVisibility(View.GONE);
    }
    setUpRecyclerView();
    return view;
  }

  //set up recycler view
  private void setUpRecyclerView() {
    adapter = new OrderAdapter(mainActivity, getOrderList(), parent == null);
    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
    recyclerView.setLayoutManager(linearLayoutManager);
    recyclerView.setAdapter(adapter);
    recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
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

  private List<SaleOrderListModel> getOrderList() {
    if (parent != null) {
      saleOrderSO.setCustomerBackendId(parent.getCustomer().getBackendId());
    }
    saleOrderSO.setIgnoreDraft(true);
    return saleOrderService.findOrders(saleOrderSO);
  }

  @Override
  public int getFragmentId() {
    return 0;
  }

  @OnClick(R.id.fab_add_order)
  public void onClick() {
    EventBus.getDefault().post(new ActionEvent(StatusCodes.ACTION_ADD_ORDER));
  }
}
