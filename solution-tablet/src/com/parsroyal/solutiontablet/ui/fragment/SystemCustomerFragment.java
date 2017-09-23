package com.parsroyal.solutiontablet.ui.fragment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Optional;
import com.parsroyal.solutiontablet.R;
import com.parsroyal.solutiontablet.data.listmodel.CustomerListModel;
import com.parsroyal.solutiontablet.service.CustomerService;
import com.parsroyal.solutiontablet.service.impl.CustomerServiceImpl;
import com.parsroyal.solutiontablet.ui.MainActivity;
import com.parsroyal.solutiontablet.ui.adapter.SystemCustomerAdapter;
import com.parsroyal.solutiontablet.util.Analytics;
import com.parsroyal.solutiontablet.util.Empty;
import com.parsroyal.solutiontablet.util.MultiScreenUtility;
import com.parsroyal.solutiontablet.util.RtlGridLayoutManager;
import java.text.Collator;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

public class SystemCustomerFragment extends BaseFragment {

  @BindView(R.id.recycler_view)
  RecyclerView recyclerView;
  @BindView(R.id.fab_add_customer)
  FloatingActionButton fabAddCustomer;

  private MainActivity mainActivity;
  private CustomerService customerService;
  private SystemCustomerAdapter adapter;
  private List<CustomerListModel> customerList;
  private boolean filterApplied = false;
  private String sortType = "0";
  private boolean filterByNone = false;
  private boolean filterByOrder = false;
  private String filterDistance = "";
  private int filterDistanceInMeter;

  public SystemCustomerFragment() {
    // Required empty public constructor
  }

  public static SystemCustomerFragment newInstance() {
    return new SystemCustomerFragment();
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    View view = inflater.inflate(R.layout.fragment_system_customer, container, false);
    ButterKnife.bind(this, view);
    mainActivity = (MainActivity) getActivity();
    customerService = new CustomerServiceImpl(mainActivity);
    setUpRecyclerView();
    return view;
  }

  //set up recycler view
  private void setUpRecyclerView() {
    adapter = new SystemCustomerAdapter(mainActivity, getCustomersList());
    if (MultiScreenUtility.isTablet(mainActivity)) {
      RtlGridLayoutManager rtlGridLayoutManager = new RtlGridLayoutManager(mainActivity, 2);
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

  private List<CustomerListModel> getCustomersList() {
    return customerService.getFilteredCustomerList(null, "");
  }

  @Optional
  @OnClick({R.id.sort_lay, R.id.filter_lay, R.id.fab_add_customer})
  public void onClick(View view) {
    switch (view.getId()) {
      case R.id.sort_lay:
        showSortDialog();
        break;
      case R.id.filter_lay:
        showFilterDialog();
        break;
      case R.id.fab_add_customer:
        mainActivity.changeFragment(MainActivity.NEW_CUSTOMER_DETAIL_FRAGMENT_ID, true);
        break;
    }
  }

  private void showFilterDialog() {
    AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(mainActivity);
    LayoutInflater inflater = mainActivity.getLayoutInflater();
    View dialogView = inflater.inflate(R.layout.dialog_customer_filter, null);
    dialogBuilder.setView(dialogView);

    Button doFilterBtn = (Button) dialogView.findViewById(R.id.do_filter_btn);
    TextView closeTv = (TextView) dialogView.findViewById(R.id.close_tv);
    Button removeFilterBtn = (Button) dialogView.findViewById(R.id.remove_filter_btn);
    CheckBox filterNoneCb = (CheckBox) dialogView.findViewById(R.id.filter_none_cb);
    filterNoneCb.setChecked(filterByNone);
    CheckBox filterOrderCb = (CheckBox) dialogView.findViewById(R.id.filter_order_cb);
    filterOrderCb.setChecked(filterByOrder);
    EditText distanceEdt = (EditText) dialogView.findViewById(R.id.distance_edt);
    TextView errorMessageTv = (TextView) dialogView.findViewById(R.id.error_msg);

    if (Empty.isNotEmpty(filterDistance)) {
      distanceEdt.setText(filterDistance);
    }

    AlertDialog alertDialog = dialogBuilder.create();

    alertDialog.show();
    closeTv.setOnClickListener(v -> alertDialog.cancel());
    removeFilterBtn.setOnClickListener(v -> {
      distanceEdt.setText("");
      filterNoneCb.setChecked(false);
      filterOrderCb.setChecked(false);
      filterByNone = false;
      filterByOrder = false;
      filterDistance = "";
      filterApplied = false;
      new RefreshAsyncTask().execute("");
      alertDialog.dismiss();
    });
    doFilterBtn.setOnClickListener(v -> {
      filterDistance = distanceEdt.getText().toString();

      filterByOrder = filterOrderCb.isChecked();
      filterByNone = filterNoneCb.isChecked();

      if (Empty.isNotEmpty(filterDistance)) {
        try {
          filterDistanceInMeter = Integer.parseInt(filterDistance);

          if (filterDistanceInMeter < 0
              || filterDistanceInMeter > 500) {
            errorMessageTv.setText(R.string.error_filter_max_distance);
            errorMessageTv.setVisibility(View.VISIBLE);
            filterDistance = "";
            distanceEdt.setText("");
            return;
          }
        } catch (NumberFormatException e) {
          errorMessageTv.setText(R.string.error_filter_is_not_correct);
          errorMessageTv.setVisibility(View.VISIBLE);
          filterDistance = "";
          distanceEdt.setText("");
          return;
        }
      } else {
        filterDistanceInMeter = 0;
      }

      if (filterDistanceInMeter == 0 && !filterByOrder
          && !filterByNone) {
        errorMessageTv.setText(R.string.error_no_filter_selected);
        errorMessageTv.setVisibility(View.VISIBLE);
        return;
      }

      filterApplied = true;
      new RefreshAsyncTask().execute("");
      alertDialog.cancel();
    });
  }

  private void showSortDialog() {
    AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(mainActivity);
    LayoutInflater inflater = mainActivity.getLayoutInflater();
    View dialogView = inflater.inflate(R.layout.dialog_sort, null);
    dialogBuilder.setView(dialogView);

    RadioGroup sortRadioGroup = (RadioGroup) dialogView.findViewById(R.id.sort_radio_group);
    AlertDialog alertDialog = dialogBuilder.create();

    RadioButton selectedSortType = (RadioButton) sortRadioGroup.findViewWithTag(sortType);
    selectedSortType.setChecked(true);
    alertDialog.show();
    sortRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
      RadioButton selectedRadio = (RadioButton) dialogView.findViewById(checkedId);
      sortType = (String) selectedRadio.getTag();

      new RefreshAsyncTask().execute("");
      alertDialog.cancel();
    });
  }

