package com.parsroyal.solutiontablet.ui.fragment;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.OnScrollListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.parsroyal.solutiontablet.R;
import com.parsroyal.solutiontablet.constants.Constants;
import com.parsroyal.solutiontablet.constants.StatusCodes;
import com.parsroyal.solutiontablet.data.entity.Customer;
import com.parsroyal.solutiontablet.data.event.ActionEvent;
import com.parsroyal.solutiontablet.service.CustomerService;
import com.parsroyal.solutiontablet.service.impl.CustomerServiceImpl;
import com.parsroyal.solutiontablet.ui.MainActivity;
import com.parsroyal.solutiontablet.ui.adapter.PictureAdapter;
import com.parsroyal.solutiontablet.util.Empty;
import com.parsroyal.solutiontablet.util.MultiScreenUtility;
import org.greenrobot.eventbus.EventBus;

public class PictureFragment extends BaseFragment {

  @BindView(R.id.recycler_view)
  RecyclerView recyclerView;
  @BindView(R.id.fab_add_pic)
  FloatingActionButton fabAddPic;

  private CustomerService customerService;
  private MainActivity mainActivity;
  private PictureAdapter adapter;
  private Customer customer;

  public PictureFragment() {
    // Required empty public constructor
  }

  public static PictureFragment newInstance(Bundle arguments) {
    PictureFragment fragment = new PictureFragment();
    fragment.setArguments(arguments);
    return fragment;
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    View view = inflater.inflate(R.layout.fragment_picture, container, false);
    ButterKnife.bind(this, view);
    mainActivity = (MainActivity) getActivity();
    customerService = new CustomerServiceImpl(mainActivity);
    Bundle arguments =getArguments();
    if (Empty.isNotEmpty(arguments)) {
      long customerId = arguments.getLong(Constants.CUSTOMER_ID);
      customer = customerService.getCustomerById(customerId);
      if (Empty.isEmpty(customer)) {
        return inflater.inflate(R.layout.empty_view, container, false);
      }
      setUpRecyclerView();
    } else {
      return inflater.inflate(R.layout.empty_view, container, false);
    }
    return view;
  }

  //set up recycler view

  private void setUpRecyclerView() {
    adapter = new PictureAdapter(mainActivity,
        customerService.getAllPicturesByCustomerBackendId(customer.getBackendId()));
    GridLayoutManager gridLayoutManager;
    if (MultiScreenUtility.isTablet(mainActivity)) {
      gridLayoutManager = new GridLayoutManager(mainActivity, 8);
    } else {
      gridLayoutManager = new GridLayoutManager(mainActivity, 3);
    }
    recyclerView.setLayoutManager(gridLayoutManager);
    recyclerView.setAdapter(adapter);
    recyclerView.addOnScrollListener(new OnScrollListener() {
      @Override
      public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
        if (dy > 0) {
          fabAddPic.setVisibility(View.GONE);
        } else {
          fabAddPic.setVisibility(View.VISIBLE);
        }
      }
    });
  }

  @Override
  public int getFragmentId() {
    return 0;
  }

  @OnClick(R.id.fab_add_pic)
  public void onViewClicked() {
    EventBus.getDefault().post(new ActionEvent(StatusCodes.ACTION_START_CAMERA));
  }

  public void update() {
    adapter.updateList(customerService.getAllPicturesByCustomerBackendId(customer.getBackendId()));
  }
}
