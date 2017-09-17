package com.parsroyal.solutiontablet.ui;

import android.os.Bundle;
import butterknife.ButterKnife;
import com.parsroyal.solutiontablet.R;

/**
 * Created by Arash 2017-09-16
 */
public class TabletMainActivity extends MainActivity {

  public static final String TAG = TabletMainActivity.class.getSimpleName();

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_main_tablet);
    ButterKnife.bind(this);

    showFeaturesFragment();
  }

  @Override
  public void onNavigationTapped() {
    //TODO: Implement
  }

  @Override
  public void changeTitle(String title) {
  }

  @Override
  public void closeDrawer() {

  }
}
