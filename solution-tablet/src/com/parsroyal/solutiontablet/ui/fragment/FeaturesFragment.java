package com.parsroyal.solutiontablet.ui.fragment;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.parsroyal.solutiontablet.R;
import com.parsroyal.solutiontablet.constants.StatusCodes;
import com.parsroyal.solutiontablet.data.event.ActionEvent;
import com.parsroyal.solutiontablet.data.model.FeatureList;
import com.parsroyal.solutiontablet.service.VisitService;
import com.parsroyal.solutiontablet.service.impl.VisitServiceImpl;
import com.parsroyal.solutiontablet.ui.activity.MainActivity;
import com.parsroyal.solutiontablet.ui.adapter.FeaturesAdapter;
import com.parsroyal.solutiontablet.util.MultiScreenUtility;
import com.parsroyal.solutiontablet.util.RtlGridLayoutManager;
import java.util.List;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.jetbrains.annotations.NotNull;

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
  public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
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

  @Override
  public void onResume() {
    super.onResume();
    EventBus.getDefault().register(this);
    mainActivity.showMenu();
  }

  @Override
  public void onPause() {
    super.onPause();
    EventBus.getDefault().unregister(this);
  }

  @Subscribe
  public void getMessage(ActionEvent event) {
    if (event.getStatusCode() == StatusCodes.ACTION_REFRESH_DATA) {
      List<FeatureList> featureList = FeatureList.getFeatureList(getActivity());
      featureList.get(0).setBadger(getVisitLineSize());
      adapter.update(featureList);
    }
  }
}
