package com.parsroyal.storemanagement.ui.activity;

import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.parsroyal.storemanagement.R;
import com.parsroyal.storemanagement.ui.fragment.AdminFragment;
import com.parsroyal.storemanagement.ui.fragment.FeaturesFragment;
import com.parsroyal.storemanagement.ui.fragment.OrderFragment;
import com.parsroyal.storemanagement.ui.fragment.QuestionsListFragment;

/**
 * Created by Arash 2017-09-16
 */
public class TabletMainActivity extends MainActivity {

  public static final String TAG = TabletMainActivity.class.getSimpleName();
  @BindView(R.id.drawer_layout)
  DrawerLayout drawerLayout;
  @BindView(R.id.app_bar)
  AppBarLayout appBar;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_main_tablet);
    ButterKnife.bind(this);

    if (savedInstanceState == null) {
      showFeaturesFragment();
    }
//    setupToolbar(savedInstanceState);

  }

  @Override
  protected void setDrawerEnable(boolean enabled) {
    int lockMode = enabled ? DrawerLayout.LOCK_MODE_UNLOCKED :
        DrawerLayout.LOCK_MODE_LOCKED_CLOSED;
    drawerLayout.setDrawerLockMode(lockMode);
  }

  /*@Override
  public void onNavigationTapped() {
    Fragment featureFragment = getSupportFragmentManager()
        .findFragmentByTag(FeaturesFragment.class.getSimpleName());
    if (featureFragment != null && featureFragment.isVisible() && crossFader != null) {
      crossFader.crossFade();
    } else {
      onBackPressed();
    }

  }*/
  public void onNavigationTapped() {
    Fragment featureFragment = getSupportFragmentManager()
        .findFragmentByTag(FeaturesFragment.class.getSimpleName());
    Fragment questionsListFragment = getSupportFragmentManager()
        .findFragmentByTag(QuestionsListFragment.class.getSimpleName());
    Fragment orderFragment = getSupportFragmentManager()
        .findFragmentByTag(OrderFragment.class.getSimpleName());
    Fragment adminFragment = getSupportFragmentManager()
        .findFragmentByTag(AdminFragment.class.getSimpleName());
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
    } else if (adminFragment != null && adminFragment.isVisible()) {
      ((AdminFragment) adminFragment).onBackPressed();
    } else {
      onBackPressed();
    }
  }

  public void closeDrawer() {
    if (drawerLayout.isDrawerOpen(GravityCompat.END)) {
      drawerLayout.closeDrawer(GravityCompat.END);
    }
  }

  @Override
  public void customizeToolbar(int fragmentId) {
    /*if (fragmentId == VISIT_DETAIL_FRAGMENT_ID) {
      detailTv.setVisibility(View.VISIBLE);

    } else {
      detailTv.setVisibility(View.GONE);
    }*/
  }

  @Override
  protected void onSaveInstanceState(Bundle outState) {
    //add the values which need to be saved from the drawer to the bundle
    /*outState = drawer.saveInstanceState(outState);
    //add the values which need to be saved from the accountHeader to the bundle
    outState = headerResult.saveInstanceState(outState);
    //add the values which need to be saved from the crossFader to the bundle
    outState = crossFader.saveInstanceState(outState);*/
    super.onSaveInstanceState(outState);
  }

  @Override
  public void onBackPressed() {
    if (drawerLayout.isDrawerOpen(GravityCompat.END)) {
      drawerLayout.closeDrawer(GravityCompat.END);
      return;
    }
    super.onBackPressed();
  }
}
