package com.parsroyal.storemanagement.ui.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.parsroyal.storemanagement.R;
import com.parsroyal.storemanagement.ui.activity.MainActivity;

/**
 * Created by Arash on 8/4/2015.
 */
public class AboutUsFragment extends BaseFragment {

  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_about_us, null);
  }

  @Override
  public int getFragmentId() {
    return MainActivity.ABOUT_US_FRAGMENT_ID;
  }
}
