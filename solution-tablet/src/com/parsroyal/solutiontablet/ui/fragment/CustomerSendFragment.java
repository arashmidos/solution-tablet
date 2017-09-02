package com.parsroyal.solutiontablet.ui.fragment;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.parsroyal.solutiontablet.R;
import com.parsroyal.solutiontablet.data.listmodel.NCustomerListModel;
import com.parsroyal.solutiontablet.data.searchobject.NCustomerSO;
import com.parsroyal.solutiontablet.service.CustomerService;
import com.parsroyal.solutiontablet.service.impl.CustomerServiceImpl;
import com.parsroyal.solutiontablet.ui.MainActivity;
import com.parsroyal.solutiontablet.ui.adapter.CustomerSendAdapter;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class CustomerSendFragment extends Fragment {

  @BindView(R.id.recycler_view) RecyclerView recyclerView;
  @BindView(R.id.fab_add_customer) FloatingActionButton fabAddCustomer;

  private boolean isSend;
  private CustomerSendAdapter adapter;
  private CustomerService customerService;
  private NCustomerSO nCustomerSO;
  private MainActivity activity;

  public CustomerSendFragment() {
    // Required empty public constructor
  }


  public static CustomerSendFragment newInstance(boolean isSend) {
    CustomerSendFragment fragment = new CustomerSendFragment();
    fragment.isSend = isSend;
    return fragment;
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    View view = inflater.inflate(R.layout.fragment_customer_send, container, false);
    ButterKnife.bind(this, view);
    activity = (MainActivity) getActivity();
    this.customerService = new CustomerServiceImpl(activity);
    this.nCustomerSO = new NCustomerSO();
    if (isSend) {
      nCustomerSO.setSent(1);
    } else {
      nCustomerSO.setSent(0);
    }
    setUpRecyclerView();
    return view;
  }

  //set up recycler view
  private void setUpRecyclerView() {
    adapter = new CustomerSendAdapter(activity, isSend, getCustomersList());
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

  private List<NCustomerListModel> getCustomersList() {
    return customerService.searchForNCustomers(nCustomerSO);
  }

  @OnClick(R.id.fab_add_customer) public void onClick() {
    activity.changeFragment(MainActivity.ADD_CUSTOMER_FRAGMENT, true);
  }
}
