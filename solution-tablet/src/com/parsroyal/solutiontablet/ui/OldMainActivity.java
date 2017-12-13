package com.parsroyal.solutiontablet.ui;

import android.Manifest;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.crashlytics.android.Crashlytics;
import com.parsroyal.solutiontablet.BuildConfig;
import com.parsroyal.solutiontablet.R;
import com.parsroyal.solutiontablet.constants.Constants;
import com.parsroyal.solutiontablet.data.entity.KeyValue;
import com.parsroyal.solutiontablet.data.event.UpdateEvent;
import com.parsroyal.solutiontablet.exception.BusinessException;
import com.parsroyal.solutiontablet.receiver.TrackerAlarmReceiver;
import com.parsroyal.solutiontablet.service.DataTransferService;
import com.parsroyal.solutiontablet.service.LocationUpdatesService;
import com.parsroyal.solutiontablet.service.SettingService;
import com.parsroyal.solutiontablet.service.impl.DataTransferServiceImpl;
import com.parsroyal.solutiontablet.service.impl.SettingServiceImpl;
import com.parsroyal.solutiontablet.ui.adapter.DrawerArrayAdapter;
import com.parsroyal.solutiontablet.ui.fragment.AboutUsFragment;
import com.parsroyal.solutiontablet.ui.fragment.BaseFragment;
import com.parsroyal.solutiontablet.ui.fragment.DataTransferFragment;
import com.parsroyal.solutiontablet.ui.fragment.GeneralQuestionnairesFragment;
import com.parsroyal.solutiontablet.ui.fragment.GoodsListForQuestionnairesFragment;
import com.parsroyal.solutiontablet.ui.fragment.GoodsQuestionnairesFragment;
import com.parsroyal.solutiontablet.ui.fragment.KPIFragment;
import com.parsroyal.solutiontablet.ui.fragment.OldGoodsListFragment;
import com.parsroyal.solutiontablet.ui.fragment.OldVisitDetailFragment;
import com.parsroyal.solutiontablet.ui.fragment.OrderDetailFragment;
import com.parsroyal.solutiontablet.ui.fragment.QuestionnairesListFragment;
import com.parsroyal.solutiontablet.ui.fragment.SaveLocationFragment;
import com.parsroyal.solutiontablet.ui.fragment.UserTrackingFragment;
import com.parsroyal.solutiontablet.ui.fragment.dialog.LoginDialogFragment;
import com.parsroyal.solutiontablet.ui.observer.ResultObserver;
import com.parsroyal.solutiontablet.util.Analytics;
import com.parsroyal.solutiontablet.util.DialogUtil;
import com.parsroyal.solutiontablet.util.Empty;
import com.parsroyal.solutiontablet.util.GPSUtil;
import com.parsroyal.solutiontablet.util.Logger;
import com.parsroyal.solutiontablet.util.NetworkUtil;
import com.parsroyal.solutiontablet.util.PreferenceHelper;
import com.parsroyal.solutiontablet.util.ToastUtil;
import com.parsroyal.solutiontablet.util.Updater;
import com.parsroyal.solutiontablet.util.constants.ApplicationKeys;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

/**
 * Created by Mahyar on 6/2/2015.
 */
public class OldMainActivity extends BaseFragmentActivity implements ResultObserver {

  public static final String TAG = OldMainActivity.class.getSimpleName();

