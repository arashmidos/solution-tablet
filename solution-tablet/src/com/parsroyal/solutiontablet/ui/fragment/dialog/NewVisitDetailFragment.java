package com.parsroyal.solutiontablet.ui.fragment.dialog;

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
import com.parsroyal.solutiontablet.ui.fragment.BaseFragment;
import com.parsroyal.solutiontablet.ui.fragment.CustomerInfoFragment;
import com.parsroyal.solutiontablet.ui.fragment.NewOrderListFragment;
import com.parsroyal.solutiontablet.ui.fragment.PaymentListFragment;


public class NewVisitDetailFragment extends BaseFragment {

  @BindView(R.id.tabs)
  TabLayout tabs;
  @BindView(R.id.viewpager)
  ViewPager viewpager;

  private CustomerDetailViewPagerAdapter viewPagerAdapter;
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
    visitId = args.getLong(Constants.VISIT_ID);
    tabs.setupWithViewPager(viewpager);
    setUpViewPager();
    viewpager.setCurrentItem(viewPagerAdapter.getCount());
    return view;
  }

  @Override public void onResume() {
    super.onResume();
  }

  private void setUpViewPager() {
    viewPagerAdapter = new CustomerDetailViewPagerAdapter(
        getActivity().getSupportFragmentManager());
//    viewPagerAdapter.add(BlankFragment.newInstance(), getString(R.string.images));
//    viewPagerAdapter.add(BlankFragment.newInstance(), getString(R.string.questionnaire));
    viewPagerAdapter.add(PaymentListFragment.newInstance(getArguments()), getString(R.string.payments));
    viewPagerAdapter.add(NewOrderListFragment.newInstance(), getString(R.string.orders));
    viewPagerAdapter
        .add(CustomerInfoFragment.newInstance(getArguments()), getString(R.string.customer_information));
    viewpager.setAdapter(viewPagerAdapter);
  }

  @Override
  public int getFragmentId() {
    return MainActivity.VISIT_DETAIL_FRAGMENT_ID;
  }
}
