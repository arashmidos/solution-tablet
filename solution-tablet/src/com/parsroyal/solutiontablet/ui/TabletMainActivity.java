package com.parsroyal.solutiontablet.ui;

import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.parsroyal.solutiontablet.R;

/**
 * Created by Arash 2017-09-16
 */
public class TabletMainActivity extends MainActivity {

  public static final String TAG = TabletMainActivity.class.getSimpleName();

  @BindView(R.id.drawer_layout)
  DrawerLayout drawerLayout;

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
    super.onBackPressed();
  }

  @Override
  public void changeTitle(String title) {
    toolbarTitle.setText(title);
  }

  public void closeDrawer() {
    if (drawerLayout.isDrawerOpen(GravityCompat.END)) {
      drawerLayout.closeDrawer(GravityCompat.END);
    }
  }

  @Override
  public void customizeToolbar(int fragmentId) {
    if (fragmentId == VISIT_DETAIL_FRAGMENT_ID) {
      detailTv.setVisibility(View.VISIBLE);
    } else {
      detailTv.setVisibility(View.GONE);
    }
  }
}