  public static final int CUSTOMER_LIST_FRAGMENT_ID = 0;
  public static final int NEW_CUSTOMER_FRAGMENT_ID = 1;
  public static final int NEW_CUSTOMER_DETAIL_FRAGMENT_ID = 2;
  public static final int CUSTOMERS_FRAGMENT_ID = 3;
  public static final int CUSTOMER_DETAIL_FRAGMENT_ID = 4;
  public static final int VISIT_DETAIL_FRAGMENT_ID = 5;
  public static final int GENERAL_QUESTIONNAIRES_FRAGMENT_ID = 6;
  public static final int QUESTIONNAIRE_DETAIL_FRAGMENT_ID = 7;
  public static final int GOODS_QUESTIONNAIRES_FRAGMENT_ID = 8;
  public static final int GOODS_LIST_FOR_QUESTIONNAIRES_FRAGMENT_ID = 9;
  public static final int SETTING_FRAGMENT_ID = 10;
  public static final int DATA_TRANSFER_FRAGMENT_ID = 11;
  public static final int DASHBOARD_FRAGMENT_ID = 12;
  public static final int ABOUT_US_FRAGMENT_ID = 13;
  public static final int ORDERS_LIST_FRAGMENT = 14;
  public static final int ORDER_DETAIL_FRAGMENT_ID = 15;
  public static final int GOODS_LIST_FRAGMENT_ID = 16;
  public static final int SAVE_LOCATION_FRAGMENT_ID = 17;
  public static final int PAYMENT_FRAGMENT_ID = 18;
  public static final int PAYMENT_DETAIL_FRAGMENT_ID = 19;
  public static final int USER_TRACKING_FRAGMENT_ID = 20;
  public static final int KPI_CUSTOMER_FRAGMENT_ID = 21;
  public static final int KPI_SALESMAN_FRAGMENT_ID = 22;
  public static final int FUNDS_FRAGMENT_ID = 23;
  public static final int CUSTOMER_TRACKING_FRAGMENT_ID = 24;
  public static final int BASE_TRACKING_FRAGMENT_ID = 25;
  public static final int QUESTIONAIRE_LIST_FRAGMENT_ID = 26;
  private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;
  private final Integer[] drawerItemTitles = {
      R.string.setting,
      R.string.data_transfer,
      R.string.about_us,
      R.string.version,
      R.string.exit
  };
  private final Integer[] drawerItemImages = {
//      R.drawable.ic_settings_43dp,
//      R.drawable.ic_transform_43dp,
//      R.drawable.ic_aboutus_43dp,
//      R.drawable.ic_version_43dp,
//      R.drawable.ic_exit_43dp,
  };
  @BindView(R.id.mainLayout)
  LinearLayout mainLayout;
  private DrawerLayout mDrawerLayout;
  private ListView drawerItemsList;
  private ImageView customerListTabIv;
  private ImageView newCustomerTabIv;
  private ImageView dashBoardTabIv;
  private ImageView ordersTabIv;
  private ImageView goodsTabIv;
  private ImageView userPerformanceTabIv;
  private ImageView fundsTabIv;
  private ImageView questionaireTabIv;
  private DataTransferService dataTransferService;
  private SettingService settingService;
  private boolean isMenuEnabled = true;
  private LocationUpdatesService gpsRecieverService = null;

