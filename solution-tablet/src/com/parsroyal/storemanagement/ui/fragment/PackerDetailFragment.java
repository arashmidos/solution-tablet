package com.parsroyal.storemanagement.ui.fragment;

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
import com.parsroyal.storemanagement.R;
import com.parsroyal.storemanagement.SolutionTabletApplication;
import com.parsroyal.storemanagement.constants.Authority;
import com.parsroyal.storemanagement.data.event.UpdateListEvent;
import com.parsroyal.storemanagement.data.model.GoodDetail;
import com.parsroyal.storemanagement.data.model.Packer;
import com.parsroyal.storemanagement.ui.activity.MainActivity;
import com.parsroyal.storemanagement.ui.activity.PackerActivity;
import com.parsroyal.storemanagement.ui.adapter.PackerGoodsAdapter;
import com.parsroyal.storemanagement.util.MultiScreenUtility;
import com.parsroyal.storemanagement.util.NumberUtil;
import com.parsroyal.storemanagement.util.RtlGridLayoutManager;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

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
  private List<GoodDetail> goodDetails = new ArrayList<>();

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
    clearTopCounters();
    setUpRecyclerView();
    return view;
  }

  private void checkPermissions() {
    if (!SolutionTabletApplication.getInstance().hasAccess(Authority.ADD_NEW_CUSTOMER)) {
    }
  }

  //set up recycler view

  private void setUpRecyclerView() {
    if (MultiScreenUtility.isTablet(activity)) {
      RtlGridLayoutManager rtlGridLayoutManager = new RtlGridLayoutManager(activity, 2);
      recyclerView.setLayoutManager(rtlGridLayoutManager);
    } else {
      LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
      recyclerView.setLayoutManager(linearLayoutManager);
    }
//    recyclerView.setAdapter(adapter);
  }


  @Override
  public int getFragmentId() {
    return MainActivity.PACKER_DETAIL_FRAGMENT_ID;
  }

  public void update(Packer packer) {
    this.packer = packer;

    goodDetails = packer.getGoodDetails();
    sortByRemaining();
    adapter = new PackerGoodsAdapter(activity, this, goodDetails);
    recyclerView.setAdapter(adapter);
    updateTopCounters();

  }

  private void sortByRemaining() {
    Collections.sort(goodDetails, (g1, g2) -> g2.getRemain().compareTo(g1.getRemain()));
  }

  private void updateTopCounters() {

    int total = 0;
    int packed = 0;
    for (int i = 0; i < goodDetails.size(); i++) {
      GoodDetail goodDetail = goodDetails.get(i);
      total += goodDetail.getQty() / 1000;
      packed += goodDetail.getPacked() / 1000;
    }
    totalPackedCount.setText(NumberUtil.digitsToPersian(packed));
    totalRemainedCount.setText(NumberUtil.digitsToPersian(total - packed));
  }

  private void clearTopCounters() {

    totalPackedCount.setText(NumberUtil.digitsToPersian(0));
    totalRemainedCount.setText(NumberUtil.digitsToPersian(0));
  }

  @Override
  public void onResume() {
    super.onResume();
    EventBus.getDefault().register(this);
  }

  @Override
  public void onPause() {
    super.onPause();
    EventBus.getDefault().unregister(this);
  }

  @Subscribe(threadMode = ThreadMode.MAIN)
  public void getMessage(UpdateListEvent event) {
    updateTopCounters();
    sortByRemaining();
    adapter.notifyDataSetChanged();
  }
}
