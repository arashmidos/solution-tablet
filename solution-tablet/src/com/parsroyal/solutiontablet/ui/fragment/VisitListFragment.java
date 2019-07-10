package com.parsroyal.solutiontablet.ui.fragment;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.LayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import com.parsroyal.solutiontablet.R;
import com.parsroyal.solutiontablet.data.dao.VisitInformationDao;
import com.parsroyal.solutiontablet.data.dao.impl.VisitInformationDaoImpl;
import com.parsroyal.solutiontablet.data.model.VisitListModel;
import com.parsroyal.solutiontablet.ui.activity.MainActivity;
import com.parsroyal.solutiontablet.ui.adapter.VisitListAdapter;
import com.parsroyal.solutiontablet.util.NumberUtil;
import java.util.List;

public class VisitListFragment extends BaseFragment {

  @BindView(R.id.recycler_view)
  RecyclerView recyclerView;

  Unbinder unbinder;
  @BindView(R.id.total_visit_hour)
  TextView totalVisitHour;
  @BindView(R.id.total_visit_count)
  TextView totalVisitCount;

  private MainActivity mainActivity;

  public VisitListFragment() {
    // Required empty public constructor
  }

  public static VisitListFragment newInstance(Bundle arguments) {
    VisitListFragment fragment = new VisitListFragment();
    fragment.setArguments(arguments);
    return fragment;
  }

  @Override
  public void onSaveInstanceState(@NonNull Bundle outState) {
    super.onSaveInstanceState(outState);
  }

  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    View view = inflater.inflate(R.layout.fragment_visit_list, container, false);
    unbinder = ButterKnife.bind(this, view);
    mainActivity = (MainActivity) getActivity();

    setUpRecyclerView();
    return view;
  }

  private void setUpRecyclerView() {

    List<VisitListModel> visitListModel = getVisitListModel();
    totalVisitCount.setText(NumberUtil.digitsToPersian(visitListModel.size()));
    double count = 0;
    for (VisitListModel model : visitListModel) {
      count += model.getPeriod();
    }

    if (count < 60) {
      totalVisitHour.setText(NumberUtil.digitsToPersian(
          String.format(getString(R.string.x_minute), String.valueOf((int) count))));
    } else {
      totalVisitHour.setText(String.format(getString(R.string.x_hour),
          NumberUtil.formatPersian1DecimalPlaces(count / 60.0)));
    }

    VisitListAdapter visitListAdapter = new VisitListAdapter(mainActivity, visitListModel);
    LayoutManager layoutManager = new LinearLayoutManager(mainActivity);
    recyclerView.setLayoutManager(layoutManager);
    recyclerView.setAdapter(visitListAdapter);
  }

  private List<VisitListModel> getVisitListModel() {
    VisitInformationDao visitInformationDao = new VisitInformationDaoImpl(mainActivity);
    return visitInformationDao.getAllVisitList();
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    unbinder.unbind();
  }

  @Override
  public int getFragmentId() {
    return MainActivity.VISIT_LIST_FRAGMENT_ID;
  }
}
