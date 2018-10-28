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
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.parsroyal.solutiontablet.R;
import com.parsroyal.solutiontablet.biz.impl.SearchGoodServiceImpl;
import com.parsroyal.solutiontablet.constants.Constants;
import com.parsroyal.solutiontablet.constants.StatusCodes;
import com.parsroyal.solutiontablet.data.entity.Customer;
import com.parsroyal.solutiontablet.data.event.ErrorEvent;
import com.parsroyal.solutiontablet.data.event.Event;
import com.parsroyal.solutiontablet.data.event.SearchCustomerSuccessEvent;
import com.parsroyal.solutiontablet.data.listmodel.CustomerListModel;
import com.parsroyal.solutiontablet.service.CustomerService;
import com.parsroyal.solutiontablet.service.impl.CustomerServiceImpl;
import com.parsroyal.solutiontablet.ui.MainActivity;
import com.parsroyal.solutiontablet.ui.adapter.CustomerAdapter;
import com.parsroyal.solutiontablet.ui.adapter.SystemCustomerAdapter;
import com.parsroyal.solutiontablet.util.MultiScreenUtility;
import com.parsroyal.solutiontablet.util.RtlGridLayoutManager;
import com.parsroyal.solutiontablet.util.ToastUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

/**
 * @author Shakib
 */
public class CustomerSearchFragment extends BaseFragment {

  @BindView(R.id.search_img)
  ImageView searchImg;
  @BindView(R.id.search_edt)
  EditText searchEdt;
  @BindView(R.id.recycler_view)
  RecyclerView recyclerView;

  private CustomerService customerService;
  private MainActivity activity;
  private boolean isClose = false;
  private SystemCustomerAdapter adapter;
  private boolean isClickable;
  private SearchGoodServiceImpl searchGoodService;
  private Timer timer;
  private CustomerAdapter onlineAdapter;

  public CustomerSearchFragment() {
    // Required empty public constructor
  }

  public static CustomerSearchFragment newInstance() {
    return new CustomerSearchFragment();
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    View view = inflater.inflate(R.layout.fragment_customer_search, container, false);
    ButterKnife.bind(this, view);
    Bundle args = getArguments();
    isClickable = args.getBoolean(Constants.IS_CLICKABLE, false);
    activity = (MainActivity) getActivity();
    activity.changeTitle(getString(R.string.search));
    customerService = new CustomerServiceImpl(activity);
    searchGoodService = new SearchGoodServiceImpl(activity);
    if (isClickable) {
      setUpRecyclerViewOnline();
    } else {
      setUpRecyclerView();
    }
    onSearchTextChanged();
    return view;
  }

  //set up recycler view
  private void setUpRecyclerView() {
    adapter = new SystemCustomerAdapter(activity, getCustomersList(), isClickable);
    if (MultiScreenUtility.isTablet(activity)) {
      RtlGridLayoutManager rtlGridLayoutManager = new RtlGridLayoutManager(activity, 2);
      recyclerView.setLayoutManager(rtlGridLayoutManager);
    } else {
      LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
      recyclerView.setLayoutManager(linearLayoutManager);
    }
    recyclerView.setAdapter(adapter);
  }

  //set up recycler view online search
  private void setUpRecyclerViewOnline() {
    onlineAdapter = new CustomerAdapter(activity, new ArrayList<>(), isClickable);
    if (MultiScreenUtility.isTablet(activity)) {
      RtlGridLayoutManager rtlGridLayoutManager = new RtlGridLayoutManager(activity, 2);
      recyclerView.setLayoutManager(rtlGridLayoutManager);
    } else {
      LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
      recyclerView.setLayoutManager(linearLayoutManager);
    }
    recyclerView.setAdapter(onlineAdapter);
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
        if (isClickable) {
          if (timer != null) {
            timer.cancel();
          }
        }
      }

      @Override
      public void afterTextChanged(Editable s) {
        if (!isClickable) {
          List<CustomerListModel> customerList = adapter.getFilteredData(s.toString());
          adapter.update(customerList);
        } else {
          timer = new Timer();
          timer.schedule(new TimerTask() {
            @Override
            public void run() {
              if (!TextUtils.isEmpty(s.toString())) {
                searchGoodService.search(s.toString());
              } else {
                runOnUiThread(() -> onlineAdapter.update(new ArrayList<>()));
              }
            }
          }, 600); // 600ms delay before the timer executes the „run“ method from TimerTask

        }
      }
    });
  }

  private List<CustomerListModel> getCustomersList() {
    return customerService.getFilteredCustomerList(null, "");
  }

  @Override
  public int getFragmentId() {
    return MainActivity.CUSTOMER_SEARCH_FRAGMENT;
  }

  @Override
  public void onResume() {
    super.onResume();
    EventBus.getDefault().register(this);
  }

  @Override
  public void onPause() {
    super.onPause();
    EventBus.getDefault().unregister(this);
  }

  @Subscribe
  public void getMessage(Event event) {
    if (event instanceof ErrorEvent) {
      ToastUtil.toastError(getActivity(), R.string.error_connecting_server);
    } else if (event instanceof SearchCustomerSuccessEvent) {
      if (event.getStatusCode() == StatusCodes.SUCCESS) {
        List<Customer> customers = ((SearchCustomerSuccessEvent) event).getCustomers();
        onlineAdapter.update(customers);
      } else if (event.getStatusCode() == StatusCodes.NO_DATA_ERROR) {
        ToastUtil.toastError(getActivity(), R.string.retry);
      }
    }
  }

  @OnClick({R.id.search_img, R.id.back_img})
  public void onClick(View view) {
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
