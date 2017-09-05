package com.parsroyal.solutiontablet.ui.fragment;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.parsroyal.solutiontablet.R;
import com.parsroyal.solutiontablet.data.model.FeatureList;
import com.parsroyal.solutiontablet.ui.MainActivity;
import com.parsroyal.solutiontablet.ui.adapter.FeaturesAdapter;
import com.parsroyal.solutiontablet.util.RtlGridLayoutManager;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FeaturesFragment extends BaseFragment {

  @BindView(R.id.recycler_view)
  RecyclerView recyclerView;

  private FeaturesAdapter adapter;
  private MainActivity mainActivity;

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
    setUpRecyclerView();
    mainActivity.changeTitle(getString(R.string.features_list));
    return view;
  }

  //set up recycler view
  private void setUpRecyclerView() {
    adapter = new FeaturesAdapter(getActivity(), FeatureList.getFeatureList(getActivity()));
    RtlGridLayoutManager gridLayoutManager = new RtlGridLayoutManager(getActivity(), 2);
    recyclerView.setLayoutManager(gridLayoutManager);
    recyclerView.setAdapter(adapter);
  }

  @Override
  public int getFragmentId() {
    return MainActivity.FEATURE_FRAGMENT_ID;
  }
}
