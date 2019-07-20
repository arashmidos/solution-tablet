package com.parsroyal.solutiontablet.ui.fragment;

import android.os.Bundle;
import androidx.annotation.NonNull;
import com.google.android.material.tabs.TabLayout;
import androidx.viewpager.widget.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.parsroyal.solutiontablet.R;
import com.parsroyal.solutiontablet.ui.activity.MainActivity;
import com.parsroyal.solutiontablet.ui.adapter.CustomerDetailViewPagerAdapter;
import com.parsroyal.solutiontablet.util.PreferenceHelper;

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
  private ReturnListFragment requestReturnListFragment;
  private AllQuestionnaireListFragment allQuestionnaireListFragment;
  private DeliveryListFragment deliveryListFragment;
  private VisitListFragment visitListFragment;
  private FreeOrderListFragment freeOrderListFragment;

  public ReportFragment() {
    // Required empty public constructor
  }

  public static ReportFragment newInstance() {
    return new ReportFragment();
  }

  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    View view = inflater.inflate(R.layout.fragment_report, container, false);
    ButterKnife.bind(this, view);
    mainActivity = (MainActivity) getActivity();
    mainActivity.changeTitle(getString(R.string.reports));
    tabs.setupWithViewPager(viewpager);

    initFragments();
    setUpViewPager();
    viewpager.setCurrentItem(viewPagerAdapter.getCount());
    return view;
  }

  private void setUpViewPager() {
    viewPagerAdapter = new CustomerDetailViewPagerAdapter(mainActivity.getSupportFragmentManager());
    viewPagerAdapter.add(visitListFragment, getString(R.string.visits));
    viewPagerAdapter.add(allQuestionnaireListFragment, getString(R.string.questionnaire));
    viewPagerAdapter.add(paymentListFragment, getString(R.string.payments));
    viewPagerAdapter.add(returnListFragment, getString(R.string.returns));
    viewPagerAdapter.add(requestReturnListFragment, getString(R.string.request_returns));
    if (PreferenceHelper.isDistributor()) {
      viewPagerAdapter.add(deliveryListFragment, getString(R.string.delivered_orders));
    } else {
      viewPagerAdapter.add(freeOrderListFragment, getString(R.string.free_orders));
      viewPagerAdapter.add(orderListFragment, getString(R.string.orders));
    }
    viewpager.setAdapter(viewPagerAdapter);
  }

  private void initFragments() {
    paymentListFragment = PaymentListFragment.newInstance(null, null);
    if (PreferenceHelper.isDistributor()) {
      deliveryListFragment = DeliveryListFragment.newInstance(null, null);
    } else {
      orderListFragment = OrderListFragment.newInstance(null, null);
      freeOrderListFragment = FreeOrderListFragment.newInstance(null, null);
    }
    returnListFragment = ReturnListFragment.Companion.newInstance(null, null, false);
    requestReturnListFragment = ReturnListFragment.Companion.newInstance(null, null, true);
    allQuestionnaireListFragment = AllQuestionnaireListFragment.newInstance(null);
    visitListFragment = VisitListFragment.newInstance(null);
  }

  @Override
  public int getFragmentId() {
    return MainActivity.REPORT_FRAGMENT;
  }
}
