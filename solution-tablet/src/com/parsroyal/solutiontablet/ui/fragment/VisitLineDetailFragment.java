package com.parsroyal.solutiontablet.ui.fragment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Optional;
import com.parsroyal.solutiontablet.R;
import com.parsroyal.solutiontablet.constants.Constants;
import com.parsroyal.solutiontablet.data.listmodel.CustomerListModel;
import com.parsroyal.solutiontablet.data.listmodel.VisitLineListModel;
import com.parsroyal.solutiontablet.service.CustomerService;
import com.parsroyal.solutiontablet.service.VisitService;
import com.parsroyal.solutiontablet.service.impl.CustomerServiceImpl;
import com.parsroyal.solutiontablet.service.impl.VisitServiceImpl;
import com.parsroyal.solutiontablet.ui.activity.MainActivity;
import com.parsroyal.solutiontablet.ui.adapter.PathDetailAdapter;
import com.parsroyal.solutiontablet.util.Analytics;
import com.parsroyal.solutiontablet.util.Empty;
import com.parsroyal.solutiontablet.util.Logger;
import com.parsroyal.solutiontablet.util.MultiScreenUtility;
import com.parsroyal.solutiontablet.util.NumberUtil;
import com.parsroyal.solutiontablet.util.RtlGridLayoutManager;
import java.text.Collator;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

/**
 * @author Shakib
 */
public class VisitLineDetailFragment extends BaseFragment {

  @Nullable
  @BindView(R.id.search_img)
  ImageView searchImg;
  @Nullable
  @BindView(R.id.path_code_tv)
  TextView pathCodeTv;
  @Nullable
  @BindView(R.id.customers_number_tv)
  TextView customersNumberTv;
  @BindView(R.id.recycler_view)
  RecyclerView recyclerView;
  @Nullable
  @BindView(R.id.search_edt)
  EditText searchEdt;
  @Nullable
  @BindView(R.id.customer_count_btn)
  Button customerCountBtn;
  @Nullable
  @BindView(R.id.search_bar_lay)
  LinearLayout searchBarLay;
  @Nullable
  @BindView(R.id.no_customer_lay)
  LinearLayout noCustomerLay;

  private boolean isSearchBarVisible = false;
  private PathDetailAdapter adapter;
  private MainActivity mainActivity;
  private CustomerService customerService;
  private VisitService visitService;
  private long visitlineBackendId;
  private List<CustomerListModel> customerList;
  private VisitLineListModel visitline;
  private String sortType = "0";
  private boolean filterByNone = false;
  private boolean filterByOrder = false;
  private boolean filterByVisit = false;
  private String filterDistance = "";
  private int filterDistanceInMeter;
  private boolean filterApplied = false;
  private boolean isClose = false;

  public static VisitLineDetailFragment newInstance() {
    return new VisitLineDetailFragment();
  }

  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    View view = inflater.inflate(R.layout.fragment_path_detail, container, false);
    ButterKnife.bind(this, view);
    Bundle args = getArguments();
    visitlineBackendId = args.getLong(Constants.VISITLINE_BACKEND_ID, -1);

    mainActivity = (MainActivity) getActivity();
    customerService = new CustomerServiceImpl(mainActivity);
    visitService = new VisitServiceImpl(mainActivity);

