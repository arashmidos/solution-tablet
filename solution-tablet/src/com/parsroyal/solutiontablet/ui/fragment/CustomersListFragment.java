package com.parsroyal.solutiontablet.ui.fragment;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.parsroyal.solutiontablet.R;
import com.parsroyal.solutiontablet.data.listmodel.CustomerListModel;
import com.parsroyal.solutiontablet.service.CustomerService;
import com.parsroyal.solutiontablet.service.VisitService;
import com.parsroyal.solutiontablet.service.impl.CustomerServiceImpl;
import com.parsroyal.solutiontablet.service.impl.VisitServiceImpl;
import com.parsroyal.solutiontablet.ui.FeedActivity;
import com.parsroyal.solutiontablet.ui.adapter.CustomersAdapter;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class CustomersListFragment extends BaseFragment {

  @BindView(R.id.toolbar) Toolbar toolbar;
  @BindView(R.id.toolbar_title) TextView toolbarTitle;
  @BindView(R.id.search_img) ImageView searchImg;
  @BindView(R.id.path_code_tv) TextView pathCodeTv;
  @BindView(R.id.customers_number_tv) TextView customersNumberTv;
  @BindView(R.id.recycler_view) RecyclerView recyclerView;
  @BindView(R.id.search_edt) EditText searchEdt;

  private CustomersAdapter adapter;
  private FeedActivity feedActivity;
  private ActionBar actionBar;
  private long visitLineId;
  private CustomerService customerService;
  private VisitService visitService;
  private AlertDialog alertDialog;

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
    feedActivity = (FeedActivity) getActivity();
    customerService = new CustomerServiceImpl(feedActivity);
    visitService = new VisitServiceImpl(feedActivity);
    setData();
    setUpToolbar();
    onSearchTextChanged();
    setUpRecyclerView();
    return view;
  }

  @Override public int getFragmentId() {
    return 0;
  }

  //set up toolbar and handle toolbar back
  private void setUpToolbar() {
    feedActivity.setSupportActionBar(toolbar);
    actionBar = feedActivity.getSupportActionBar();
    actionBar.setDisplayHomeAsUpEnabled(true);
    toolbarTitle.setText("مسیر پاسداران-هروی");
    toolbar.setNavigationIcon(R.drawable.ic_arrow_forward);
    toolbar.setNavigationOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        feedActivity.onBackPressed();
      }
    });
  }

  private void setData() {
    String customerCount = String.valueOf(visitService.getAllVisitLinesListModel().get(0).getCustomerCount()) + " مشتری";
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
      @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {

      }

      @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (TextUtils.isEmpty(s.toString()))
          searchImg.setVisibility(View.VISIBLE);
        else
          searchImg.setVisibility(View.GONE);
      }

      @Override public void afterTextChanged(Editable s) {

      }
    });
  }

  @OnClick({R.id.path_code_tv, R.id.customers_number_lay, R.id.sort_lay, R.id.filter_lay})
  public void onClick(View view) {
    switch (view.getId()) {
      case R.id.sort_lay:
        showSortDialog();
        break;
      case R.id.filter_lay:
        showFilterDialog();
        break;
    }
  }

  private void showSortDialog() {
    AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(feedActivity);
    LayoutInflater inflater = feedActivity.getLayoutInflater();
    View dialogView = inflater.inflate(R.layout.dialog_sort, null);
    dialogBuilder.setView(dialogView);

    RadioGroup sortRadioGroup = (RadioGroup) dialogView.findViewById(R.id.sort_radio_group);
    AlertDialog alertDialog = dialogBuilder.create();
    alertDialog.show();
    sortRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
      @Override
      public void onCheckedChanged(RadioGroup group, int checkedId) {
        RadioButton selectedRadio = (RadioButton) dialogView.findViewById(checkedId);
        Toast.makeText(feedActivity, selectedRadio.getText(), Toast.LENGTH_SHORT).show();
        alertDialog.cancel();
      }
    });
  }


  private void showFilterDialog() {
    AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(feedActivity);
    LayoutInflater inflater = feedActivity.getLayoutInflater();
    View dialogView = inflater.inflate(R.layout.dialog_customer_filter, null);
    dialogBuilder.setView(dialogView);

    Button doFilterBtn = (Button) dialogView.findViewById(R.id.do_filter_btn);
    TextView closeTv = (TextView) dialogView.findViewById(R.id.close_tv);
    Button removeFilterBtn = (Button) dialogView.findViewById(R.id.remove_filter_btn);
    CheckBox registerNo = (CheckBox) dialogView.findViewById(R.id.register_no);
    CheckBox registerOrder = (CheckBox) dialogView.findViewById(R.id.register_order);
    EditText distanceEdt = (EditText) dialogView.findViewById(R.id.distance_edt);

    AlertDialog alertDialog = dialogBuilder.create();
    alertDialog.show();
    closeTv.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        alertDialog.cancel();
      }
    });
    removeFilterBtn.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        //TODO:add enter action
        Toast.makeText(feedActivity, "remove filter " + distanceEdt.getText().toString() + "no " + String.valueOf(registerNo.isChecked()) + "order " + String.valueOf(registerOrder.isChecked()), Toast.LENGTH_SHORT).show();
        alertDialog.dismiss();
      }
    });
    doFilterBtn.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        //TODO:add no visit action
        Toast.makeText(feedActivity, "do filter " + distanceEdt.getText().toString() + "no " + String.valueOf(registerNo.isChecked()) + "order " + String.valueOf(registerOrder.isChecked()), Toast.LENGTH_SHORT).show();
        alertDialog.cancel();
      }
    });
  }
}
