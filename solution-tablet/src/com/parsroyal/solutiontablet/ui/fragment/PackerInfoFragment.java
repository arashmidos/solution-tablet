package com.parsroyal.solutiontablet.ui.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.ButterKnife;
import com.parsroyal.solutiontablet.R;
import com.parsroyal.solutiontablet.SolutionTabletApplication;
import com.parsroyal.solutiontablet.constants.Authority;
import com.parsroyal.solutiontablet.ui.activity.MainActivity;
import com.parsroyal.solutiontablet.ui.activity.PackerActivity;

public class PackerInfoFragment extends BaseFragment {

  private PackerActivity activity;

  public PackerInfoFragment() {
    // Required empty public constructor
  }

  public static PackerInfoFragment newInstance() {
    PackerInfoFragment fragment = new PackerInfoFragment();
    return fragment;
  }

  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    View view = inflater.inflate(R.layout.fragment_packer_info, container, false);
    ButterKnife.bind(this, view);
    activity = (PackerActivity) getActivity();

//    checkPermissions();
    return view;
  }

  private void checkPermissions() {
    if (!SolutionTabletApplication.getInstance().hasAccess(Authority.ADD_NEW_CUSTOMER)) {
    }
  }

  //set up recycler view
//  private void setUpRecyclerView() {
//    adapter = new NewCustomerAdapter(activity, isSend, getCustomersList());
//    if (MultiScreenUtility.isTablet(activity)) {
//      RtlGridLayoutManager rtlGridLayoutManager = new RtlGridLayoutManager(activity, 2);
//      recyclerView.setLayoutManager(rtlGridLayoutManager);
//    } else {
//      LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
//      recyclerView.setLayoutManager(linearLayoutManager);
//    }
//    recyclerView.setAdapter(adapter);
//    recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
//      @Override
//      public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
//        super.onScrolled(recyclerView, dx, dy);
//        if (dy > 0) {
//          fabAddCustomer.setVisibility(View.GONE);
//        } else {
//          fabAddCustomer.setVisibility(View.VISIBLE);
//        }
//      }
//    });
//  }

  @Override
  public int getFragmentId() {
    return MainActivity.PACKER_INFO_FRAGMENT_ID;
  }
}