    if (MultiScreenUtility.isTablet(mainActivity)) {
      setTabletData();
    } else {
      setMobileData();
    }
    onSearchTextChanged();
    setUpRecyclerView();
    return view;
  }

  @Override
  public int getFragmentId() {
    return MainActivity.VISITLINE_DETAIL_FRAGMENT_ID;
  }

  private void setMobileData() {
    visitline = visitService.getVisitLineListModelByBackendId(visitlineBackendId);

    if (Empty.isNotEmpty(customersNumberTv)) {

      Integer customerCount = visitline.getCustomerCount();
      if (customerCount == 0) {
        customersNumberTv.setText(R.string.no_customer_exist);
      } else {
        customersNumberTv.setText(NumberUtil.digitsToPersian(
            String.format(getString(R.string.x_customers), customerCount)));
      }
      pathCodeTv.setText(NumberUtil.digitsToPersian(
          String.format(getString(R.string.visitline_code_x), visitline.getCode())));
    } else {
      //We detected wrong device size!
      Logger.sendError("Wrong Orientation", "Device is not tablet");
    }
    mainActivity.changeTitle(NumberUtil.digitsToPersian(
        String.format(getString(R.string.visitline_code_x), visitline.getCode())));
  }

  private void setTabletData() {
    visitline = visitService.getVisitLineListModelByBackendId(visitlineBackendId);
    mainActivity.changeDetailContent(String.format(getString(R.string.visitline_code_x),
        NumberUtil.digitsToPersian(visitline.getCode())));

    if (Empty.isNotEmpty(customerCountBtn)) {
      Integer customerCount = visitline.getCustomerCount();
//      if (customerCount == 0) {
//        customerCountBtn.setText(R.string.no_customer_exist);
//      } else {
      customerCountBtn.setText(NumberUtil.digitsToPersian(customerCount));
//      }
    } else {
      //We detected wrong device size!
      Logger.sendError("Wrong Orientation", "Device is not tablet");
    }
    mainActivity.changeTitle(String.format(getString(R.string.visitline_code_x),
        NumberUtil.digitsToPersian(visitline.getCode())));
  }

  //set up recycler view
  private void setUpRecyclerView() {
    adapter = new PathDetailAdapter(mainActivity, getCustomersList(), visitlineBackendId, false);
    if (MultiScreenUtility.isTablet(mainActivity)) {
      RtlGridLayoutManager gridLayoutManager = new RtlGridLayoutManager(mainActivity, 2);
      recyclerView.setLayoutManager(gridLayoutManager);

    } else {
      LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mainActivity);
      recyclerView.setLayoutManager(linearLayoutManager);
    }
    recyclerView.setAdapter(adapter);
  }

  private List<CustomerListModel> getCustomersList() {
    customerList = customerService.getFilteredCustomerList(visitlineBackendId, "", false);
    return customerList;
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
      }

      @Override
      public void afterTextChanged(Editable s) {
        new RefreshAsyncTask().execute(s.toString());
      }
    });
  }

  @Optional
  @OnClick({R.id.sort_lay, R.id.filter_lay, R.id.search_lay, R.id.search_img})
  public void onClick(View view) {
    switch (view.getId()) {
      case R.id.sort_lay:
        showSortDialog();
        break;
      case R.id.filter_lay:
        showFilterDialog();
        break;
      case R.id.search_lay:
        setSearchBarVisibility();
        break;
      case R.id.search_img:
        if (isClose) {
          searchEdt.setText("");
        }
        break;
    }
  }

  private void setSearchBarVisibility() {
    isSearchBarVisible = !isSearchBarVisible;
    if (isSearchBarVisible) {
      searchBarLay.setVisibility(View.VISIBLE);
    } else {
      searchBarLay.setVisibility(View.GONE);
    }
  }

  private void showSortDialog() {
    AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(mainActivity);
    LayoutInflater inflater = mainActivity.getLayoutInflater();
    View dialogView = inflater.inflate(R.layout.dialog_sort, null);
    dialogBuilder.setView(dialogView);

    RadioGroup sortRadioGroup = dialogView.findViewById(R.id.sort_radio_group);
    AlertDialog alertDialog = dialogBuilder.create();

    RadioButton selectedSortType = sortRadioGroup.findViewWithTag(sortType);
    selectedSortType.setChecked(true);
    alertDialog.show();
    sortRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
      RadioButton selectedRadio = dialogView.findViewById(checkedId);
      sortType = (String) selectedRadio.getTag();

      new RefreshAsyncTask().execute(searchEdt.getText().toString());
      alertDialog.cancel();
    });
  }

  private void sort() {

    switch (sortType) {
      case "0":
        Collections.sort(customerList,
            (item1, item2) -> item1.getPrimaryKey().compareTo(item2.getPrimaryKey()));
        break;
      case "1":
        Collections.sort(customerList,
            (item1, item2) -> item1.getCodeNumber().compareTo(item2.getCodeNumber()));
        break;
      case "2":
        Collections.sort(customerList,
            (item1, item2) -> item2.getCodeNumber().compareTo(item1.getCodeNumber()));
        break;
      case "3":
        Collections.sort(customerList, (item1, item2) -> Collator.getInstance(new Locale("fa"))
            .compare(item1.getTitle(), item2.getTitle()));
        break;
      case "4":
        Collections.sort(customerList, (item1, item2) -> Collator.getInstance(new Locale("fa"))
            .compare(item2.getTitle(), item1.getTitle()));
        break;
    }
  }

  private void showFilterDialog() {
    AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(mainActivity);
    LayoutInflater inflater = mainActivity.getLayoutInflater();
    View dialogView = inflater.inflate(R.layout.dialog_customer_filter, null);
    dialogBuilder.setView(dialogView);

    Button doFilterBtn = dialogView.findViewById(R.id.do_filter_btn);
    TextView closeTv = dialogView.findViewById(R.id.close_tv);
    Button removeFilterBtn = dialogView.findViewById(R.id.remove_filter_btn);
    CheckBox filterNoneCb = dialogView.findViewById(R.id.filter_none_cb);
    filterNoneCb.setChecked(filterByNone);
    CheckBox filterOrderCb = dialogView.findViewById(R.id.filter_order_cb);
    filterOrderCb.setChecked(filterByOrder);
    CheckBox filterVisitedCb = dialogView.findViewById(R.id.filter_not_visited_cb);
    filterVisitedCb.setChecked(filterByVisit);
    EditText distanceEdt = dialogView.findViewById(R.id.distance_edt);
    TextView errorMessageTv = dialogView.findViewById(R.id.error_msg);

    if (Empty.isNotEmpty(filterDistance)) {
      distanceEdt.setText(filterDistance);
    }

    AlertDialog alertDialog = dialogBuilder.create();

    alertDialog.show();
    closeTv.setOnClickListener(v -> alertDialog.cancel());
    filterNoneCb.setOnClickListener(v -> {
      filterOrderCb.setChecked(false);
      filterVisitedCb.setChecked(false);
      filterByVisit = false;
      filterByOrder = false;
    });
    filterOrderCb.setOnClickListener(v -> {
      filterNoneCb.setChecked(false);
      filterVisitedCb.setChecked(false);
      filterByVisit = false;
      filterByNone = false;
    });
    filterVisitedCb.setOnClickListener(v -> {
      filterNoneCb.setChecked(false);
      filterOrderCb.setChecked(false);
      filterByOrder = false;
      filterByNone = false;
    });
    removeFilterBtn.setOnClickListener(v -> {
      distanceEdt.setText("");
      filterNoneCb.setChecked(false);
      filterOrderCb.setChecked(false);
      filterVisitedCb.setChecked(false);
      filterByNone = false;
      filterByOrder = false;
      filterByVisit = false;
      filterDistance = "";
      filterApplied = false;
      new RefreshAsyncTask().execute(searchEdt.getText().toString());
      alertDialog.dismiss();
    });
    doFilterBtn.setOnClickListener(v -> {
      filterDistance = distanceEdt.getText().toString();

      filterByOrder = filterOrderCb.isChecked();
      filterByNone = filterNoneCb.isChecked();
      filterByVisit = filterVisitedCb.isChecked();

      if (Empty.isNotEmpty(filterDistance)) {
        try {
          filterDistanceInMeter = Integer.parseInt(filterDistance);

          if (filterDistanceInMeter < 0 || filterDistanceInMeter > 500) {
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

      if (filterDistanceInMeter == 0 && !filterByOrder && !filterByNone && !filterByVisit) {
        errorMessageTv.setText(R.string.error_no_filter_selected);
        errorMessageTv.setVisibility(View.VISIBLE);
        return;
      }

      filterApplied = true;
      new RefreshAsyncTask().execute(searchEdt.getText().toString());
      alertDialog.cancel();
    });
  }

  public void doFilter() {
    if (!filterApplied) {
      return;
    }
    for (Iterator<CustomerListModel> it = customerList.iterator(); it.hasNext(); ) {
      CustomerListModel listModel = it.next();
      if ((filterByOrder && filterByOrder != listModel.hasOrder()) || (filterByNone
          && listModel.hasRejection() != filterByNone) || (filterByVisit
          && listModel.isVisited() == filterByVisit)) {
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

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    Fragment f = getChildFragmentManager().findFragmentById(R.id.map);
    if (f != null) {
      getChildFragmentManager().beginTransaction().remove(f).commitAllowingStateLoss();
    }
  }

  private class RefreshAsyncTask extends AsyncTask<String, Void, List<CustomerListModel>> {

    @Override
    protected void onPreExecute() {
      super.onPreExecute();
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
      ////  Shakib, hide progressbar
      customerList = customerListModels;
      if (customerList == null || customerList.size() == 0) {
        noCustomerLay.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
      } else {
        recyclerView.setVisibility(View.VISIBLE);
        noCustomerLay.setVisibility(View.GONE);
        adapter.setDataModel(customerList);
        adapter.notifyDataSetChanged();
      }
      adapter.setDataModel(customerList);
      adapter.notifyDataSetChanged();
    }
  }
}

