package com.parsroyal.solutiontablet.navigation;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.parsroyal.solutiontablet.R;
import com.parsroyal.solutiontablet.ui.activity.MainActivity;
import com.parsroyal.solutiontablet.ui.fragment.BaseFragment;
import com.parsroyal.solutiontablet.ui.fragment.NewCustomerFragment;

/**
 * Created by Arash on 2019-04-15
 */
public class NavigateListFragment extends BaseFragment {


  public static NavigateListFragment newInstance() {
    NavigateListFragment fragment = new NavigateListFragment();

    return fragment;
  }

  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_navigate_list, null);
  }


  @Override
  public int getFragmentId() {
    return MainActivity.NAVIGATE_LIST_FRAGMENT;
  }
}
