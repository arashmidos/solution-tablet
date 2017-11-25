package com.parsroyal.solutiontablet.ui.fragment;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
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
import java.util.List;

/**
 * @author Shakib
 */
public class PaymentListFragment extends BaseFragment {

  @BindView(R.id.recycler_view)
  RecyclerView recyclerView;
  @BindView(R.id.fab_add_Payment)
  FloatingActionButton fabAddPayment;

  private PaymentService paymentService;
  private long customerId;
  private long visitId;
  private CustomerService customerService;
  private Customer customer;
  private PaymentSO paymentSO;
  private PaymentAdapter adapter;
  private MainActivity mainActivity;

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

    View view = inflater.inflate(R.layout.fragment_payment_list, container, false);
    ButterKnife.bind(this, view);
    mainActivity = (MainActivity) getActivity();
    paymentService = new PaymentServiceImpl(mainActivity);
    customerService = new CustomerServiceImpl(mainActivity);
    setData();
    setUpRecyclerView();
    return view;
  }

  private void setData() {
    Bundle arguments = getArguments();
    if (Empty.isNotEmpty(arguments)) {
      //It comes from VisitDetail
      customerId = arguments.getLong(Constants.CUSTOMER_ID);
      visitId = arguments.getLong(Constants.VISIT_ID);
      customer = customerService.getCustomerById(customerId);
      paymentSO = new PaymentSO(customer.getBackendId(), SendStatus.NEW.getId());
    } else {
      fabAddPayment.setVisibility(View.GONE);
      paymentSO = new PaymentSO(SendStatus.NEW.getId());
    }
  }

  //set up recycler view
  private void setUpRecyclerView() {
    adapter = new PaymentAdapter(mainActivity, getPaymentList(), visitId,
        paymentSO.getCustomerBackendId() == -1);
    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
    recyclerView.setLayoutManager(linearLayoutManager);
    recyclerView.setAdapter(adapter);
    if (!Empty.isEmpty(getArguments())) {
      recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
          super.onScrolled(recyclerView, dx, dy);
          if (dy > 0) {
            fabAddPayment.setVisibility(View.GONE);
          } else {
            fabAddPayment.setVisibility(View.VISIBLE);
          }
        }
      });
    }
  }

  private List<PaymentListModel> getPaymentList() {
    return paymentService.searchForPayments(paymentSO);
  }

  @Override
  public int getFragmentId() {
    return 0;
  }

  @OnClick(R.id.fab_add_Payment)
  public void onClick() {
    Bundle args = new Bundle();
    args.putLong(Constants.CUSTOMER_BACKEND_ID, customer.getBackendId());
    args.putLong(Constants.VISIT_ID, visitId);
    mainActivity.changeFragment(MainActivity.REGISTER_PAYMENT_FRAGMENT, args, true);
  }
}