  public void doFilter() {
    if (!filterApplied) {
      return;
    }
    for (Iterator<CustomerListModel> it = customerList.iterator(); it.hasNext(); ) {
      CustomerListModel listModel = it.next();
      if (listModel.hasOrder() != filterByOrder || listModel.hasRejection() != filterByNone) {
        it.remove();
      } else if (filterDistanceInMeter != 0)//If meter filter set
      {
        //If has not location or it's greater than user filter
        if (listModel.getDistance() == -1 || listModel.getDistance() > filterDistanceInMeter) {
          it.remove();
        }
      }
    }

    Analytics.logCustom("Filter", new String[]{"Distance", "Has Order", "Has none"},
        String.valueOf(filterDistanceInMeter), String.valueOf(filterByOrder),
        String.valueOf(filterByNone));
  }

  private void sort() {

    switch (sortType) {
      case "0":
        Collections.sort(customerList,
            (item1, item2) -> item1.getCodeNumber().compareTo(item2.getCodeNumber()));
        break;
      case "1":
        Collections.sort(customerList,
            (item1, item2) -> item2.getCodeNumber().compareTo(item1.getCodeNumber()));
        break;
      case "2":
        Collections.sort(customerList, (item1, item2) -> Collator.getInstance(new Locale("fa"))
            .compare(item1.getTitle(), item2.getTitle()));
        break;
      case "3":
        Collections.sort(customerList, (item1, item2) -> Collator.getInstance(new Locale("fa"))
            .compare(item2.getTitle(), item1.getTitle()));
        break;
    }
  }

  @Override
  public int getFragmentId() {
    return MainActivity.SYSTEM_CUSTOMER_FRAGMENT;
  }

  private class RefreshAsyncTask extends AsyncTask<String, Void, List<CustomerListModel>> {

    @Override
    protected void onPreExecute() {
      super.onPreExecute();//TODO: Shakib,Show progressbar
    }


    @Override
    protected List<CustomerListModel> doInBackground(String... params) {
      customerList = adapter.getFilteredData(params[0]);
      doFilter();
      sort();
      return customerList;
    }

    @Override
    protected void onPostExecute(List<CustomerListModel> customerListModels) {
      //// TODO: Shakib, hide progressbar
      customerList = customerListModels;
      adapter.update(customerList);
      adapter.notifyDataSetChanged();
    }
  }

}
