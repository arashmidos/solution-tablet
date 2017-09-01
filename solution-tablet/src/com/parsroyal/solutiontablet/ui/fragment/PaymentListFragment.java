package com.parsroyal.solutiontablet.ui.fragment;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.parsroyal.solutiontablet.R;
import com.parsroyal.solutiontablet.constants.Constants;
import com.parsroyal.solutiontablet.constants.SendStatus;
import com.parsroyal.solutiontablet.data.entity.Customer;
import com.parsroyal.solutiontablet.data.listmodel.PaymentListModel;
import com.parsroyal.solutiontablet.data.searchobject.PaymentSO;
import com.parsroyal.solutiontablet.service.CustomerService;
import com.parsroyal.solutiontablet.service.PaymentService;
import com.parsroyal.solutiontablet.service.impl.CustomerServiceImpl;
import com.parsroyal.solutiontablet.service.impl.PaymentServiceImpl;
import com.parsroyal.solutiontablet.ui.MainActivity;
import com.parsroyal.solutiontablet.ui.adapter.PaymentAdapter;
import com.parsroyal.solutiontablet.util.Empty;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PaymentListFragment extends BaseFragment {

  @BindView(R.id.recycler_view) RecyclerView recyclerView;
  @BindView(R.id.fab_add_Payment) FloatingActionButton fabAddPayment;

  private PaymentService paymentService;
  private long customerId;
  private long visitId;
  private CustomerService customerService;
  private Customer customer;
  private PaymentSO paymentSO;
  private PaymentAdapter adapter;
  private MainActivity activity;

  public PaymentListFragment() {
    // Required empty public constructor
  }

  public static PaymentListFragment newInstance(Bundle arguments) {
    PaymentListFragment fragment = new PaymentListFragment();
    fragment.setArguments(arguments);
    return fragment;
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    View view = inflater.inflate(R.layout.fragment_payment_list, container, false);
    ButterKnife.bind(this, view);
    activity = (MainActivity) getActivity();
    this.paymentService = new PaymentServiceImpl(activity);
    this.customerService = new CustomerServiceImpl(activity);
    Bundle arguments = getArguments();
    if (Empty.isNotEmpty(arguments)) {
      customerId = arguments.getLong(Constants.CUSTOMER_ID);
      visitId = arguments.getLong(Constants.VISIT_ID);
      customer = customerService.getCustomerById(customerId);
      paymentSO = new PaymentSO(customer.getBackendId(), SendStatus.NEW.getId());
    } else {
      paymentSO = new PaymentSO(SendStatus.NEW.getId());
    }
    setUpRecyclerView();
    return view;
  }

  //set up recycler view
  private void setUpRecyclerView() {
    adapter = new PaymentAdapter(activity, getPaymentList());
    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
    recyclerView.setLayoutManager(linearLayoutManager);
    recyclerView.setAdapter(adapter);
    recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
      @Override public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
        if (dy > 0) {
          fabAddPayment.setVisibility(View.GONE);
        } else {
          fabAddPayment.setVisibility(View.VISIBLE);
        }
      }
    });
  }

  private List<PaymentListModel> getPaymentList() {
    List<PaymentListModel> list = new ArrayList<>();
    list.add(new PaymentListModel("جمعه 2 مرداد 96", "158.500 تومان", "چک", 0l, null));
    list.add(new PaymentListModel("پنجشنبه 23 مرداد 96", "156465650 تومان", "نقد", 0l, null));
    list.add(new PaymentListModel("چهارشنبه 12 تیر 96", "345345.500 تومان", "پرداخت الکترونیکی", 0l, null));
    list.add(new PaymentListModel("سه شنبه 6 اسفند 96", "158.500 تومان", "نقد", 0l, null));
    list.add(new PaymentListModel("دوشنبه 30 شهریور 96", "19825.300 تومان", "چک", 0l, null));
    list.add(new PaymentListModel("شنبه 29 مرداد 96", "56325512 تومان", "چک", 0l, null));
    list.add(new PaymentListModel("یکشنبه 2 آبان 96", "158.500 تومان", "پرداخت الکترونیکی", 0l, null));
    return list;
//    return paymentService.searchForPayments(paymentSO);
  }

  @Override public int getFragmentId() {
    return 0;
  }

  @OnClick(R.id.fab_add_Payment) public void onClick() {
    ((MainActivity) getActivity()).changeFragment(MainActivity.REGISTER_PAYMENT_FRAGMENT, true);
  }
}
