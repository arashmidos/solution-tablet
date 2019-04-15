package com.parsroyal.solutiontablet.navigation;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.parsroyal.solutiontablet.R;
import com.parsroyal.solutiontablet.biz.impl.RestServiceImpl;
import com.parsroyal.solutiontablet.constants.Constants;
import com.parsroyal.solutiontablet.data.listmodel.CustomerListModel;
import com.parsroyal.solutiontablet.data.model.CustomerLocationDto;
import com.parsroyal.solutiontablet.service.CustomerService;
import com.parsroyal.solutiontablet.service.impl.CustomerServiceImpl;
import com.parsroyal.solutiontablet.ui.adapter.CustomerDetailViewPagerAdapter;
import com.parsroyal.solutiontablet.util.Empty;
import com.parsroyal.solutiontablet.vrp.model.OptimizedRouteResponse;
import java.util.List;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class NavigateActivity extends AppCompatActivity {


  @BindView(R.id.tabs)
  TabLayout tabs;
  @BindView(R.id.viewpager)
  ViewPager viewpager;
  private CustomerDetailViewPagerAdapter viewPagerAdapter;
  private CustomerService customerService;
  private long visitLineBackendId;
  private List<CustomerLocationDto> customersLocation;
  private List<CustomerListModel> customerList;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_navigate);
    ButterKnife.bind(this);

    tabs.setupWithViewPager(viewpager);
    setUpViewPager();

    customerService = new CustomerServiceImpl(this);
    try {
      setData();
    } catch (IllegalArgumentException ex) {
      ex.printStackTrace();
      setContentView(R.layout.view_error_page);
      return;
    }

    boolean existLocal = false;
    if (existLocal) {

    } else {
      new RestServiceImpl().valOptimizedRoute(this, customersLocation);

    }
  }

  @Override
  protected void onPause() {
    super.onPause();
    EventBus.getDefault().register(this);
  }

  @Override
  protected void onResume() {
    super.onResume();
    EventBus.getDefault().unregister(this);
  }

  @Subscribe
  public void getMessage(OptimizedRouteResponse response) {

  }

  private void setData() {

    Intent intent = getIntent();
    if (Empty.isEmpty(intent) || intent.getLongExtra(Constants.VISITLINE_BACKEND_ID, -1L) == -1) {
      throw new IllegalArgumentException("VisitLine Id not found");
    }
    visitLineBackendId = intent.getLongExtra(Constants.VISITLINE_BACKEND_ID, -1);
    customersLocation = customerService.getAllCustomersLocation(visitLineBackendId);
    customerList = customerService.getFilteredCustomerList(visitLineBackendId, "", false);
  }

  private void setUpViewPager() {
    viewPagerAdapter = new CustomerDetailViewPagerAdapter(getSupportFragmentManager());
    viewPagerAdapter.add(NavigateMapFragment.newInstance(), getString(R.string.map));
    viewPagerAdapter.add(NavigateListFragment.newInstance(), getString(R.string.list));
    viewpager.setAdapter(viewPagerAdapter);
    viewpager.setCurrentItem(viewPagerAdapter.getCount());
  }
}
