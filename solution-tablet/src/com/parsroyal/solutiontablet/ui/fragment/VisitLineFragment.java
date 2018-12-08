package com.parsroyal.solutiontablet.ui.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.alirezaafkar.sundatepicker.DatePicker;
import com.alirezaafkar.sundatepicker.components.JDF;
import com.alirezaafkar.sundatepicker.interfaces.DateSetListener;
import com.parsroyal.solutiontablet.R;
import com.parsroyal.solutiontablet.data.listmodel.VisitLineListModel;
import com.parsroyal.solutiontablet.service.VisitService;
import com.parsroyal.solutiontablet.service.impl.VisitServiceImpl;
import com.parsroyal.solutiontablet.ui.activity.MainActivity;
import com.parsroyal.solutiontablet.ui.adapter.VisitLineAdapter;
import com.parsroyal.solutiontablet.util.DateUtil;
import com.parsroyal.solutiontablet.util.MultiScreenUtility;
import com.parsroyal.solutiontablet.util.NumberUtil;
import com.parsroyal.solutiontablet.util.RtlGridLayoutManager;
import com.parsroyal.solutiontablet.util.SunDate;
import com.parsroyal.solutiontablet.util.ToastUtil;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * @author Arash 2017-08-18
 */
public class VisitLineFragment extends BaseFragment implements DateSetListener {

  @BindView(R.id.recycler_view)
  RecyclerView recyclerView;
  @BindView(R.id.filter_layout)
  LinearLayout filterLayout;
  @BindView(R.id.fromDate)
  EditText fromDateEt;
  @BindView(R.id.toDate)
  EditText toDateEt;

  private SunDate startDate = new SunDate();
  private SunDate endDate = new SunDate();

  private VisitLineAdapter adapter;
  private MainActivity mainActivity;
  private VisitService visitService;

  public static VisitLineFragment newInstance() {
    return new VisitLineFragment();
  }

  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    View view = inflater.inflate(R.layout.fragment_visitline_list, container, false);
    ButterKnife.bind(this, view);
    mainActivity = (MainActivity) getActivity();
    visitService = new VisitServiceImpl(mainActivity);
    setUpRecyclerView();
    mainActivity.changeTitle(getString(R.string.today_path_list));

    setCurrentDate();
    return view;
  }

  private void setCurrentDate() {
    startDate.setDate(new JDF(new GregorianCalendar()));
    endDate.setDate(new JDF(new GregorianCalendar()));

    fromDateEt.setHint(NumberUtil.digitsToPersian(
        startDate.getYear() % 100 + "/" + startDate.getMonth() + "/" + startDate.getDay()));
    toDateEt.setHint(NumberUtil
        .digitsToPersian(
            endDate.getYear() % 100 + "/" + endDate.getMonth() + "/" + endDate.getDay()));

    doFilter();
  }

  //set up recycler view
  private void setUpRecyclerView() {
    adapter = new VisitLineAdapter(mainActivity, getVisitLineList());
    if (MultiScreenUtility.isTablet(mainActivity)) {
      RtlGridLayoutManager rtlGridLayoutManager = new RtlGridLayoutManager(mainActivity, 2);
      recyclerView.setLayoutManager(rtlGridLayoutManager);
    } else {
      LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
      recyclerView.setLayoutManager(linearLayoutManager);
    }

    recyclerView.setAdapter(adapter);
  }

  private List<VisitLineListModel> getVisitLineList() {
    return visitService.getAllVisitLinesListModel();
  }

  public void showFilter() {
    filterLayout.setVisibility(View.VISIBLE);
  }

  @OnClick({R.id.cancel_btn, R.id.filter_btn, R.id.toDate, R.id.fromDate})
  public void onClick(View view) {
    switch (view.getId()) {
      case R.id.filter_btn:
        doFilter();
        break;
      case R.id.cancel_btn:
        filterLayout.setVisibility(View.GONE);
        break;
      case R.id.toDate:
        DatePicker.Builder builder = new DatePicker.Builder().id(2);
        builder.date(endDate.getDay(), endDate.getMonth(), endDate.getYear());
        builder.build(VisitLineFragment.this).show(getFragmentManager(), "");
        break;
      case R.id.fromDate:
        DatePicker.Builder builder2 = new DatePicker.Builder().id(3);
        builder2.date(startDate.getDay(), startDate.getMonth(), startDate.getYear());
        builder2.build(VisitLineFragment.this).show(getFragmentManager(), "");
        break;
    }
  }

  @Override
  public int getFragmentId() {
    return MainActivity.VISITLINE_FRAGMENT_ID;
  }

  @Override
  public void onDateSet(int id, @Nullable Calendar calendar, int day, int month, int year) {
    int tempYear = year % 100;
    if (id == 2) {
      toDateEt.setHint(NumberUtil.digitsToPersian(tempYear + "/" + month + "/" + day));
      endDate.setDate(day, month, year);
    } else if (id == 3) {
      fromDateEt.setHint(NumberUtil.digitsToPersian(tempYear + "/" + month + "/" + day));
      startDate.setDate(day, month, year);
    }
  }

  private void doFilter() {

    Calendar c1 = startDate.getCalendar();
    Calendar c2 = endDate.getCalendar();
    if (validate(c1, c2)) {

      Date from = DateUtil.startOfDay(c1);
      Date to = DateUtil.endOfDay(c2);

      List<VisitLineListModel> filteredList = visitService.getAllVisitLinesListModel(from, to);
      adapter.update(filteredList);
//      drawRoute(lastRoute);
      filterLayout.setVisibility(View.GONE);
    }
  }

  private boolean validate(Calendar c1, Calendar c2) {
    if (fromDateEt.getHint().equals("--")) {
      ToastUtil.toastError(getActivity(), getString(R.string.error_cal1_empty));
      return false;
    }

    if (toDateEt.getHint().equals("--")) {
      ToastUtil.toastError(getActivity(), getString(R.string.error_cal2_empty));
      return false;
    }

    long days = DateUtil.compareDatesInDays(c1, c2);

    if (days < 0) {
      ToastUtil.toastError(getActivity(), getString(R.string.error_report_date_invalid));
      return false;
    }
    return true;
  }
}
