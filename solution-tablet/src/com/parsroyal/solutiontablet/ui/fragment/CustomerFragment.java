package com.parsroyal.solutiontablet.ui.fragment;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.parsroyal.solutiontablet.R;
import com.parsroyal.solutiontablet.ui.MainActivity;
import com.parsroyal.solutiontablet.ui.adapter.CustomerDetailViewPagerAdapter;

public class CustomerFragment extends BaseFragment {

  @BindView(R.id.tabs)
  TabLayout tabs;
  @BindView(R.id.viewpager)
  ViewPager viewpager;

  private CustomerDetailViewPagerAdapter viewPagerAdapter;
  private MainActivity activity;

  public CustomerFragment() {
    // Required empty public constructor
  }

  public static CustomerFragment newInstance() {
    return new CustomerFragment();
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    View view = inflater.inflate(R.layout.fragment_customer, container, false);
    ButterKnife.bind(this, view);
    activity = (MainActivity) getActivity();
    activity.changeTitle(getString(R.string.cusomters));
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