  private boolean boundToGpsService = false;
  // Monitors the state of the connection to the service.
  private final ServiceConnection serviceConnection = new ServiceConnection() {

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
      LocationUpdatesService.LocalBinder binder = (LocationUpdatesService.LocalBinder) service;
      gpsRecieverService = binder.getService();
      boundToGpsService = true;
      gpsRecieverService.requestLocationUpdates();
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
      gpsRecieverService = null;
      boundToGpsService = false;
    }
  };
  private BroadcastReceiver gpsStatusReceiver = new BroadcastReceiver() {
    @Override
    public void onReceive(Context context, Intent intent) {
      if (intent.getAction().matches("android.location.PROVIDERS_CHANGED")) {
        if (!GPSUtil.isGpsAvailable(context)) {
          showGpsOffDialog();
          Analytics.logCustom("GPS", new String[]{"GPS Status"}, "OFF");
        }
      }
    }
  };

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    dataTransferService = new DataTransferServiceImpl(this);
    settingService = new SettingServiceImpl(this);
    setContentView(R.layout.activity_main_old);
    ButterKnife.bind(this);

    setupSidebar();
    setupActionbar();
    setupDrawer();
    initialize();
    if (!BuildConfig.DEBUG) {
      logUser();
    }

    if (!checkPermissions()) {
      requestPermissions();
    }
  }

  public void startGpsService() {
    gpsRecieverService.requestLocationUpdates();
  }

  private void logUser() {
    Crashlytics.setUserName(userFullNameTxt.getText().toString());
    Crashlytics.setString("Company", companyNameTxt.getText().toString());

    KeyValue saleType = PreferenceHelper.retrieveByKey(ApplicationKeys.SETTING_SALE_TYPE);
    Crashlytics.setString("Sale Type", saleType == null ? "" : saleType.getValue());
  }

  private void initialize() {
    changeFragment(NEW_CUSTOMER_FRAGMENT_ID, false);
  }

  private void setupSidebar() {
    View.OnClickListener sideBarItemsOnClickListener = view ->
    {
      if (isMenuEnabled || BuildConfig.DEBUG) {
        int fragmentId = Integer.parseInt(view.getTag().toString());
        changeFragment(fragmentId, true);
      }
    };
    customerListTabIv = (ImageView) findViewById(R.id.customerListTabIv);
    customerListTabIv.setOnClickListener(sideBarItemsOnClickListener);
    newCustomerTabIv = (ImageView) findViewById(R.id.newCustomerTabIv);
    newCustomerTabIv.setOnClickListener(sideBarItemsOnClickListener);
    dashBoardTabIv = (ImageView) findViewById(R.id.dashboardTabIv);
    dashBoardTabIv.setOnClickListener(sideBarItemsOnClickListener);
    ordersTabIv = (ImageView) findViewById(R.id.ordersTabIv);
    ordersTabIv.setOnClickListener(sideBarItemsOnClickListener);
    goodsTabIv = (ImageView) findViewById(R.id.goodsTabIv);
    goodsTabIv.setOnClickListener(sideBarItemsOnClickListener);
    userPerformanceTabIv = (ImageView) findViewById(R.id.userPerformanceTabIv);
    userPerformanceTabIv.setOnClickListener(sideBarItemsOnClickListener);
    fundsTabIv = (ImageView) findViewById(R.id.fundsTabIv);
    fundsTabIv.setOnClickListener(sideBarItemsOnClickListener);
    questionaireTabIv = (ImageView) findViewById(R.id.questionaryTabIv);
    questionaireTabIv.setOnClickListener(sideBarItemsOnClickListener);
  }

  public void changeSidebarItem(int fragmentId) {
    setAllImagesInactive();
    String contentName = "";
    switch (fragmentId) {
      case 0:
//        customerListTabIv.setImageResource(R.drawable.ic_sidebar_customer_list_active);
        contentName = "Customer List";
        break;
      case 1:
//        newCustomerTabIv.setImageResource(R.drawable.ic_sidebar_new_customer_active);
        contentName = "New Customer";
        break;
      case 12:
//        dashBoardTabIv.setImageResource(R.drawable.ic_sidebar_map_active);
        contentName = "Map";
        break;
      case 14:
//        ordersTabIv.setImageResource(R.drawable.ic_sidebar_report_active);
        contentName = "Reports";
        break;
      case 16:
//        goodsTabIv.setImageResource(R.drawable.ic_sidebar_goods_active);
        contentName = "Goods List";
        break;
      case KPI_SALESMAN_FRAGMENT_ID://22
//        userPerformanceTabIv.setImageResource(R.drawable.ic_sidebar_salesman_performance_active);
        contentName = "User KPI";
        break;
      case FUNDS_FRAGMENT_ID://23
//        fundsTabIv.setImageResource(R.drawable.ic_sidebar_cash_report_active);
        contentName = "Payment Reports";
        break;
      case QUESTIONAIRE_LIST_FRAGMENT_ID://26
//        questionaireTabIv.setImageResource(R.drawable.ic_sidebar_questionaire_active);
        contentName = "New Questionnaire";
        break;
    }
    Analytics.logContentView(contentName);
  }

  private void setAllImagesInactive() {
    /*customerListTabIv.setImageResource(R.drawable.ic_sidebar_customer_list);
    dashBoardTabIv.setImageResource(R.drawable.ic_sidebar_map_inactive);
    newCustomerTabIv.setImageResource(R.drawable.ic_sidebar_new_customer);
    ordersTabIv.setImageResource(R.drawable.ic_sidebar_report_inactive);
    goodsTabIv.setImageResource(R.drawable.ic_sidebar_goods);
    userPerformanceTabIv.setImageResource(R.drawable.ic_sidebar_salesman_performance);
    fundsTabIv.setImageResource(R.drawable.ic_sidebar_cash_report);
    questionaireTabIv.setImageResource(R.drawable.ic_sidebar_questionaire_inactive);*/
  }

  public void changeFragment(int fragmentId, boolean addToBackStack) {
    BaseFragment fragment = findFragment(fragmentId, new Bundle());
    if (Empty.isNotEmpty(fragment) && !fragment.isVisible()) {
      commitFragment(fragment.getFragmentTag(), fragment, addToBackStack);
    }
  }

  public void changeFragment(int fragmentId, Bundle args, boolean addToBackStack) {
    BaseFragment fragment = findFragment(fragmentId, args);
    if (Empty.isNotEmpty(fragment) && !fragment.isVisible()) {
      if (Empty.isNotEmpty(args)) {
        fragment.setArguments(args);
      }
      commitFragment(fragment.getFragmentTag(), fragment, addToBackStack);
    }
  }

  private void commitFragment(String fragmentTag, BaseFragment fragment, boolean addToBackStack) {
    FragmentTransaction fragmentTransaction;
    FragmentManager fragmentManager = getSupportFragmentManager();
    fragmentTransaction = fragmentManager.beginTransaction();
    fragmentTransaction.replace(R.id.contentFrame, fragment, fragmentTag);
    fragmentTransaction.addToBackStack(fragmentTag);
    fragmentTransaction.commit();
  }

  private BaseFragment findFragment(int fragmentId, Bundle args) {
    BaseFragment fragment = null;
    int parent = 0;
    switch (fragmentId) {
      case CUSTOMER_LIST_FRAGMENT_ID:
//        fragment = new VisitLinesFragment();
        break;
      case NEW_CUSTOMER_FRAGMENT_ID:
//        fragment = new NCustomersFragment();
        changeSidebarItem(OldMainActivity.NEW_CUSTOMER_FRAGMENT_ID);
        break;
      case NEW_CUSTOMER_DETAIL_FRAGMENT_ID:
//        fragment = new NCustomerDetailFragment();
        changeSidebarItem(OldMainActivity.NEW_CUSTOMER_FRAGMENT_ID);
        break;
      case CUSTOMERS_FRAGMENT_ID:
//        fragment = new CustomersFragment();
        changeSidebarItem(OldMainActivity.CUSTOMER_LIST_FRAGMENT_ID);
        break;
      case CUSTOMER_DETAIL_FRAGMENT_ID:
//        fragment = new CustomerDetailFragment();
        changeSidebarItem(OldMainActivity.CUSTOMER_LIST_FRAGMENT_ID);
        break;
      case VISIT_DETAIL_FRAGMENT_ID:
        fragment = new OldVisitDetailFragment();
        changeSidebarItem(OldMainActivity.CUSTOMER_LIST_FRAGMENT_ID);
        setMenuEnabled(false);
        break;
      case GENERAL_QUESTIONNAIRES_FRAGMENT_ID://6
        fragment = new GeneralQuestionnairesFragment();
        parent = args.getInt(Constants.PARENT, 0);
        changeSidebarItem(parent);
        break;
      case QUESTIONNAIRE_DETAIL_FRAGMENT_ID:
//        fragment = new QuestionnaireDetailFragment();
        parent = args.getInt(Constants.PARENT);
        changeSidebarItem(parent);
        break;
      case GOODS_QUESTIONNAIRES_FRAGMENT_ID://8
        fragment = new GoodsQuestionnairesFragment();
        changeSidebarItem(CUSTOMER_LIST_FRAGMENT_ID);
        break;
      case GOODS_LIST_FOR_QUESTIONNAIRES_FRAGMENT_ID:
        fragment = new GoodsListForQuestionnairesFragment();
        break;
      case SETTING_FRAGMENT_ID:
//        fragment = new SettingFragment();
        changeSidebarItem(-1);
        break;
      case DATA_TRANSFER_FRAGMENT_ID:
        if (isDataTransferPossible()) {
          fragment = new DataTransferFragment();
          changeSidebarItem(-1);
        }
        break;
      case DASHBOARD_FRAGMENT_ID:
        fragment = new UserTrackingFragment();
        changeSidebarItem(DASHBOARD_FRAGMENT_ID);
        break;
      case ABOUT_US_FRAGMENT_ID:
        fragment = new AboutUsFragment();
        changeSidebarItem(-1);
        break;
      case ORDERS_LIST_FRAGMENT:
//        fragment = new OldOrdersListFragment();
        changeSidebarItem(ORDERS_LIST_FRAGMENT);
        break;
      case ORDER_DETAIL_FRAGMENT_ID:
        fragment = new OrderDetailFragment();
        changeSidebarItem(CUSTOMER_LIST_FRAGMENT_ID);
        break;
      case GOODS_LIST_FRAGMENT_ID:
        fragment = new OldGoodsListFragment();
        args.putBoolean("view_all", true);
        fragment.setArguments(args);
        changeSidebarItem(GOODS_LIST_FRAGMENT_ID);
        break;
      case SAVE_LOCATION_FRAGMENT_ID:
        fragment = new SaveLocationFragment();
        changeSidebarItem(CUSTOMER_LIST_FRAGMENT_ID);
        break;
      case PAYMENT_FRAGMENT_ID://18
//        fragment = new PaymentFragment();
        changeSidebarItem(CUSTOMER_LIST_FRAGMENT_ID);
        break;
      case PAYMENT_DETAIL_FRAGMENT_ID://19
//        fragment = new PaymentDetailFragment();
        changeSidebarItem(CUSTOMER_LIST_FRAGMENT_ID);
        break;
      case USER_TRACKING_FRAGMENT_ID://20
        fragment = new UserTrackingFragment();
        changeSidebarItem(-1);
        break;
      case KPI_CUSTOMER_FRAGMENT_ID: //21
        fragment = new KPIFragment();
        changeSidebarItem(CUSTOMER_LIST_FRAGMENT_ID);
        break;
      case KPI_SALESMAN_FRAGMENT_ID: //22
        fragment = new KPIFragment();
        changeSidebarItem(KPI_SALESMAN_FRAGMENT_ID);
        break;
      case FUNDS_FRAGMENT_ID: //23
//        fragment = new PaymentFragment();
        changeSidebarItem(FUNDS_FRAGMENT_ID);
        break;
      case QUESTIONAIRE_LIST_FRAGMENT_ID:
        fragment = new QuestionnairesListFragment();
        if (Empty.isNotEmpty(args)) {
          fragment.setArguments(args);
        }
        changeSidebarItem(args.getInt(Constants.PARENT, QUESTIONAIRE_LIST_FRAGMENT_ID));

    }
    Analytics.logContentView("Fragment " + String.valueOf(fragmentId));
    return fragment;
  }

  private boolean isDataTransferPossible() {
    boolean dataTransferPossible = dataTransferService.isDataTransferPossible();
    boolean networkAvailable = NetworkUtil.isNetworkAvailable(this);

    if (!dataTransferPossible) {
      ToastUtil.toastError(this, R.string.message_required_setting_for_data_transfer_not_found);
    }

    if (!networkAvailable) {
      ToastUtil.toastError(this, R.string.message_device_does_not_have_active_internet_connection);
    }

    return dataTransferPossible && networkAvailable;
  }

  private void setupDrawer() {
    mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
    DrawerArrayAdapter adapter = new DrawerArrayAdapter(OldMainActivity.this, drawerItemTitles,
        drawerItemImages);
    drawerItemsList = (ListView) findViewById(R.id.left_drawer);

    drawerItemsList.setAdapter(adapter);
    drawerItemsList.setOnItemClickListener((parent, view, position, id) ->
    {
      switch (position) {
        case 0:
          if (Empty.isNotEmpty(settingService.getSettingValue(ApplicationKeys.SETTING_USERNAME))
              && Empty.isNotEmpty(settingService.getSettingValue(ApplicationKeys.SETTING_PASSWORD))
              && !BuildConfig.DEBUG) {
            settingLoginDialog();
            closeDrawer();
          } else {
            changeFragment(SETTING_FRAGMENT_ID, false);
            closeDrawer();
          }
          break;
        case 1:
          changeFragment(DATA_TRANSFER_FRAGMENT_ID, false);
          closeDrawer();
          break;
        case 2:
          changeFragment(ABOUT_US_FRAGMENT_ID, false);
          closeDrawer();
          break;
        case 3:
//          showVersionDialog();
          break;
        case 4:
          showDialogForExit();
          break;
      }
    });

    menuIv.setOnClickListener(v ->
    {
      if (isMenuEnabled || BuildConfig.DEBUG) {
        if (mDrawerLayout.isDrawerOpen(Gravity.LEFT)) {
          closeDrawer();
        } else {
          openDrawer();
        }
      }
    });
  }

  private void settingLoginDialog() {
    DialogFragment loginDialog = LoginDialogFragment.newInstance();
    loginDialog.show(getSupportFragmentManager(), "login_dialog");
  }

  @Override
  public void onBackPressed() {
    try {
      FragmentManager supportFragmentManager = getSupportFragmentManager();

      if (supportFragmentManager.getBackStackEntryCount() > 1) {
        FragmentManager.BackStackEntry backStackEntryAt = supportFragmentManager
            .getBackStackEntryAt(supportFragmentManager.getBackStackEntryCount() - 2);
        String tag = backStackEntryAt.getName();
        BaseFragment lastFragment = (BaseFragment) supportFragmentManager.findFragmentByTag(tag);
        if (Empty.isNotEmpty(lastFragment)) {
          findFragment(lastFragment.getFragmentId(), new Bundle());
        }
        super.onBackPressed();
      } else {
        showDialogForExit();
      }
    } catch (Exception e) {
      Logger.sendError("UI Exception", "Error in backPressed " + e.getMessage());
      Log.e(TAG, e.getMessage(), e);
    }
  }

  protected void showDialogForExit() {
    DialogUtil.showConfirmDialog(this, getString(R.string.message_exit),
        getString(R.string.message_do_you_want_to_exit),
        (dialog, which) ->
        {
          dialog.dismiss();
          finish();
        });
  }

  public void openDrawer() {
    mDrawerLayout.openDrawer(Gravity.LEFT);
  }

  public void closeDrawer() {
    mDrawerLayout.closeDrawer(Gravity.LEFT);
  }

  public void removeFragment(Fragment fragment) {
    FragmentManager manager = getSupportFragmentManager();
    FragmentTransaction trans = manager.beginTransaction();
    trans.remove(fragment);
    trans.commit();
    manager.popBackStack();
  }

  public boolean isMenuEnabled() {
    return isMenuEnabled;
  }

  public void setMenuEnabled(boolean menuEnabled) {
    isMenuEnabled = menuEnabled;
  }

  @Override
  protected void onStart() {
    super.onStart();
    EventBus.getDefault().register(this);
    // Bind to the service. If the service is in foreground mode, this signals to the service
    // that since this activity is in the foreground, the service can exit foreground mode.
    bindService(new Intent(this, LocationUpdatesService.class), serviceConnection,
        Context.BIND_AUTO_CREATE);

    if (Updater.updateExist()) {
      PreferenceHelper.setForceExit(true);
      installNewVersion();
    } else {
      Updater.checkAppUpdate(this);
    }
  }

  @Override
  protected void onResume() {
    super.onResume();
    if (!GPSUtil.isGpsAvailable(this)) {
      showGpsOffDialog();
      Analytics.logCustom("GPS", new String[]{"GPS Status"}, "OFF");
    }

    registerReceiver(gpsStatusReceiver, new IntentFilter("android.location.PROVIDERS_CHANGED"));
    new TrackerAlarmReceiver().setAlarm(this);
  }

  @Override
  protected void onPause() {
    super.onPause();
    unregisterReceiver(gpsStatusReceiver);
  }

  @Override
  protected void onStop() {
    if (boundToGpsService) {
      // Unbind from the service. This signals to the service that this activity is no longer
      // in the foreground, and the service can respond by promoting itself to a foreground
      // service.
      unbindService(serviceConnection);
      boundToGpsService = false;
    }
    super.onStop();
    EventBus.getDefault().unregister(this);
  }

  @Subscribe
  public void getMessage(UpdateEvent event) {
    installNewVersion();
  }

  private void installNewVersion() {
    DialogUtil.showCustomDialog(this, getString(R.string.message_update_title),
        getString(R.string.message_update_alert), "", (dialogInterface, i) ->
        {
          dialogInterface.dismiss();
          DialogUtil.showCustomDialog(OldMainActivity.this, getString(R.string.warning),
              getString(R.string.message_alert_send_data),
              "",
              (dialog, i1) ->
              {
                dialog.dismiss();
                showProgressDialog(getString(R.string.message_sending_data));
                new Thread(() ->
                {
                  try {
                    dataTransferService.sendAllData(OldMainActivity.this);
                  } catch (Exception ex) {
                    Logger.sendError("Install Update",
                        "Error in installing new version" + ex.getMessage());
                    ex.printStackTrace();
                    ToastUtil
                        .toastError(OldMainActivity.this, R.string.error_unknown_system_exception);
                  }
                }).start();
              },
              "",
              (dialogInterface1, i12) -> doInstall(), Constants.ICON_WARNING);
        }, "", (dialogInterface, i) ->
        {
          dialogInterface.dismiss();
          if (PreferenceHelper.isForceExit()) {
            finish();
          }
        }, Constants.ICON_MESSAGE);
  }

  private void showGpsOffDialog() {
    Dialog dialog = new AlertDialog.Builder(this)
        .setTitle(getString(R.string.error_gps_is_disabled))

        .setNegativeButton(getString(R.string.no), (dialog1, which) -> finish())
        .setPositiveButton(getString(R.string.yes), (dialog12, which) ->
        {
          Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
          startActivity(intent);
        })
        .create();
    dialog.show();
  }

  @Override
  public void publishResult(BusinessException ex) {
  }

  @Override
  public void publishResult(String message) {
    changeMessageDialog(message);
  }

  @Override
  public void finished(boolean success) {
    dismissProgressDialog();
    if (success) {
      doInstall();
    }
  }

  private void doInstall() {
    Intent installIntent = new Intent(Intent.ACTION_VIEW);
    installIntent.setDataAndType(Uri.parse(PreferenceHelper.getUpdateUri()),
        "application/vnd.android.package-archive");
    installIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    try {
      startActivity(installIntent);
      if (PreferenceHelper.isForceExit()) {
        finish();
      }
    } catch (Exception ex) {
      Logger.sendError("Install Update", "Error in installing update" + ex.getMessage());
      ToastUtil.toastError(OldMainActivity.this, R.string.err_update_failed);
    }
  }

  private void requestPermissions() {
    boolean shouldProvideRationale =
        ActivityCompat.shouldShowRequestPermissionRationale(this,
            Manifest.permission.ACCESS_FINE_LOCATION);

    // Provide an additional rationale to the user. This would happen if the user denied the
    // request previously, but didn't check the "Don't ask again" checkbox.
    if (shouldProvideRationale) {
      Log.i(TAG, "Displaying permission rationale to provide additional context.");
      ToastUtil.toastError(this, getString(R.string.permission_rationale),
          view -> {
            // Request permission
            ActivityCompat.requestPermissions(OldMainActivity.this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                REQUEST_PERMISSIONS_REQUEST_CODE);
          });
    } else {
      Log.i(TAG, "Requesting permission");
      // Request permission. It's possible this can be auto answered if device policy
      // sets the permission in a given state or the user denied the permission
      // previously and checked "Never ask again".
      ActivityCompat.requestPermissions(OldMainActivity.this,
          new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
          REQUEST_PERMISSIONS_REQUEST_CODE);
    }
  }

  /**
   * Returns the current state of the permissions needed.
   */
  private boolean checkPermissions() {
    return PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(this,
        Manifest.permission.ACCESS_FINE_LOCATION);
  }

  /**
   * Callback received when a permissions request has been completed.
   */
  @Override
  public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
      @NonNull int[] grantResults) {
    Log.i(TAG, "onRequestPermissionResult");
    if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
      if (grantResults.length <= 0) {
        // If user interaction was interrupted, the permission request is cancelled and you
        // receive empty arrays.
        Log.i(TAG, "User interaction was cancelled.");
      } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
        // Permission was granted.
        gpsRecieverService.requestLocationUpdates();
      } else {
        ToastUtil.toastError(this, getString(R.string.permission_denied_explanation),
            view -> {
              // Build intent that displays the App settings screen.
              Intent intent = new Intent();
              intent.setAction(
                  Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
              Uri uri = Uri.fromParts("package", BuildConfig.APPLICATION_ID, null);
              intent.setData(uri);
              intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
              startActivity(intent);
            });
      }
    }
  }
}

