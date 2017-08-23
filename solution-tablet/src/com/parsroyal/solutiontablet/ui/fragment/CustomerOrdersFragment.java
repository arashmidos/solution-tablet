package com.parsroyal.solutiontablet.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.parsroyal.solutiontablet.R;
import com.parsroyal.solutiontablet.ui.MainActivity;


public class CustomerOrdersFragment extends BaseFragment {


  public CustomerOrdersFragment() {
    // Required empty public constructor
  }


  public static CustomerOrdersFragment newInstance() {
    CustomerOrdersFragment fragment = new CustomerOrdersFragment();
    return fragment;
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    View view = inflater.inflate(R.layout.fragment_customer_orders, container, false);
    return view;
  }

  @Override public int getFragmentId() {
    return MainActivity.CUSTOMER_ORDER_FRAGMENT_ID;
  }
}
