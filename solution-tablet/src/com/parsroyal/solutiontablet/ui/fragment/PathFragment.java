package com.parsroyal.solutiontablet.ui.fragment;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.parsroyal.solutiontablet.R;
import com.parsroyal.solutiontablet.data.listmodel.VisitLineListModel;
import com.parsroyal.solutiontablet.service.VisitService;
import com.parsroyal.solutiontablet.service.impl.VisitServiceImpl;
import com.parsroyal.solutiontablet.ui.MainActivity;
import com.parsroyal.solutiontablet.ui.adapter.PathAdapter;
import com.parsroyal.solutiontablet.util.MultiScreenUtility;
import com.parsroyal.solutiontablet.util.RtlGridLayoutManager;
import java.util.List;

/**
 * @author Arash 2017-08-18
 */
public class PathFragment extends BaseFragment {

  @BindView(R.id.recycler_view)
  RecyclerView recyclerView;

  private PathAdapter adapter;
  private MainActivity mainActivity;
  private VisitService visitService;

  public static PathFragment newInstance() {
    return new PathFragment();
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    View view = inflater.inflate(R.layout.fragment_visitline_list, container, false);
    ButterKnife.bind(this, view);
    mainActivity = (MainActivity) getActivity();
    visitService = new VisitServiceImpl(mainActivity);
    setUpRecyclerView();
    mainActivity.changeTitle(getString(R.string.today_path_list));

    return view;
  }

  //set up recycler view
  private void setUpRecyclerView() {
    adapter = new PathAdapter(mainActivity, getVisitLineList());
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

  @Override
  public int getFragmentId() {
    return MainActivity.PATH_FRAGMENT_ID;
  }
}
