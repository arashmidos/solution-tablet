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

public class ReportFragment extends BaseFragment {

  @BindView(R.id.tabs) TabLayout tabs;
  @BindView(R.id.viewpager) ViewPager viewpager;

  private CustomerDetailViewPagerAdapter viewPagerAdapter;
  private MainActivity mainActivity;

  public ReportFragment() {
    // Required empty public constructor
  }

  public static ReportFragment newInstance() {
    ReportFragment fragment = new ReportFragment();
    return fragment;
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    View view = inflater.inflate(R.layout.fragment_report, container, false);
    ButterKnife.bind(this, view);
    mainActivity = (MainActivity) getActivity();
    mainActivity.changeTitle(getString(R.string.reports));
    tabs.setupWithViewPager(viewpager);
    setUpViewPager();
    viewpager.setCurrentItem(1);
    return view;
  }

  private void setUpViewPager() {
    viewPagerAdapter = new CustomerDetailViewPagerAdapter(
        getActivity().getSupportFragmentManager());
    viewPagerAdapter
        .add(PaymentListFragment.newInstance(null), getString(R.string.payments));
    viewPagerAdapter.add(OrderListFragment.newInstance(getArguments(), null), getString(R.string.orders));
    viewpager.setAdapter(viewPagerAdapter);
  }


  @Override
  public int getFragmentId() {
    return MainActivity.REPORT_FRAGMENT;
  }
}
