package com.parsroyal.storemanagement.ui.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.parsroyal.storemanagement.R;
import com.parsroyal.storemanagement.ui.activity.MainActivity;
import com.parsroyal.storemanagement.ui.adapter.CustomerDetailViewPagerAdapter;

/**
 * @author Shakib
 */
public class CustomerFragment extends BaseFragment {

  @BindView(R.id.tabs)
  TabLayout tabs;
  @BindView(R.id.viewpager)
  ViewPager viewpager;

  private CustomerDetailViewPagerAdapter viewPagerAdapter;
  private MainActivity mainActivity;

  public CustomerFragment() {
    // Required empty public constructor
  }

  public static CustomerFragment newInstance() {
    return new CustomerFragment();
  }

  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    View view = inflater.inflate(R.layout.fragment_customer, container, false);
    ButterKnife.bind(this, view);
    mainActivity = (MainActivity) getActivity();
    mainActivity.changeTitle(getString(R.string.customers));
    tabs.setupWithViewPager(viewpager);
    setUpViewPager();
    viewpager.setCurrentItem(viewPagerAdapter.getCount());
    return view;
  }

  private void setUpViewPager() {
    viewPagerAdapter = new CustomerDetailViewPagerAdapter(
        getActivity().getSupportFragmentManager());
    viewPagerAdapter.add(NewCustomerFragment.newInstance(false), getString(R.string.has_not_sent));
    viewPagerAdapter.add(NewCustomerFragment.newInstance(true), getString(R.string.has_sent));
    viewPagerAdapter.add(SystemCustomerFragment.newInstance(), getString(R.string.system_customer));
    viewpager.setAdapter(viewPagerAdapter);
  }

  @Override
  public int getFragmentId() {
    return MainActivity.CUSTOMER_FRAGMENT;
  }
}
