package com.parsroyal.solutiontablet.ui.fragment;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.parsroyal.solutiontablet.R;
import com.parsroyal.solutiontablet.constants.Constants;
import com.parsroyal.solutiontablet.data.entity.Customer;
import com.parsroyal.solutiontablet.service.impl.CustomerServiceImpl;
import com.parsroyal.solutiontablet.ui.MainActivity;
import com.parsroyal.solutiontablet.ui.adapter.CustomerDetailViewPagerAdapter;

public class NewVisitDetailFragment extends BaseFragment {

  @BindView(R.id.tabs)
  TabLayout tabs;
  @BindView(R.id.viewpager)
  ViewPager viewpager;

  private CustomerDetailViewPagerAdapter viewPagerAdapter;
  private CustomerServiceImpl customerService;
  private CustomerInfoFragment customerInfoFragment;
  private FragmentActivity mainActivity;
  private NewOrderListFragment orderListFragment;
  private PaymentListFragment paymentListFragment;

  public Customer getCustomer() {
    return customer;
  }

  private Customer customer;

  public long getCustomerId() {
    return customerId;
  }

  public long getVisitId() {
    return visitId;
  }

  private long customerId;
  private long visitId;

  public NewVisitDetailFragment() {
    // Required empty public constructor
  }

  public static NewVisitDetailFragment newInstance() {
    return new NewVisitDetailFragment();
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    View view = inflater.inflate(R.layout.fragment_new_visit_detail, container, false);
    ButterKnife.bind(this, view);
    Bundle args = getArguments();
    customerId = args.getLong(Constants.CUSTOMER_ID);
    mainActivity = getActivity();
    customerService = new CustomerServiceImpl(mainActivity);
    customer = customerService.getCustomerById(customerId);
    visitId = args.getLong(Constants.VISIT_ID);
    tabs.setupWithViewPager(viewpager);
    initFragments();
    setUpViewPager();
    viewpager.setCurrentItem(viewPagerAdapter.getCount());
    viewpager.setOffscreenPageLimit(viewPagerAdapter.getCount());

    return view;
  }

  private void initFragments() {
    Bundle arguments = getArguments();
    paymentListFragment = PaymentListFragment.newInstance(arguments);
    orderListFragment = NewOrderListFragment.newInstance(arguments,this);
    customerInfoFragment = CustomerInfoFragment.newInstance(arguments);
  }

  private void setUpViewPager() {
    viewPagerAdapter = new CustomerDetailViewPagerAdapter(mainActivity.getSupportFragmentManager());
//    viewPagerAdapter.add(BlankFragment.newInstance(), getString(R.string.images));
//    viewPagerAdapter.add(BlankFragment.newInstance(), getString(R.string.questionnaire));
    viewPagerAdapter.add(paymentListFragment, getString(R.string.payments));
    viewPagerAdapter.add(orderListFragment, getString(R.string.orders));
    viewPagerAdapter.add(customerInfoFragment, getString(R.string.customer_information));
    viewpager.setAdapter(viewPagerAdapter);
  }

  @Override
  public int getFragmentId() {
    return MainActivity.VISIT_DETAIL_FRAGMENT_ID;
  }

  public void exit() {
    customerInfoFragment.finishVisiting();
  }
}
