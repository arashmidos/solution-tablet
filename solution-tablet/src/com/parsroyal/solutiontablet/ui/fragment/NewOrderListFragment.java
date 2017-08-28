package com.parsroyal.solutiontablet.ui.fragment;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.parsroyal.solutiontablet.R;
import com.parsroyal.solutiontablet.data.listmodel.SaleOrderListModel;
import com.parsroyal.solutiontablet.ui.MainActivity;
import com.parsroyal.solutiontablet.ui.adapter.OrderAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class NewOrderListFragment extends BaseFragment {

  @BindView(R.id.recycler_view) RecyclerView recyclerView;
  @BindView(R.id.fab_add_order) FloatingActionButton fabAddOrder;
  private OrderAdapter adapter;
  private MainActivity activity;

  public NewOrderListFragment() {
    // Required empty public constructor
  }


  public static NewOrderListFragment newInstance() {
    NewOrderListFragment fragment = new NewOrderListFragment();
    return fragment;
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    View view = inflater.inflate(R.layout.fragment_new_order_list, container, false);
    activity = (MainActivity) getActivity();
    ButterKnife.bind(this, view);
    setUpRecyclerView();
    return view;
  }

  //set up recycler view
  private void setUpRecyclerView() {
    adapter = new OrderAdapter(activity, getOrderList());
    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
    recyclerView.setLayoutManager(linearLayoutManager);
    recyclerView.setAdapter(adapter);
    recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
      @Override public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
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
    List<SaleOrderListModel> list = new ArrayList<>();
    list.add(new SaleOrderListModel(524525l, 234l, "پنجشنبه 2 مرداد 96", 25000000l, "چک 15 روزه", "", 2555l, 4554l));
    list.add(new SaleOrderListModel(32543254l, 234l, "شنبه 3 مرداد 96", 23500000l, "نقد", "", 2555l, 4554l));
    list.add(new SaleOrderListModel(56465l, 234l, "جمعه 4 مرداد 96", 4275000l, "چک 40 روزه", "", 2555l, 4554l));
    list.add(new SaleOrderListModel(568665l, 234l, "چهارشنبه 5 مرداد 96", 6325600l, "رسید", "", 2555l, 4554l));
    list.add(new SaleOrderListModel(5465l, 234l, "دوشنبه 30 مرداد 96", 75000000l, "نقد", "", 2555l, 4554l));
    list.add(new SaleOrderListModel(7365256465l, 234l, "سه شنبه 23 تیر 96", 6582122l, "چک 10 روزه", "", 2555l, 4554l));
    list.add(new SaleOrderListModel(378645l, 234l, "پنجشنبه 2 شهریور 96", 632000000l, "نقد", "", 2555l, 4554l));
    list.add(new SaleOrderListModel(678265l, 234l, "یکشنبه 2 اسفند 96", 248215623l, "رسید", "", 2555l, 4554l));
    list.add(new SaleOrderListModel(8968l, 234l, "شنبه 20 فروردین 96", 5263452l, "نقد", "", 2555l, 4554l));
    return list;
  }

  @Override public int getFragmentId() {
    return 0;
  }

  @OnClick(R.id.fab_add_order) public void onClick() {
    ((MainActivity) getActivity()).changeFragment(MainActivity.ORDER_FRAGMENT_ID, true);
  }
}
