package com.parsroyal.solutiontablet.ui.fragment;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

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


public class CustomerSearchFragment extends BaseFragment {

  @BindView(R.id.search_img) ImageView searchImg;
  @BindView(R.id.search_edt) EditText searchEdt;
  @BindView(R.id.recycler_view) RecyclerView recyclerView;

  private CustomerService customerService;
  private MainActivity activity;
  private boolean isClose = false;
  private SystemCustomerAdapter adapter;

  public CustomerSearchFragment() {
    // Required empty public constructor
  }

  public static CustomerSearchFragment newInstance() {
    CustomerSearchFragment fragment = new CustomerSearchFragment();
    return fragment;
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    View view = inflater.inflate(R.layout.fragment_customer_search, container, false);
    ButterKnife.bind(this, view);
    activity = (MainActivity) getActivity();
    activity.changeTitle(getString(R.string.search));
    customerService = new CustomerServiceImpl(activity);
    setUpRecyclerView();
    onSearchTextChanged();
    return view;
  }

  //set up recycler view
  private void setUpRecyclerView() {
    adapter = new SystemCustomerAdapter(activity, getCustomersList());
    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
    recyclerView.setLayoutManager(linearLayoutManager);
    recyclerView.setAdapter(adapter);
  }

  private void onSearchTextChanged() {
    searchEdt.addTextChangedListener(new TextWatcher() {
      @Override
      public void beforeTextChanged(CharSequence s, int start, int count, int after) {
      }

      @Override
      public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (TextUtils.isEmpty(s.toString())) {
          isClose = false;
          searchImg.setImageResource(R.drawable.ic_search);
        } else {
          isClose = true;
          searchImg.setImageResource(R.drawable.ic_close_24dp);
        }
      }

      @Override
      public void afterTextChanged(Editable s) {
        //TODO:PLEASE ADD SEARCH LOGIC
//        new PathDetailFragment.RefreshAsyncTask().execute(s.toString());
      }
    });
  }

  private List<CustomerListModel> getCustomersList() {
    return customerService.getFilteredCustomerList(null,"");
  }

  @Override public int getFragmentId() {
    return MainActivity.CUSTOMER_SEARCH_FRAGMENT;
  }

  @OnClick({R.id.search_img, R.id.back_img}) public void onClick(View view) {
    switch (view.getId()) {
      case R.id.search_img:
        if (isClose) {
          searchEdt.setText("");
        }
        break;
      case R.id.back_img:
        activity.onBackPressed();
        break;
    }
  }
}
