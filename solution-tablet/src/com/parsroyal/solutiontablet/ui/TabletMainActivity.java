package com.parsroyal.solutiontablet.ui;

import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.mikepenz.crossfader.Crossfader;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.MiniDrawer;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialize.util.UIUtils;
import com.parsroyal.solutiontablet.R;
import com.parsroyal.solutiontablet.service.impl.SettingServiceImpl;
import com.parsroyal.solutiontablet.ui.component.CrossfadeWrapper;
import com.parsroyal.solutiontablet.ui.fragment.FeaturesFragment;
import com.parsroyal.solutiontablet.ui.fragment.OrderFragment;
import com.parsroyal.solutiontablet.ui.fragment.QuestionsListFragment;
import com.parsroyal.solutiontablet.util.Empty;
import com.parsroyal.solutiontablet.util.constants.ApplicationKeys;

/**
 * Created by Arash 2017-09-16
 */
public class TabletMainActivity extends MainActivity {

  public static final String TAG = TabletMainActivity.class.getSimpleName();
  @BindView(R.id.drawer_layout)
  DrawerLayout drawerLayout;
  @BindView(R.id.app_bar)
  AppBarLayout appBar;

  private Drawer drawer;
  private AccountHeader headerResult;
  private SettingServiceImpl settingService;
  private Crossfader crossFader;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_main_tablet);
    ButterKnife.bind(this);

    settingService = new SettingServiceImpl(this);
    if (savedInstanceState == null) {
      showFeaturesFragment();
    }
//    setupToolbar(savedInstanceState);

  }

  private void setupToolbar(Bundle savedInstanceState) {
    Drawable drawable = getResources().getDrawable(R.drawable.ic_account_circle_black_24dp);
    drawable.setTint(getResources().getColor(R.color.primary_dark));
    String fullName = settingService.getSettingValue(ApplicationKeys.USER_FULL_NAME);

    headerResult = new AccountHeaderBuilder()
        .withActivity(this)
        .withCompactStyle(true)
        .withTextColor(getResources().getColor(android.R.color.black))
//        .withHeaderBackground(R.drawable.header)
        .addProfiles(
            new ProfileDrawerItem().withName(Empty.isNotEmpty(fullName) ? fullName : "")
                .withEmail("ویزیتور")
                .withIcon(drawable)
        )
        .withOnAccountHeaderListener((view, profile, currentProfile) -> false)
        .build();
    //if you want to update the items at a later time it is recommended to keep it in a variable
    PrimaryDrawerItem item1 = new PrimaryDrawerItem().withIdentifier(1).withName("Home")
        .withTextColor(ContextCompat.getColor(this, R.color.black));
    SecondaryDrawerItem item2 = new SecondaryDrawerItem().withIdentifier(2).withName("Settings");

    drawer = new DrawerBuilder()
        .withActivity(this)
        .withCloseOnClick(true)
        .withActionBarDrawerToggle(false)
        .withToolbar((Toolbar) findViewById(R.id.toolbar))
        .withAccountHeader(headerResult)
        .withDrawerGravity(Gravity.END)
        .withDisplayBelowStatusBar(true)
        .addDrawerItems(
            item1,
            new DividerDrawerItem(),
            item2,
            new SecondaryDrawerItem().withName("Settings2")
        )
        .withOnDrawerItemClickListener((view, position, drawerItem) -> {
          // do something with the clicked item :D
          Toast.makeText(TabletMainActivity.this, "Clicked on item " + position,
              Toast.LENGTH_SHORT)
              .show();
          drawer.closeDrawer();
          return true;
        })
        .withGenerateMiniDrawer(true)
        .withSavedInstance(savedInstanceState)
        .buildView();
    //the result object also allows you to add new items, remove items, add footer, sticky footer, ..

    MiniDrawer mini = drawer.getMiniDrawer();

    mini.withInRTL(false);
    //get the widths in px for the first and second panel
    int firstWidth = (int) UIUtils.convertDpToPixel(300, this);
    int secondWidth = (int) UIUtils.convertDpToPixel(72, this);

    //create and build our crossfader (see the MiniDrawer is also builded in here, as the build method returns the view to be used in the crossfader)
    //the crossfader library can be found here: https://github.com/mikepenz/Crossfader
    crossFader = new Crossfader()
        .withContent(findViewById(R.id.container))
        .withFirst(drawer.getSlider(), firstWidth)
        .withSecond(mini.build(this), secondWidth)
        .withSavedInstance(savedInstanceState)

        .build();

    //define the crossfader to be used with the miniDrawer. This is required to be able to automatically toggle open / close
    mini.withCrossFader(new CrossfadeWrapper(crossFader));

    //define a shadow (this is only for normal LTR layouts if you have a RTL app you need to define the other one
    crossFader.getCrossFadeSlidingPaneLayout()
        .setShadowResourceLeft(R.drawable.material_drawer_shadow_left);
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

 /* @Override
  public void closeDrawer() {
    if (crossFader != null && crossFader.isCrossFaded()) {
      crossFader.crossFade();
    }
  }*/

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

  /*  @Override
    public void onBackPressed() {
      if (crossFader != null && crossFader.isCrossFaded()) {
        crossFader.crossFade();
        return;
      }
      super.onBackPressed();
    }*/
  @Override
  public void onBackPressed() {
    if (drawerLayout.isDrawerOpen(GravityCompat.END)) {
      drawerLayout.closeDrawer(GravityCompat.END);
      return;
    }
    super.onBackPressed();
  }
}
