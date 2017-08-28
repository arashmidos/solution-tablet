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
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.parsroyal.solutiontablet.BuildConfig;
import com.parsroyal.solutiontablet.R;
import com.parsroyal.solutiontablet.constants.Constants;
import com.parsroyal.solutiontablet.constants.StatusCodes;
import com.parsroyal.solutiontablet.data.entity.KeyValue;
import com.parsroyal.solutiontablet.data.event.DataTransferEvent;
import com.parsroyal.solutiontablet.data.event.ErrorEvent;
import com.parsroyal.solutiontablet.data.event.Event;
import com.parsroyal.solutiontablet.data.event.UpdateEvent;
import com.parsroyal.solutiontablet.receiver.TrackerAlarmReceiver;
import com.parsroyal.solutiontablet.service.DataTransferService;
import com.parsroyal.solutiontablet.service.LocationUpdatesService;
import com.parsroyal.solutiontablet.service.impl.DataTransferServiceImpl;
import com.parsroyal.solutiontablet.ui.fragment.AboutUsFragment;
import com.parsroyal.solutiontablet.ui.fragment.BaseFragment;
import com.parsroyal.solutiontablet.ui.fragment.DataTransferFragment;
import com.parsroyal.solutiontablet.ui.fragment.FeaturesFragment;
import com.parsroyal.solutiontablet.ui.fragment.NewOrderInfoFragment;
import com.parsroyal.solutiontablet.ui.fragment.OrderFragment;
import com.parsroyal.solutiontablet.ui.fragment.PathDetailFragment;
import com.parsroyal.solutiontablet.ui.fragment.SettingFragment;
import com.parsroyal.solutiontablet.ui.fragment.VisitLinesListFragment;
import com.parsroyal.solutiontablet.ui.fragment.dialog.NewVisitDetailFragment;
import com.parsroyal.solutiontablet.util.Analytics;
import com.parsroyal.solutiontablet.util.DialogUtil;
import com.parsroyal.solutiontablet.util.Empty;
import com.parsroyal.solutiontablet.util.GPSUtil;
import com.parsroyal.solutiontablet.util.NetworkUtil;
import com.parsroyal.solutiontablet.util.PreferenceHelper;
import com.parsroyal.solutiontablet.util.ToastUtil;
import com.parsroyal.solutiontablet.util.Updater;
import com.parsroyal.solutiontablet.util.constants.ApplicationKeys;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends BaseFragmentActivity {

  public static final String TAG = MainActivity.class.getSimpleName();
  public static final int FEATURE_FRAGMENT_ID = 0;
  public static final int CUSTOMER_LIST_FRAGMENT_ID = 1;
  public static final int VISIT_DETAIL_FRAGMENT_ID = 2;
  public static final int CUSTOMER_ORDER_FRAGMENT_ID = 3;
  public static final int ORDER_FRAGMENT_ID = 4;
  public static final int ORDER_INFO_FRAGMENT = 5;
  public static final int SETTING_FRAGMENT_ID = 10;
  public static final int DATA_TRANSFER_FRAGMENT_ID = 11;
  public static final int ABOUT_US_FRAGMENT_ID = 13;
  public static final int GOODS_LIST_FRAGMENT_ID = 16;
  public static final int PATH_FRAGMENT_ID = 27;
  public static final int PATH_DETAIL_FRAGMENT_ID = 28;
  private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;
  @BindView(R.id.toolbar)
  Toolbar toolbar;
  @BindView(R.id.drawer_layout)
  DrawerLayout drawerLayout;
  @BindView(R.id.toolbar_title)
  TextView toolbarTitle;
  @BindView(R.id.navigation_img)
  ImageView navigationImg;
  private ActionBar actionBar;
  private LocationUpdatesService gpsRecieverService = null;
  private DataTransferService dataTransferService;

  private boolean boundToGpsService = false;
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
    setContentView(R.layout.activity_main);
    ButterKnife.bind(this);

    dataTransferService = new DataTransferServiceImpl(this);

    if (!BuildConfig.DEBUG) {
      logUser();
    }

    if (!checkPermissions()) {
      requestPermissions();
    }
    showFeaturesFragment();
  }


  public void setNavigationToolbarIcon(int id) {
    navigationImg.setImageResource(id);
  }

  private void showVersionDialog() {
    //TODO OLd code, need new style
    DialogUtil.showMessageDialog(this, getString(R.string.version),
        String.format(Locale.US, getString(R.string.your_version), BuildConfig.VERSION_NAME));
  }

  @Subscribe
  public void getMessage(Event event) {
    if (event instanceof UpdateEvent) {
      installNewVersion();
    } else if (event instanceof ErrorEvent) {
      if (event.getStatusCode() == StatusCodes.PERMISSION_DENIED) {
        requestPermissions();
      }
    } else if (event instanceof DataTransferEvent) {
      ToastUtil.toastSuccess(this, R.string.message_setting_saved_successfully);
      startGpsService();
      new TrackerAlarmReceiver().setAlarm(this);
    }

    //TODO: If instance of data transfer completed
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

  protected void showDialogForExit() {
    DialogUtil.showConfirmDialog(this, getString(R.string.message_exit),
        getString(R.string.message_do_you_want_to_exit),
        (dialog, which) ->
        {
          dialog.dismiss();
          finish();
        });
  }

  private void installNewVersion() {
    //TODO: Old code, update new style
    DialogUtil.showCustomDialog(this, getString(R.string.message_update_title),
        getString(R.string.message_update_alert), "", (dialogInterface, i) ->
        {
          dialogInterface.dismiss();
          DialogUtil.showCustomDialog(MainActivity.this, getString(R.string.warning),
              getString(R.string.message_alert_send_data),
              "",
              (dialog, i1) ->
              {
                dialog.dismiss();
                showProgressDialog(getString(R.string.message_sending_data));
                new Thread(() ->
                {
                  try {//TODO Send Event bus instead of publisher
                    dataTransferService.sendAllData(null);
                  } catch (Exception ex) {
                    Crashlytics.log(Log.ERROR, "Install Update",
                        "Error in installing new version" + ex.getMessage());
                    ex.printStackTrace();
                    ToastUtil
                        .toastError(MainActivity.this, R.string.error_unknown_system_exception);
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
      Crashlytics.log(Log.ERROR, "Install Update", "Error in installing update" + ex.getMessage());
      ToastUtil.toastError(MainActivity.this, R.string.err_update_failed);
    }
  }

  public void startGpsService() {
    gpsRecieverService.requestLocationUpdates();
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

  private void showGpsOffDialog() {
    //TODO: Old code, need new style
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

  private void logUser() {
    Crashlytics.setUserName(userFullNameTxt.getText().toString());
    Crashlytics.setString("Company", companyNameTxt.getText().toString());

    KeyValue saleType = PreferenceHelper.retrieveByKey(ApplicationKeys.SETTING_SALE_TYPE);
    Crashlytics.setString("Sale Type", saleType == null ? "" : saleType.getValue());
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
            ActivityCompat.requestPermissions(MainActivity.this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                REQUEST_PERMISSIONS_REQUEST_CODE);
          });
    } else {
      Log.i(TAG, "Requesting permission");
      // Request permission. It's possible this can be auto answered if device policy
      // sets the permission in a given state or the user denied the permission
      // previously and checked "Never ask again".
      ActivityCompat.requestPermissions(MainActivity.this,
          new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
          REQUEST_PERMISSIONS_REQUEST_CODE);
    }
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

  /**
   * Returns the current state of the permissions needed.
   */
  private boolean checkPermissions() {
    return PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(this,
        Manifest.permission.ACCESS_FINE_LOCATION);
  }

  public void showFeaturesFragment() {
    changeFragment(MainActivity.FEATURE_FRAGMENT_ID, true);
  }

  public void showCustomersListFragment() {//TODO: change to Path page
    changeFragment(MainActivity.CUSTOMER_LIST_FRAGMENT_ID, true);
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
      Crashlytics.log(Log.ERROR, "UI Exception", "Error in backPressed " + e.getMessage());
      Log.e(TAG, e.getMessage(), e);
    }
  }

  public void removeFragment(Fragment fragment) {
    FragmentManager manager = getSupportFragmentManager();
    FragmentTransaction trans = manager.beginTransaction();
    trans.remove(fragment);
    trans.commit();
    manager.popBackStack();
  }

  //TODO: Need review addTobackStack is useless
  private void commitFragment(String fragmentTag, BaseFragment fragment, boolean addToBackStack) {
    FragmentTransaction fragmentTransaction;
    FragmentManager fragmentManager = getSupportFragmentManager();
    fragmentTransaction = fragmentManager.beginTransaction();
    fragmentTransaction.replace(R.id.container, fragment, fragmentTag);
    fragmentTransaction.addToBackStack(fragmentTag);
    fragmentTransaction.commit();
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

  private BaseFragment findFragment(int fragmentId, Bundle args) {
    BaseFragment fragment = null;
    int parent = 0;
    if (fragmentId == FEATURE_FRAGMENT_ID)
      setNavigationToolbarIcon(R.drawable.ic_menu);
    else
      setNavigationToolbarIcon(R.drawable.ic_arrow_forward);

    switch (fragmentId) {
      case FEATURE_FRAGMENT_ID:
        fragment = FeaturesFragment.newInstance();
        break;
      case CUSTOMER_LIST_FRAGMENT_ID://TODO it should point to Path Page
        fragment = PathDetailFragment.newInstance();
        break;
      case VISIT_DETAIL_FRAGMENT_ID:
        fragment = NewVisitDetailFragment.newInstance();
        break;
      case ORDER_INFO_FRAGMENT:
        fragment = NewOrderInfoFragment.newInstance();
        break;
      /*case NEW_CUSTOMER_FRAGMENT_ID:
        fragment = new NCustomersFragment();
        break;
      case NEW_CUSTOMER_DETAIL_FRAGMENT_ID:
        fragment = new NCustomerDetailFragment();
        break;
      case CUSTOMERS_FRAGMENT_ID:
        fragment = new CustomersFragment();
        break;
      case CUSTOMER_DETAIL_FRAGMENT_ID:
        fragment = new CustomerDetailFragment();
        break;
      case VISIT_DETAIL_FRAGMENT_ID:
        fragment = new VisitDetailFragment();
//        setMenuEnabled(false);
        break;
      case GENERAL_QUESTIONNAIRES_FRAGMENT_ID://6
        fragment = new GeneralQuestionnairesFragment();
        parent = args.getInt(Constants.PARENT, 0);
        break;
      case QUESTIONNAIRE_DETAIL_FRAGMENT_ID:
        fragment = new QuestionnaireDetailFragment();
        parent = args.getInt(Constants.PARENT);
        break;
      case GOODS_QUESTIONNAIRES_FRAGMENT_ID://8
        fragment = new GoodsQuestionnairesFragment();
        break;
      case GOODS_LIST_FOR_QUESTIONNAIRES_FRAGMENT_ID:
        fragment = new GoodsListForQuestionnairesFragment();
        break;*/
      case SETTING_FRAGMENT_ID:
        fragment = new SettingFragment();
        break;
      case DATA_TRANSFER_FRAGMENT_ID:
        if (isDataTransferPossible()) {
          fragment = new DataTransferFragment();
        }
        break;
      /*case DASHBOARD_FRAGMENT_ID:
        fragment = new UserTrackingFragment();
        break;*/
      case ABOUT_US_FRAGMENT_ID:
        fragment = new AboutUsFragment();
        break;
      /*case ORDERS_LIST_FRAGMENT:
        fragment = new OrdersListFragment();
        break;
      case ORDER_DETAIL_FRAGMENT_ID:
        fragment = new OrderDetailFragment();
        break;*/
      case GOODS_LIST_FRAGMENT_ID:
        fragment = OrderFragment.newInstance();
        args.putBoolean(Constants.READ_ONLY, true);
        fragment.setArguments(args);
        break;
      /*case SAVE_LOCATION_FRAGMENT_ID:
        fragment = new SaveLocationFragment();
        break;
      case PAYMENT_FRAGMENT_ID://18
        fragment = new PaymentFragment();
        break;
      case PAYMENT_DETAIL_FRAGMENT_ID://19
        fragment = new PaymentDetailFragment();
        break;
      case USER_TRACKING_FRAGMENT_ID://20
        fragment = new UserTrackingFragment();
        break;
      case KPI_CUSTOMER_FRAGMENT_ID: //21
        fragment = new KPIFragment();
        break;
      case KPI_SALESMAN_FRAGMENT_ID: //22
        fragment = new KPIFragment();
        break;
      case FUNDS_FRAGMENT_ID: //23
        fragment = new PaymentFragment();
        break;
      case QUESTIONAIRE_LIST_FRAGMENT_ID:
        fragment = new QuestionnairesListFragment();
        if (Empty.isNotEmpty(args)) {
          fragment.setArguments(args);
        }break;*/
      case PATH_FRAGMENT_ID:
        fragment = VisitLinesListFragment.newInstance();
        break;
      case PATH_DETAIL_FRAGMENT_ID:
        fragment = PathDetailFragment.newInstance();
        break;
    }
    Analytics.logContentView("Fragment " + String.valueOf(fragmentId));
    return fragment;
  }

  public void changeTitle(String title) {
    toolbarTitle.setText(title);
  }

  @OnClick(R.id.navigation_img) public void onClick() {
    Fragment featureFragment = getSupportFragmentManager().findFragmentByTag(FeaturesFragment.class.getSimpleName());
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
