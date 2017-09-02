package com.parsroyal.solutiontablet.ui.fragment;

import android.os.AsyncTask;
import android.os.Bundle;
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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.parsroyal.solutiontablet.R;
import com.parsroyal.solutiontablet.constants.Constants;
import com.parsroyal.solutiontablet.data.listmodel.CustomerListModel;
import com.parsroyal.solutiontablet.data.listmodel.VisitLineListModel;
import com.parsroyal.solutiontablet.service.CustomerService;
import com.parsroyal.solutiontablet.service.VisitService;
import com.parsroyal.solutiontablet.service.impl.CustomerServiceImpl;
import com.parsroyal.solutiontablet.service.impl.VisitServiceImpl;
import com.parsroyal.solutiontablet.ui.MainActivity;
import com.parsroyal.solutiontablet.ui.adapter.PathDetailAdapter;
import com.parsroyal.solutiontablet.util.Analytics;
import com.parsroyal.solutiontablet.util.Empty;

import java.text.Collator;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @author Shakib
 */
public class PathDetailFragment extends BaseFragment {

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
  private String filterDistance = "";
  private int filterDistanceInMeter;
  private boolean filterApplied = false;

  public static PathDetailFragment newInstance() {
    return new PathDetailFragment();
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    View view = inflater.inflate(R.layout.fragment_path_detail, container, false);
    ButterKnife.bind(this, view);
    Bundle args = getArguments();
    visitlineBackendId = args.getLong(Constants.VISITLINE_BACKEND_ID, -1);
    if (visitlineBackendId == -1) {
      //TODO: LOG ERROR
    }

    mainActivity = (MainActivity) getActivity();
    customerService = new CustomerServiceImpl(mainActivity);
    visitService = new VisitServiceImpl(mainActivity);
    setData();
    onSearchTextChanged();
    setUpRecyclerView();
    return view;
  }

  @Override
  public int getFragmentId() {
    return MainActivity.PATH_DETAIL_FRAGMENT_ID;
  }

  private void setData() {
    visitline = visitService.getVisitLineListModelByBackendId(visitlineBackendId);

    customersNumberTv
        .setText(String.format(getString(R.string.x_customers), visitline.getCustomerCount()));
    pathCodeTv.setText(String.format(getString(R.string.visitline_code_x), visitline.getCode()));
    mainActivity
        .changeTitle(String.format(getString(R.string.visitline_code_x), visitline.getCode()));
  }

  //set up recycler view
  private void setUpRecyclerView() {
    adapter = new PathDetailAdapter(mainActivity, getCustomersList(), visitlineBackendId);
    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mainActivity);
    recyclerView.setLayoutManager(linearLayoutManager);
    recyclerView.setAdapter(adapter);
  }

  private List<CustomerListModel> getCustomersList() {
    customerList = customerService.getFilteredCustomerList(visitlineBackendId,"");
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
          searchImg.setVisibility(View.VISIBLE);
        } else {
          searchImg.setVisibility(View.GONE);
        }
      }

      @Override
      public void afterTextChanged(Editable s) {
        new RefreshAsyncTask().execute(s.toString());
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

      new RefreshAsyncTask().execute(searchEdt.getText().toString());
      alertDialog.cancel();
    });
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
      new RefreshAsyncTask().execute(searchEdt.getText().toString());
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
      adapter.setDataModel(customerList);
      adapter.notifyDataSetChanged();
    }
  }
}
