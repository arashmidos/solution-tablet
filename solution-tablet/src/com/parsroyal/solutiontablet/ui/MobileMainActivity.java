package com.parsroyal.solutiontablet.ui;

import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.transition.ChangeBounds;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.parsroyal.solutiontablet.R;
import com.parsroyal.solutiontablet.ui.fragment.FeaturesFragment;
import com.parsroyal.solutiontablet.ui.fragment.OrderFragment;
import com.parsroyal.solutiontablet.ui.fragment.QuestionsListFragment;

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
    if (VERSION.SDK_INT >= VERSION_CODES.LOLLIPOP) {
      getWindow().setSharedElementEnterTransition(new ChangeBounds().setDuration(1000));
    }

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
  public void customizeToolbar(int fragmentId) {

  }

  @Override
  protected void setDrawerEnable(boolean enabled) {
    int lockMode = enabled ? DrawerLayout.LOCK_MODE_UNLOCKED :
        DrawerLayout.LOCK_MODE_LOCKED_CLOSED;
    drawerLayout.setDrawerLockMode(lockMode);
  }

  public void onNavigationTapped() {
    Fragment featureFragment = getSupportFragmentManager()
        .findFragmentByTag(FeaturesFragment.class.getSimpleName());
    Fragment orderFragment = getSupportFragmentManager()
        .findFragmentByTag(OrderFragment.class.getSimpleName());
    Fragment questionsListFragment = getSupportFragmentManager()
        .findFragmentByTag(QuestionsListFragment.class.getSimpleName());
    if (questionsListFragment != null && questionsListFragment.isVisible()) {
      ((QuestionsListFragment) questionsListFragment).exit();
    } else if (orderFragment != null && orderFragment.isVisible()) {
      ((OrderFragment) orderFragment).onBackPressed();
    } else if (featureFragment != null && featureFragment.isVisible()) {
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
