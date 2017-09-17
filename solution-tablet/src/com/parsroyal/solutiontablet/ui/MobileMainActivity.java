package com.parsroyal.solutiontablet.ui;

import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.parsroyal.solutiontablet.R;
import com.parsroyal.solutiontablet.ui.fragment.FeaturesFragment;

public class MobileMainActivity extends MainActivity {

  public static final String TAG = MobileMainActivity.class.getSimpleName();

  @BindView(R.id.drawer_layout)
  DrawerLayout drawerLayout;
  @BindView(R.id.app_bar)
  AppBarLayout appBar;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_main_mobile);
    ButterKnife.bind(this);

    showFeaturesFragment();
  }

  @Override
  protected void onStart() {
    super.onStart();
  }

  @Override
  protected void onResume() {
    super.onResume();
  }

  @Override
  protected void onPause() {
    super.onPause();
  }

  @Override
  protected void onStop() {
    super.onStop();
  }

  public void closeDrawer() {
    if (drawerLayout.isDrawerOpen(GravityCompat.END)) {
      drawerLayout.closeDrawer(GravityCompat.END);
    }
  }

  @Override
  public void onBackPressed() {
    if (drawerLayout.isDrawerOpen(GravityCompat.END)) {
      drawerLayout.closeDrawer(GravityCompat.END);
      return;
    }
    super.onBackPressed();
  }

  @Override
  public void changeTitle(String title) {
    toolbarTitle.setText(title);
  }

  @Override
  public void customizeToolbar(int fragmentId) {

  }

  public void onNavigationTapped() {
    Fragment featureFragment = getSupportFragmentManager()
        .findFragmentByTag(FeaturesFragment.class.getSimpleName());
    if (featureFragment != null && featureFragment.isVisible()) {
      if (drawerLayout.isDrawerOpen(GravityCompat.END)) {
        drawerLayout.closeDrawer(GravityCompat.END);
      } else {
        drawerLayout.openDrawer(GravityCompat.END);
      }
    } else {
      onBackPressed();
    }
  }
}
