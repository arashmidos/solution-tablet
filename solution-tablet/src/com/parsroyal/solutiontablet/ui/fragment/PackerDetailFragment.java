package com.parsroyal.solutiontablet.ui.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import com.parsroyal.solutiontablet.R;
import com.parsroyal.solutiontablet.SolutionTabletApplication;
import com.parsroyal.solutiontablet.constants.Authority;
import com.parsroyal.solutiontablet.data.model.Packer;
import com.parsroyal.solutiontablet.ui.activity.MainActivity;
import com.parsroyal.solutiontablet.ui.activity.PackerActivity;
import com.parsroyal.solutiontablet.ui.adapter.PackerGoodsAdapter;
import com.parsroyal.solutiontablet.util.MultiScreenUtility;
import com.parsroyal.solutiontablet.util.NumberUtil;
import com.parsroyal.solutiontablet.util.RtlGridLayoutManager;

public class PackerDetailFragment extends BaseFragment {

  @BindView(R.id.recycler_view)
  RecyclerView recyclerView;
  Unbinder unbinder;
  @BindView(R.id.total_remained_count)
  TextView totalRemainedCount;
  @BindView(R.id.total_packed_count)
  TextView totalPackedCount;
  private PackerActivity activity;
  private PackerActivity packerActivity;
  private Packer packer;
  private PackerGoodsAdapter adapter;

  public PackerDetailFragment() {
    // Required empty public constructor
  }

  public static PackerDetailFragment newInstance() {
    PackerDetailFragment fragment = new PackerDetailFragment();
    return fragment;
  }

  public static PackerDetailFragment newInstance(Bundle arguments) {
    PackerDetailFragment fragment = new PackerDetailFragment();
    fragment.setArguments(arguments);
    return fragment;
  }

  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    View view = inflater.inflate(R.layout.fragment_packer_detail, container, false);
    ButterKnife.bind(this, view);
    activity = (PackerActivity) getActivity();

//    checkPermissions();
    updateTopCounters();
    setUpRecyclerView();
    return view;
  }

  private void checkPermissions() {
    if (!SolutionTabletApplication.getInstance().hasAccess(Authority.ADD_NEW_CUSTOMER)) {
    }
  }

  //set up recycler view

  private void setUpRecyclerView() {
    adapter = new PackerGoodsAdapter(activity, this, packer);
    if (MultiScreenUtility.isTablet(activity)) {
      RtlGridLayoutManager rtlGridLayoutManager = new RtlGridLayoutManager(activity, 2);
      recyclerView.setLayoutManager(rtlGridLayoutManager);
    } else {
      LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
      recyclerView.setLayoutManager(linearLayoutManager);
    }
    recyclerView.setAdapter(adapter);
  }


  @Override
  public int getFragmentId() {
    return MainActivity.PACKER_DETAIL_FRAGMENT_ID;
  }

  public void update(Packer packer) {
    this.packer = packer;
    updateTopCounters();
  }

  private void updateTopCounters() {

    totalPackedCount.setText(NumberUtil.digitsToPersian(120));
    totalRemainedCount.setText(NumberUtil.digitsToPersian(50));
  }
}
