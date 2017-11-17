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
import com.parsroyal.solutiontablet.constants.Constants;
import com.parsroyal.solutiontablet.ui.MainActivity;
import com.parsroyal.solutiontablet.ui.adapter.CustomerDetailViewPagerAdapter;

public class ReportFragment extends BaseFragment {

  @BindView(R.id.tabs)
  TabLayout tabs;
  @BindView(R.id.viewpager)
  ViewPager viewpager;

  private CustomerDetailViewPagerAdapter viewPagerAdapter;
  private MainActivity mainActivity;
  private PaymentListFragment paymentListFragment;
  private OrderListFragment orderListFragment;
  private ReturnListFragment returnListFragment;
  private AllQuestionnaireListFragment allQuestionnaireListFragment;

  public ReportFragment() {
    // Required empty public constructor
  }

  public static ReportFragment newInstance() {
    return new ReportFragment();
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
    initFragments();
    setUpViewPager();
    viewpager.setCurrentItem(2);
    return view;
  }

  private void setUpViewPager() {
    viewPagerAdapter = new CustomerDetailViewPagerAdapter(mainActivity.getSupportFragmentManager());
    viewPagerAdapter.add(allQuestionnaireListFragment, getString(R.string.questionnaire));
    viewPagerAdapter.add(paymentListFragment, getString(R.string.payments));
    viewPagerAdapter.add(returnListFragment, getString(R.string.returns));
    viewPagerAdapter.add(orderListFragment, getString(R.string.orders));
    viewpager.setAdapter(viewPagerAdapter);
  }

  private void initFragments() {
    paymentListFragment = PaymentListFragment.newInstance(null);
    orderListFragment = OrderListFragment.newInstance(null, null);
    returnListFragment = ReturnListFragment.newInstance(null, null);
    allQuestionnaireListFragment = AllQuestionnaireListFragment.newInstance(null);
  }

  @Override
  public int getFragmentId() {
    return MainActivity.REPORT_FRAGMENT;
  }
}
