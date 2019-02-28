package com.parsroyal.storemanagement.ui.fragment;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.parsroyal.storemanagement.R;
import com.parsroyal.storemanagement.SolutionTabletApplication;
import com.parsroyal.storemanagement.constants.Authority;
import com.parsroyal.storemanagement.constants.Constants;
import com.parsroyal.storemanagement.constants.PageStatus;
import com.parsroyal.storemanagement.data.listmodel.NCustomerListModel;
import com.parsroyal.storemanagement.data.searchobject.NCustomerSO;
import com.parsroyal.storemanagement.service.CustomerService;
import com.parsroyal.storemanagement.service.impl.CustomerServiceImpl;
import com.parsroyal.storemanagement.ui.activity.MainActivity;
import com.parsroyal.storemanagement.ui.adapter.NewCustomerAdapter;
import com.parsroyal.storemanagement.util.MultiScreenUtility;
import com.parsroyal.storemanagement.util.RtlGridLayoutManager;
import java.util.List;

public class NewCustomerFragment extends BaseFragment {

  @BindView(R.id.recycler_view)
  RecyclerView recyclerView;
  @BindView(R.id.fab_add_customer)
  FloatingActionButton fabAddCustomer;

  private boolean isSend;
  private NewCustomerAdapter adapter;
  private CustomerService customerService;
  private NCustomerSO nCustomerSO;
  private MainActivity activity;

  public NewCustomerFragment() {
    // Required empty public constructor
  }

  public static NewCustomerFragment newInstance(boolean isSend) {
    NewCustomerFragment fragment = new NewCustomerFragment();
    fragment.isSend = isSend;
    return fragment;
  }

  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    View view = inflater.inflate(R.layout.fragment_customer_send, container, false);
    ButterKnife.bind(this, view);
    activity = (MainActivity) getActivity();
    this.customerService = new CustomerServiceImpl(activity);
    this.nCustomerSO = new NCustomerSO();
    if (isSend) {
      nCustomerSO.setSent(1);
    } else {
      nCustomerSO.setSent(0);
    }
    setUpRecyclerView();
    checkPermissions();
    return view;
  }

  private void checkPermissions() {
    if (!SolutionTabletApplication.getInstance().hasAccess(Authority.ADD_NEW_CUSTOMER)) {
      fabAddCustomer.setEnabled(false);
      fabAddCustomer
          .setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.gray)));
    }
  }

  //set up recycler view
  private void setUpRecyclerView() {
    adapter = new NewCustomerAdapter(activity, isSend, getCustomersList());
    if (MultiScreenUtility.isTablet(activity)) {
      RtlGridLayoutManager rtlGridLayoutManager = new RtlGridLayoutManager(activity, 2);
      recyclerView.setLayoutManager(rtlGridLayoutManager);
    } else {
      LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
      recyclerView.setLayoutManager(linearLayoutManager);
    }
    recyclerView.setAdapter(adapter);
    recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
      @Override
      public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
        if (dy > 0) {
          fabAddCustomer.setVisibility(View.GONE);
        } else {
          fabAddCustomer.setVisibility(View.VISIBLE);
        }
      }
    });
  }

  private List<NCustomerListModel> getCustomersList() {
    return customerService.searchForNCustomers(nCustomerSO);
  }

  @OnClick(R.id.fab_add_customer)
  public void onClick() {
    Bundle args = new Bundle();
    args.putSerializable(Constants.PAGE_STATUS, PageStatus.EDIT);
    activity.changeFragment(MainActivity.NEW_CUSTOMER_DETAIL_FRAGMENT_ID, args, true);
  }

  @Override
  public int getFragmentId() {
    return MainActivity.NEW_CUSTOMER_FRAGMENT_ID;
  }
}
