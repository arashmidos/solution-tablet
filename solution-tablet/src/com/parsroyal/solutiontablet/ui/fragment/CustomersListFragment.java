package com.parsroyal.solutiontablet.ui.fragment;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.parsroyal.solutiontablet.R;
import com.parsroyal.solutiontablet.data.listmodel.CustomerListModel;
import com.parsroyal.solutiontablet.service.CustomerService;
import com.parsroyal.solutiontablet.service.VisitService;
import com.parsroyal.solutiontablet.service.impl.CustomerServiceImpl;
import com.parsroyal.solutiontablet.service.impl.VisitServiceImpl;
import com.parsroyal.solutiontablet.ui.MainActivity;
import com.parsroyal.solutiontablet.ui.adapter.CustomersAdapter;
import java.util.List;


public class CustomersListFragment extends BaseFragment {

  @BindView(R.id.toolbar)
  Toolbar toolbar;
  @BindView(R.id.toolbar_title)
  TextView toolbarTitle;
  @BindView(R.id.search_img)
  ImageView searchImg;
  @BindView(R.id.path_code_tv)
  TextView pathCodeTv;
  @BindView(R.id.customers_number_tv)
  TextView customersNumberTv;
  @BindView(R.id.recycler_view)
  RecyclerView recyclerView;
  @BindView(R.id.search_edt)
  EditText searchEdt;

  private CustomersAdapter adapter;
  private MainActivity mainActivity;
  private ActionBar actionBar;
  private long visitLineId;
  private CustomerService customerService;
  private VisitService visitService;

  public static CustomersListFragment newInstance() {
    CustomersListFragment fragment = new CustomersListFragment();
    return fragment;
  }


  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    View view = inflater.inflate(R.layout.fragment_customers_list, container, false);
    ButterKnife.bind(this, view);
    mainActivity = (MainActivity) getActivity();
    customerService = new CustomerServiceImpl(mainActivity);
    visitService = new VisitServiceImpl(mainActivity);
    setData();
    setUpToolbar();
    onSearchTextChanged();
    setUpRecyclerView();
    return view;
  }

  @Override
  public int getFragmentId() {
    return 0;
  }

  //set up toolbar and handle toolbar back
  private void setUpToolbar() {
    mainActivity.setSupportActionBar(toolbar);
    actionBar = mainActivity.getSupportActionBar();
    actionBar.setDisplayHomeAsUpEnabled(true);
    toolbarTitle.setText("مسیر پاسداران-هروی");
    toolbar.setNavigationIcon(R.drawable.ic_arrow_forward);
    toolbar.setNavigationOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        mainActivity.onBackPressed();
      }
    });
  }

  private void setData() {
    String customerCount =
        String.valueOf(visitService.getAllVisitLinesListModel().get(0).getCustomerCount())
            + " مشتری";
    String pathCode = "کد مسیر : " + visitService.getAllVisitLinesListModel().get(0).getCode();
    customersNumberTv.setText(customerCount);
    pathCodeTv.setText(pathCode);
  }

  //set up recycler view
  private void setUpRecyclerView() {
    adapter = new CustomersAdapter(getActivity(), getCustomersList());
    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
    recyclerView.setLayoutManager(linearLayoutManager);
    recyclerView.setAdapter(adapter);
  }

  private List<CustomerListModel> getCustomersList() {
//    visitLineId = getArguments().getLong(Constants.VISITLINE_BACKEND_ID);
    visitLineId = visitService.getAllVisitLinesListModel().get(0).getPrimaryKey();
    return customerService.getAllCustomersListModelByVisitLineBackendId(visitLineId);
  }

  private void onSearchTextChanged() {
    searchEdt.addTextChangedListener(new TextWatcher() {
      @Override
      public void beforeTextChanged(CharSequence s, int start, int count, int after) {

      }

      @Override
      public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (TextUtils.isEmpty(s.toString())) {
          searchImg.setVisibility(View.VISIBLE);
        } else {
          searchImg.setVisibility(View.GONE);
        }
      }

      @Override
      public void afterTextChanged(Editable s) {

      }
    });
  }

  @OnClick({R.id.path_code_tv, R.id.customers_number_lay, R.id.sort_lay, R.id.filter_lay})
  public void onClick(View view) {
    switch (view.getId()) {
      case R.id.sort_lay:
        Toast.makeText(mainActivity, "sort", Toast.LENGTH_SHORT).show();
        break;
      case R.id.filter_lay:
        Toast.makeText(mainActivity, "filter", Toast.LENGTH_SHORT).show();
        break;
    }
  }
}
