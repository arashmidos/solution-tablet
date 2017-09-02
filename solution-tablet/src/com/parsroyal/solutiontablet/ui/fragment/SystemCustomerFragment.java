package com.parsroyal.solutiontablet.ui.fragment;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.parsroyal.solutiontablet.R;
import com.parsroyal.solutiontablet.data.listmodel.CustomerListModel;
import com.parsroyal.solutiontablet.service.CustomerService;
import com.parsroyal.solutiontablet.service.impl.CustomerServiceImpl;
import com.parsroyal.solutiontablet.ui.MainActivity;
import com.parsroyal.solutiontablet.ui.adapter.SystemCustomerAdapter;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class SystemCustomerFragment extends Fragment {

  @BindView(R.id.recycler_view) RecyclerView recyclerView;
  @BindView(R.id.fab_add_customer) FloatingActionButton fabAddCustomer;

  private MainActivity activity;
  private CustomerService customerService;
  private SystemCustomerAdapter adapter;

  public SystemCustomerFragment() {
    // Required empty public constructor
  }

  public static SystemCustomerFragment newInstance() {
    SystemCustomerFragment fragment = new SystemCustomerFragment();
    return fragment;
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    View view = inflater.inflate(R.layout.fragment_system_customer, container, false);
    ButterKnife.bind(this, view);
    activity = (MainActivity) getActivity();
    customerService = new CustomerServiceImpl(activity);
    setUpRecyclerView();
    return view;
  }

  //set up recycler view
  private void setUpRecyclerView() {
    adapter = new SystemCustomerAdapter(activity, getCustomersList());
    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
    recyclerView.setLayoutManager(linearLayoutManager);
    recyclerView.setAdapter(adapter);
    recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
      @Override public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
        if (dy > 0) {
          fabAddCustomer.setVisibility(View.GONE);
        } else {
          fabAddCustomer.setVisibility(View.VISIBLE);
        }
      }
    });
  }

  private List<CustomerListModel> getCustomersList() {
    return customerService.getAllCustomersListModelByVisitLineBackendId(3911l);
  }

  @OnClick({R.id.sort_lay, R.id.filter_lay, R.id.fab_add_customer}) public void onClick(View view) {
    switch (view.getId()) {
      case R.id.sort_lay:
        Toast.makeText(activity, "sort", Toast.LENGTH_SHORT).show();
        break;
      case R.id.filter_lay:
        Toast.makeText(activity, "filter", Toast.LENGTH_SHORT).show();
        break;
      case R.id.fab_add_customer:
        activity.changeFragment(MainActivity.ADD_CUSTOMER_FRAGMENT, true);
        break;
    }
  }

}
