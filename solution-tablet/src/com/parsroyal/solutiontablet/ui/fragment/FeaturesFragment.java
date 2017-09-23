package com.parsroyal.solutiontablet.ui.fragment;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.parsroyal.solutiontablet.R;
import com.parsroyal.solutiontablet.data.model.FeatureList;
import com.parsroyal.solutiontablet.service.VisitService;
import com.parsroyal.solutiontablet.service.impl.VisitServiceImpl;
import com.parsroyal.solutiontablet.ui.MainActivity;
import com.parsroyal.solutiontablet.ui.adapter.FeaturesAdapter;
import com.parsroyal.solutiontablet.util.MultiScreenUtility;
import com.parsroyal.solutiontablet.util.RtlGridLayoutManager;
import java.util.List;

public class FeaturesFragment extends BaseFragment {

  @BindView(R.id.recycler_view)
  RecyclerView recyclerView;

  private FeaturesAdapter adapter;
  private MainActivity mainActivity;
  private VisitService visitService;

  public FeaturesFragment() {
    // Required empty public constructor
  }

  public static FeaturesFragment newInstance() {
    return new FeaturesFragment();
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    View view = inflater.inflate(R.layout.fragment_features, container, false);
    ButterKnife.bind(this, view);
    mainActivity = (MainActivity) getActivity();
    visitService = new VisitServiceImpl(mainActivity);
    setUpRecyclerView();
    mainActivity.changeTitle(getString(R.string.features_list));
    return view;
  }

  //set up recycler view
  private void setUpRecyclerView() {
    List<FeatureList> featureList = FeatureList.getFeatureList(getActivity());
    featureList.get(0).setBadger(getVisitLineSize());
    adapter = new FeaturesAdapter(getActivity(), featureList);
    RtlGridLayoutManager gridLayoutManager;
    if (MultiScreenUtility.isTablet(mainActivity)) {
      gridLayoutManager = new RtlGridLayoutManager(getActivity(), 3);
    } else {
      gridLayoutManager = new RtlGridLayoutManager(getActivity(), 2);
    }
    recyclerView.setLayoutManager(gridLayoutManager);
    recyclerView.setAdapter(adapter);
  }

  private int getVisitLineSize() {
    return visitService.getAllVisitLinesListModel().size();
  }

  @Override
  public int getFragmentId() {
    return MainActivity.FEATURE_FRAGMENT_ID;
  }
}
