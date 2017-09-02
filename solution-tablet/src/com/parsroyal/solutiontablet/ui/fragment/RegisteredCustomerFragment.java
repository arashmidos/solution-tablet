package com.parsroyal.solutiontablet.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.parsroyal.solutiontablet.R;
import com.parsroyal.solutiontablet.service.CustomerService;
import com.parsroyal.solutiontablet.service.impl.CustomerServiceImpl;
import com.parsroyal.solutiontablet.ui.MainActivity;

public class RegisteredCustomerFragment extends Fragment {


  private CustomerService customerService;
  private MainActivity activity;

  public RegisteredCustomerFragment() {
    // Required empty public constructor
  }


  public static RegisteredCustomerFragment newInstance() {
    RegisteredCustomerFragment fragment = new RegisteredCustomerFragment();
    return fragment;
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    View view = inflater.inflate(R.layout.fragment_registered_customer, container, false);
    activity = (MainActivity) getActivity();
    customerService = new CustomerServiceImpl(activity);
    return view;
  }
}
