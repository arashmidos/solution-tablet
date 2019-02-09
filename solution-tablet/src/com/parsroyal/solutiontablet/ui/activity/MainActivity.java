package com.parsroyal.solutiontablet.ui.activity;

import android.Manifest.permission;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentManager.BackStackEntry;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.OnClick;
import co.ronash.pushe.Pushe;
import com.crashlytics.android.Crashlytics;
import com.parsroyal.solutiontablet.BuildConfig;
import com.parsroyal.solutiontablet.R;
import com.parsroyal.solutiontablet.SolutionTabletApplication;
import com.parsroyal.solutiontablet.biz.impl.RestServiceImpl;
import com.parsroyal.solutiontablet.constants.Constants;
import com.parsroyal.solutiontablet.constants.StatusCodes;
import com.parsroyal.solutiontablet.data.entity.KeyValue;
import com.parsroyal.solutiontablet.data.event.ErrorEvent;
import com.parsroyal.solutiontablet.data.event.Event;
import com.parsroyal.solutiontablet.data.event.UpdateEvent;
import com.parsroyal.solutiontablet.receiver.TrackerAlarmReceiver;
import com.parsroyal.solutiontablet.service.DataTransferService;
import com.parsroyal.solutiontablet.service.LocationUpdatesService;
import com.parsroyal.solutiontablet.service.LocationUpdatesService.LocalBinder;
import com.parsroyal.solutiontablet.service.PositionService;
import com.parsroyal.solutiontablet.service.SettingService;
import com.parsroyal.solutiontablet.service.impl.DataTransferServiceImpl;
import com.parsroyal.solutiontablet.service.impl.PositionServiceImpl;
import com.parsroyal.solutiontablet.service.impl.PositionServiceImpl.GpsStatus;
import com.parsroyal.solutiontablet.service.impl.SettingServiceImpl;
import com.parsroyal.solutiontablet.ui.fragment.AboutUsFragment;
import com.parsroyal.solutiontablet.ui.fragment.AddCustomerFragment;
import com.parsroyal.solutiontablet.ui.fragment.AdminFragment;
import com.parsroyal.solutiontablet.ui.fragment.AnonymousQuestionnaireFragment;
import com.parsroyal.solutiontablet.ui.fragment.BaseFragment;
import com.parsroyal.solutiontablet.ui.fragment.ChatFragment;
import com.parsroyal.solutiontablet.ui.fragment.CustomerFragment;
import com.parsroyal.solutiontablet.ui.fragment.CustomerSearchFragment;
import com.parsroyal.solutiontablet.ui.fragment.FeaturesFragment;
import com.parsroyal.solutiontablet.ui.fragment.OrderFragment;
import com.parsroyal.solutiontablet.ui.fragment.OrderInfoFragment;
import com.parsroyal.solutiontablet.ui.fragment.PhoneVisitDetailFragment;
import com.parsroyal.solutiontablet.ui.fragment.QuestionnaireListFragment;
import com.parsroyal.solutiontablet.ui.fragment.QuestionnairesCategoryFragment;
import com.parsroyal.solutiontablet.ui.fragment.QuestionsListFragment;
import com.parsroyal.solutiontablet.ui.fragment.RegisterPaymentFragment;
import com.parsroyal.solutiontablet.ui.fragment.ReportFragment;
import com.parsroyal.solutiontablet.ui.fragment.SaveLocationFragment;
import com.parsroyal.solutiontablet.ui.fragment.SettingFragment;
import com.parsroyal.solutiontablet.ui.fragment.UserTrackingFragment;
import com.parsroyal.solutiontablet.ui.fragment.VisitDetailFragment;
import com.parsroyal.solutiontablet.ui.fragment.VisitLineDetailFragment;
import com.parsroyal.solutiontablet.ui.fragment.VisitLineFragment;
import com.parsroyal.solutiontablet.ui.fragment.dialogFragment.CustomerSearchDialogFragment;
import com.parsroyal.solutiontablet.ui.fragment.dialogFragment.DataTransferDialogFragment;
import com.parsroyal.solutiontablet.util.Analytics;
import com.parsroyal.solutiontablet.util.DialogUtil;
import com.parsroyal.solutiontablet.util.Empty;
import com.parsroyal.solutiontablet.util.GPSUtil;
import com.parsroyal.solutiontablet.util.Logger;
import com.parsroyal.solutiontablet.util.NumberUtil;
import com.parsroyal.solutiontablet.util.PreferenceHelper;
import com.parsroyal.solutiontablet.util.ToastUtil;
import com.parsroyal.solutiontablet.util.Updater;
import com.parsroyal.solutiontablet.util.constants.ApplicationKeys;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;
import java.util.List;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import timber.log.Timber;

/**
 * Created by Arash 2017-09-16
 */
public abstract class MainActivity extends AppCompatActivity {

  public static final int FEATURE_FRAGMENT_ID = 0;
  public static final int PHONE_VISIT_DETAIL_FRAGMENT_ID = 1;
  public static final int NEW_CUSTOMER_DETAIL_FRAGMENT_ID = 2;
  public static final int VISIT_LIST_FRAGMENT_ID = 3;
  public static final int ADMIN_FRAGMENT_ID = 4;
  public static final int VISIT_DETAIL_FRAGMENT_ID = 5;
  public static final int REGISTER_PAYMENT_FRAGMENT = 6;
  public static final int CUSTOMER_FRAGMENT = 7;
  public static final int ORDER_INFO_FRAGMENT = 8;
  public static final int CUSTOMER_SEARCH_FRAGMENT = 9;
  public static final int DELIVERY_FRAGMENT_ID = 10;
  public static final int USER_TRACKING_FRAGMENT_ID = 12;
  public static final int ABOUT_US_FRAGMENT_ID = 13;
  public static final int REPORT_FRAGMENT = 14;
  public static final int GOODS_LIST_FRAGMENT_ID = 16;
  public static final int SAVE_LOCATION_FRAGMENT_ID = 17;
  public static final int QUESTIONNAIRE_CATEGORY_FRAGMENT_ID = 18;
  public static final int QUESTIONNAIRE_LIST_FRAGMENT_ID = 19;
  public static final int QUESTION_LIST_FRAGMENT_ID = 20;
  public static final int ALL_QUESTIONNAIRE_FRAGMENT_ID = 21;
  public static final int ANONYMOUS_QUESTIONNAIRE_FRAGMENT_ID = 22;
  public static final int VISITLINE_FRAGMENT_ID = 27;
  public static final int VISITLINE_DETAIL_FRAGMENT_ID = 28;
  public static final int SYSTEM_CUSTOMER_FRAGMENT = 29;
  public static final int NEW_CUSTOMER_FRAGMENT_ID = 30;
  public static final int NAVIGATION_DRAWER_FRAGMENT = 31;
  public static final int CUSTOMER_INFO_FRAGMENT = 32;
  public static final int SETTING_FRAGMENT = 33;
  public static final int CHAT_FRAGMENT = 37;

  private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;
  private static final int REQUEST_PERMISSIONS_REQUEST_CODE_CAMERA_STORAGE = 35;
  private static final int MY_PERMISSIONS_REQUEST_SEND_SMS = 56;

  private static final String TAG = MainActivity.class.getName();
  public static int batteryLevel = -1;
  public static String batteryStatusTitle;
  protected ProgressDialog progressDialog;
  protected BaseFragment currentFragment;
  protected LocationUpdatesService gpsRecieverService = null;
  protected DataTransferService dataTransferService;
  protected boolean boundToGpsService = false;
  protected final ServiceConnection serviceConnection = new ServiceConnection() {

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
      LocalBinder binder = (LocalBinder) service;
      gpsRecieverService = binder.getService();
      boundToGpsService = true;
      if (!checkPermissions()) {
        requestPermissions();
      } else {
        gpsRecieverService.requestLocationUpdates();
      }
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
      gpsRecieverService = null;
      boundToGpsService = false;
    }
  };
  @BindView(R.id.toolbar)
  Toolbar toolbar;
  @BindView(R.id.toolbar_title)
  TextView toolbarTitle;
  @BindView(R.id.search_img)
  ImageView searchImg;
  @BindView(R.id.filter_img)
  ImageView filterImg;
  @BindView(R.id.save_img)
  ImageView saveImg;
  @BindView(R.id.notif_img)
  ImageView notifImg;
  @BindView(R.id.container)
  FrameLayout container;
  @BindView(R.id.navigation_img)
  ImageView navigationImg;
  @Nullable
  @BindView(R.id.detail_tv)
  TextView detailTv;
  @BindView(R.id.chronometer)
  TextView chronometer;

  private BroadcastReceiver batteryInfoReceiver = new BroadcastReceiver() {
    @Override
    public void onReceive(Context ctxt, Intent intent) {
      batteryLevel = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
      int deviceStatus = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);

      if (deviceStatus == BatteryManager.BATTERY_STATUS_CHARGING) {
        batteryStatusTitle = "Charging";
      }

      if (deviceStatus == BatteryManager.BATTERY_STATUS_DISCHARGING) {
        batteryStatusTitle = "Discharging";
      }

      if (deviceStatus == BatteryManager.BATTERY_STATUS_FULL) {
        batteryStatusTitle = "Battery Full";
      }

      if (deviceStatus == BatteryManager.BATTERY_STATUS_UNKNOWN) {
        batteryStatusTitle = "Unknown";
      }

      if (deviceStatus == BatteryManager.BATTERY_STATUS_NOT_CHARGING) {
        batteryStatusTitle = "Not Charging";
      }
    }
  };
  private PositionService positionService;
  protected BroadcastReceiver gpsStatusReceiver = new BroadcastReceiver() {
    @Override
    public void onReceive(Context context, Intent intent) {
      if (intent.getAction() != null && intent.getAction()
          .matches("android.location.PROVIDERS_CHANGED")) {
        if (!GPSUtil.isGpsAvailable(context)) {
          showGpsOffDialog();
          positionService.sendGpsChangedPosition(GpsStatus.OFF);
        } else {
          positionService.sendGpsChangedPosition(GpsStatus.ON);
        }
      }
    }
  };
  private boolean phoneVisit;

  public void onCreate(Bundle savedInstanceState) {
   /* StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
        .detectAll()
        .penaltyLog()
        .penaltyDeath()
        .build());*/
    super.onCreate(savedInstanceState);

    Pushe.initialize(this, true);

    dataTransferService = new DataTransferServiceImpl(this);
    positionService = new PositionServiceImpl(this);

    if (!BuildConfig.DEBUG) {
      logUser();
    }
    loadPermission();
//    updatePushe();//TODO:LATER
  }

  private void updatePushe() {
    try {
      Pushe.initialize(this, true);
      Timber.tag("Pushe").d(Pushe.getPusheId(this));
      KeyValue username = PreferenceHelper.retrieveByKey(ApplicationKeys.SETTING_USERNAME);
      if (Empty.isNotEmpty(username)) {
        new RestServiceImpl().updatePusheId(this, Pushe.getPusheId(this), "GCMToken");
      }

    } catch (Exception ignore) {

    }
  }

  private void loadPermission() {
    SolutionTabletApplication.getInstance().loadAuthorities();
  }


  @Subscribe
  public void getMessage(Event event) {
    if (event instanceof UpdateEvent) {
      installNewVersion();
    } else if (event instanceof ErrorEvent) {
      if (event.getStatusCode() == StatusCodes.PERMISSION_DENIED) {
        requestPermissions();
      }
    }
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
      Thread t = new Thread(() -> Updater.checkAppUpdate(MainActivity.this));
      t.start();
    }
  }

  public void dismissProgressDialog() {
    if (this.progressDialog != null) {
      this.progressDialog.dismiss();
    }
  }

  protected void showDialogForExit() {
    DialogUtil.showConfirmDialog(this, getString(R.string.message_exit),
        getString(R.string.message_do_you_want_to_exit),
        (dialog, which) ->
        {
          dialog.dismiss();
          Intent intent = new Intent(this, LocationUpdatesService.class);
          stopService(intent);
          finish();
        });
  }

  private void installNewVersion() {
    DialogUtil.showCustomDialog(this, getString(R.string.message_update_title),
        getString(R.string.message_update_alert), "", (dialogInterface, i) -> {
          dialogInterface.dismiss();
          if (dataTransferService.hasUnsentData()) {
            DialogUtil.showCustomDialog(this, getString(R.string.warning),
                "شما اطلاعات ارسال نشده دارید که قبل از نصب نسخه جدید می بایست ارسال شوند. آیا میخواهید آنها را ارسال کنید؟",
                getString(R.string.yes),
                (dialog, which) -> openDataTransferDialog(),
                getString(R.string.no),
                (dialog, which) -> {
                  dialog.dismiss();/* openDataTransferDialog(Constants.DATA_TRANSFER_GET)*/
                },
                Constants.ICON_WARNING);
          } else {
            doInstall();
          }

        }, "", (dialogInterface, i) -> {
          dialogInterface.dismiss();
          if (PreferenceHelper.isForceExit()) {
            finish();
          }
        }, Constants.ICON_MESSAGE);
  }

  private void openDataTransferDialog() {
    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
    Bundle args = new Bundle();
    args.putString(Constants.DATA_TRANSFER_ACTION, Constants.DATA_TRANSFER_SEND_DATA);
    DataTransferDialogFragment dialogFragment = DataTransferDialogFragment.newInstance(args);

    dialogFragment.show(ft, "data_transfer");
  }

  private void doInstall() {
    Intent installIntent = new Intent(Intent.ACTION_VIEW);
    installIntent.setDataAndType(Uri.parse(PreferenceHelper.getUpdateUri()),
        "application/vnd.android.package-archive");
    installIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_GRANT_READ_URI_PERMISSION);
    try {
      startActivity(installIntent);
      if (PreferenceHelper.isForceExit()) {
        finish();
      }
    } catch (Exception ex) {
      Logger.sendError("Install Update", "Error in installing update" + ex.getMessage());
      ToastUtil.toastError(MainActivity.this, R.string.err_update_failed);
    }
  }

  public void startGpsService() {
    gpsRecieverService.requestLocationUpdates();
  }

  @Override
  protected void onResume() {
    super.onResume();
    if (!checkPermissions()) {
      requestPermissions();
    }

    if (!GPSUtil.isGpsAvailable(this)) {
      showGpsOffDialog();
      positionService.sendGpsChangedPosition(GpsStatus.OFF);
      Analytics.logCustom("GPS", new String[]{"GPS Status"}, "OFF");
    }

    registerReceiver(gpsStatusReceiver, new IntentFilter("android.location.PROVIDERS_CHANGED"));
    registerReceiver(batteryInfoReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));

    new TrackerAlarmReceiver().setAlarm(this);
    navigationImg.setVisibility(View.VISIBLE);

    /*List<String> fakeApps = GPSUtil.getListOfFakeLocationApps(this);
    if (fakeApps.size() > 0 && !BuildConfig.DEBUG) {
      showFakeGpsDetected(fakeApps);
    }*///TODOO: Time consuming, do it in async
    SolutionTabletApplication.getInstance().reSyncTrueTime();
  }

  private void showFakeGpsDetected(List<String> fakeApps) {

    StringBuilder s = new StringBuilder("");
    for (int i = 0; i < fakeApps.size(); i++) {
      s.append("-").append(fakeApps.get(i)).append("\n");
    }
    String message = String.format(getString(R.string.error_fake_gps_detected), s.toString());

    DialogUtil.showCustomDialog(this, getString(R.string.warning),
        message, "تایید", (dialog, which) -> finish(), null, null, Constants.ICON_MESSAGE);
  }

  @Override
  protected void onPause() {
    super.onPause();
    try {
      unregisterReceiver(gpsStatusReceiver);
      unregisterReceiver(batteryInfoReceiver);
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  @Override
  protected void onStop() {
    super.onStop();

    if (boundToGpsService) {
      // Unbind from the service. This signals to the service that this activity is no longer
      // in the foreground, and the service can respond by promoting itself to a foreground
      // service.
      unbindService(serviceConnection);
      boundToGpsService = false;
    }

    EventBus.getDefault().unregister(this);
  }

  /**
   * Callback received when a permissions request has been completed.
   */
  @Override
  public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
      @NonNull int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    Timber.i("onRequestPermissionResult");
    if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
      if (grantResults.length <= 0) {
        // If user interaction was interrupted, the permission request is cancelled and you
        // receive empty arrays.
        Timber.i("User interaction was cancelled.");
      } else if ((grantResults.length > 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED
          && grantResults[1] == PackageManager.PERMISSION_GRANTED) || (grantResults.length == 1
          && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
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
    } else if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE_CAMERA_STORAGE) {
      if (grantResults.length <= 0) {
        // If user interaction was interrupted, the permission request is cancelled and you
        // receive empty arrays.
        Timber.i("User interaction was cancelled.");

      } else if ((grantResults.length > 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED
          && grantResults[1] == PackageManager.PERMISSION_GRANTED) || (grantResults.length == 1
          && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
        // Permission was granted.
        Fragment visitDetailFragment = getSupportFragmentManager()
            .findFragmentByTag(VisitDetailFragment.class.getSimpleName());
        Fragment addCustomerFragment = getSupportFragmentManager()
            .findFragmentByTag(AddCustomerFragment.class.getSimpleName());
        if (visitDetailFragment != null && visitDetailFragment.isVisible()) {
          ((VisitDetailFragment) visitDetailFragment).startCameraActivity();
        } else if (addCustomerFragment != null && addCustomerFragment.isVisible()) {
          ((AddCustomerFragment) addCustomerFragment).startCameraActivity();
        }
      } else {
        ToastUtil.toastError(this, getString(R.string.permission_rationale_camera_storage),
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

  public void hideKeyboard() {
    InputMethodManager imm = (InputMethodManager) this
        .getSystemService(Context.INPUT_METHOD_SERVICE);
    if (imm != null) {
      imm.hideSoftInputFromWindow(container.getWindowToken(), 0);
    }
  }

  private void showGpsOffDialog() {
    DialogUtil.showCustomDialog(this, getString(R.string.warning),
        getString(R.string.error_gps_is_disabled), "", (dialog, which) -> {
          Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
          startActivity(intent);
        }, "", (dialog, which) -> finish(), Constants.ICON_MESSAGE);
  }

  private void logUser() {
    SettingService settingService = new SettingServiceImpl();
    Crashlytics.setUserName(settingService.getSettingValue(ApplicationKeys.USER_FULL_NAME));
    Crashlytics
        .setString("Company", settingService.getSettingValue(ApplicationKeys.USER_COMPANY_NAME));

    KeyValue saleType = PreferenceHelper.retrieveByKey(ApplicationKeys.SETTING_SALE_TYPE);
    Crashlytics.setString("Sale Type", saleType == null ? "" : saleType.getValue());
    Crashlytics
        .setString("Username", settingService.getSettingValue(ApplicationKeys.SETTING_USERNAME));
  }

  private void requestPermissions() {
    boolean shouldProvideRationale =
        ActivityCompat.shouldShowRequestPermissionRationale(this,
            permission.ACCESS_FINE_LOCATION);
    boolean storageShouldProvideRationale =
        ActivityCompat.shouldShowRequestPermissionRationale(this,
            permission.WRITE_EXTERNAL_STORAGE);
    boolean stateShouldProvideRationale =
        ActivityCompat.shouldShowRequestPermissionRationale(this,
            permission.READ_PHONE_STATE);

    // Provide an additional rationale to the user. This would happen if the user denied the
    // request previously, but didn't check the "Don't ask again" checkbox.
    if (shouldProvideRationale && storageShouldProvideRationale && stateShouldProvideRationale) {
      Timber.i("Displaying permission rationale to provide additional context.");
      try {
        ToastUtil.toastError(this, getString(R.string.permission_rationale_storage_location),
            view -> {
              // Request permission
              ActivityCompat.requestPermissions(MainActivity.this,
                  new String[]{permission.ACCESS_FINE_LOCATION, permission.WRITE_EXTERNAL_STORAGE,
                      permission.READ_PHONE_STATE}, REQUEST_PERMISSIONS_REQUEST_CODE);

            });
      } catch (Exception e) {
        e.printStackTrace();
      }
    } else if (shouldProvideRationale) {
      Timber.i("Displaying permission rationale to provide additional context.");
      ToastUtil.toastError(this, getString(R.string.permission_rationale_location),
          view -> {
            // Request permission
            ActivityCompat.requestPermissions(this,
                new String[]{permission.ACCESS_FINE_LOCATION},
                REQUEST_PERMISSIONS_REQUEST_CODE);
          });
    } else if (storageShouldProvideRationale) {
      Timber.i("Displaying permission rationale to provide additional context.");
      ToastUtil.toastError(this, getString(R.string.permission_rationale_storage),
          view -> {
            // Request permission
            ActivityCompat.requestPermissions(this,
                new String[]{permission.WRITE_EXTERNAL_STORAGE},
                REQUEST_PERMISSIONS_REQUEST_CODE);
          });
    } else {
      Timber.i("Requesting permission");
      // Request permission. It's possible this can be auto answered if device policy
      // sets the permission in a given state or the user denied the permission
      // previously and checked "Never ask again".
      ActivityCompat.requestPermissions(MainActivity.this,
          new String[]{permission.ACCESS_FINE_LOCATION, permission.WRITE_EXTERNAL_STORAGE,
              permission.READ_PHONE_STATE}, REQUEST_PERMISSIONS_REQUEST_CODE);
    }
  }

  /**
   * Returns the current state of the permissions needed.
   */
  private boolean checkPermissions() {
    return PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(this,
        permission.ACCESS_FINE_LOCATION) &&
        PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(this,
            permission.WRITE_EXTERNAL_STORAGE) &&
        PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(this,
            permission.READ_PHONE_STATE);
  }

  public void showFeaturesFragment() {
    changeFragment(MainActivity.FEATURE_FRAGMENT_ID, true);
  }

  protected BaseFragment getLastFragment() {
    FragmentManager supportFragmentManager = getSupportFragmentManager();
    if (supportFragmentManager.getBackStackEntryCount() > 1) {
      BackStackEntry backStackEntryAt = supportFragmentManager
          .getBackStackEntryAt(supportFragmentManager.getBackStackEntryCount() - 1);
      String tag = backStackEntryAt.getName();
      BaseFragment lastFragment = (BaseFragment) supportFragmentManager.findFragmentByTag(tag);
      return lastFragment;
    }
    return null;
  }

  public void removeFragment(Fragment fragment) {
    FragmentManager manager = getSupportFragmentManager();
    FragmentTransaction trans = manager.beginTransaction();
    trans.remove(fragment);
    trans.commit();
    manager.popBackStack();
  }

  public void navigateToFragment(String fragmentName) {
    FragmentManager fm = getSupportFragmentManager();
    fm.popBackStack(fragmentName, FragmentManager.POP_BACK_STACK_INCLUSIVE);
  }

  protected void commitFragment(String fragmentTag, BaseFragment fragment, boolean addToBackStack) {
    try {
      if (!isFinishing()) {
        FragmentTransaction fragmentTransaction;
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container, fragment, fragmentTag);
        fragmentTransaction.addToBackStack(fragmentTag);
        fragmentTransaction.commit();
      }
    } catch (Exception e) {
      e.printStackTrace();
      ToastUtil.toastError(this, R.string.error_unknown_system_exception);
    } finally {
      Logger.sendError("MainActivity", "Exception in commitFragment");
    }
  }

  public void changeFragment(int fragmentId, boolean addToBackStack) {
    currentFragment = findFragment(fragmentId, new Bundle());
    if (Empty.isNotEmpty(currentFragment) && !currentFragment.isVisible()) {
      commitFragment(currentFragment.getFragmentTag(), currentFragment, addToBackStack);
    }
  }

  public void changeFragment(int fragmentId, Bundle args, boolean addToBackStack) {
    currentFragment = findFragment(fragmentId, args);
    if (Empty.isNotEmpty(currentFragment) && !currentFragment.isVisible()) {
      if (Empty.isNotEmpty(args)) {
        currentFragment.setArguments(args);
      }
      commitFragment(currentFragment.getFragmentTag(), currentFragment, addToBackStack);
    }
  }

  public abstract void onNavigationTapped();

  @OnClick({R.id.navigation_img, R.id.search_img, R.id.save_img, R.id.notif_img, R.id.filter_img})
  public void onClick(View view) {
    switch (view.getId()) {
      case R.id.navigation_img:
        onNavigationTapped();
        break;
      case R.id.search_img:
        Fragment orderFragment = getSupportFragmentManager()
            .findFragmentByTag(OrderFragment.class.getSimpleName());
        Fragment visitLine = getSupportFragmentManager()
            .findFragmentByTag(VisitLineFragment.class.getSimpleName());
        if (orderFragment != null && orderFragment.isVisible()) {
          ((OrderFragment) orderFragment).onSearchClicked();
        } else if (visitLine != null && visitLine.isVisible()) {
          FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
          CustomerSearchDialogFragment customerSearchDialogFragment = CustomerSearchDialogFragment
              .newInstance();
          customerSearchDialogFragment.show(ft, "customer search");
        } else {
          Bundle clickBundle = new Bundle();
          clickBundle.putBoolean(Constants.IS_CLICKABLE, false);
          changeFragment(MainActivity.CUSTOMER_SEARCH_FRAGMENT, clickBundle, true);
        }
        break;
      case R.id.save_img:
        onSaveImageClicked(true);
        break;
      case R.id.notif_img:
        changeFragment(MainActivity.CHAT_FRAGMENT, null, true);
        break;
      case R.id.filter_img:
        Fragment visitLine2 = getSupportFragmentManager()
            .findFragmentByTag(VisitLineFragment.class.getSimpleName());
        if (visitLine2 != null && visitLine2.isVisible()) {
          ((VisitLineFragment) visitLine2).showFilter();
        }
    }
  }

  public void onSaveImageClicked(boolean isRequiredMode) {
    Fragment questionnaireCategoryFragment = getSupportFragmentManager()
        .findFragmentByTag(QuestionnairesCategoryFragment.class.getSimpleName());
    Fragment questionnaireListFragment = getSupportFragmentManager()
        .findFragmentByTag(QuestionnaireListFragment.class.getSimpleName());
    Fragment questionsListFragment = getSupportFragmentManager()
        .findFragmentByTag(QuestionsListFragment.class.getSimpleName());
    if (questionsListFragment != null && questionsListFragment.isVisible()) {
      if (!isRequiredMode || ((QuestionsListFragment) questionsListFragment)
          .hasRequiredQuestionAnswer()) {
        saveImg.setVisibility(View.GONE);
        ((QuestionsListFragment) questionsListFragment).closeVisit();
        if (questionnaireCategoryFragment != null) {
          navigateToFragment(QuestionnairesCategoryFragment.class.getSimpleName());
        } else if (questionnaireListFragment != null) {
          navigateToFragment(QuestionnaireListFragment.class.getSimpleName());
        } else {
          onBackPressed();
        }
      } else {
        ToastUtil.toastError(this, "لطفا به همه سوالات ستاره دار پاسخ بدهید");
      }
    }
  }

  @Override
  public void onBackPressed() {
    hideKeyboard();
    try {
      FragmentManager supportFragmentManager = getSupportFragmentManager();
      if (supportFragmentManager.getBackStackEntryCount() > 1) {
        BackStackEntry backStackEntryAt = supportFragmentManager
            .getBackStackEntryAt(supportFragmentManager.getBackStackEntryCount() - 2);
        String tag = backStackEntryAt.getName();
        BaseFragment lastFragment = (BaseFragment) supportFragmentManager.findFragmentByTag(tag);
        if (Empty.isNotEmpty(lastFragment)) {
          findFragment(lastFragment.getFragmentId(), new Bundle());
        }
        BaseFragment lastItem = getLastFragment();
        if (Empty.isNotEmpty(lastFragment)) {
          if (lastItem instanceof VisitDetailFragment) {
            showTimer();
            ((VisitDetailFragment) lastItem).finishVisiting();
            return;
          } else if (lastItem instanceof PhoneVisitDetailFragment) {
            ((PhoneVisitDetailFragment) lastItem).finishVisiting();
            return;
          } else if (lastItem instanceof OrderFragment && !((OrderFragment) lastItem)
              .isExpandableVisible()) {
            ((OrderFragment) lastItem).onBackPressed();
            showNav();
            return;
          }/* else if (lastItem instanceof AdminFragment) {
            ((AdminFragment) lastItem).onBackPressed();
            return;
          }*/
        }
        super.onBackPressed();
      } else {
        showDialogForExit();
      }
    } catch (Exception e) {
      Logger.sendError("UI Exception", "Error in backPressed " + e.getMessage());
      Timber.e(e);
    }
  }

  public void setNavigationToolbarIcon(int id) {
    navigationImg.setImageResource(id);
  }

  public void changeTitle(String title) {
    toolbarTitle.setText(title);
  }

  public void setTimer(String text) {
    runOnUiThread(() -> chronometer.setText(NumberUtil.digitsToPersian(text)));
  }

  public void showTimer() {
    chronometer.setVisibility(View.VISIBLE);
  }

  public void hideTimer() {
    chronometer.setVisibility(View.GONE);
  }

  public void changeDetailContent(String content) {
    if (detailTv != null) {
      detailTv.setText(content);
    }
  }

  public void setToolbarIconVisibility(int id, int visibility) {
    View view = findViewById(id);
    view.setVisibility(visibility);
    if (visibility == View.VISIBLE) {
      hideTimer();
    }
  }

  public abstract void customizeToolbar(int fragmentId);

  private boolean canOpenDrawer(int fragmentId) {
    switch (fragmentId) {
      case FEATURE_FRAGMENT_ID:
      case VISIT_LIST_FRAGMENT_ID:
      case CUSTOMER_FRAGMENT:
      case CUSTOMER_SEARCH_FRAGMENT:
      case USER_TRACKING_FRAGMENT_ID:
      case REPORT_FRAGMENT:
      case GOODS_LIST_FRAGMENT_ID:
      case ANONYMOUS_QUESTIONNAIRE_FRAGMENT_ID:
      case VISITLINE_FRAGMENT_ID:
      case VISITLINE_DETAIL_FRAGMENT_ID:
      case SYSTEM_CUSTOMER_FRAGMENT:
      case SETTING_FRAGMENT:
      case CHAT_FRAGMENT:

        return true;
    }
    return false;
  }

  protected BaseFragment findFragment(int fragmentId, Bundle args) {
    BaseFragment fragment = null;
    int parent = 0;
    if (fragmentId == FEATURE_FRAGMENT_ID) {
      setNavigationToolbarIcon(R.drawable.ic_menu);
//      setDrawerEnable(true);
      displayNotifbutton(false);//TODO:LATER
    } else {
      setNavigationToolbarIcon(R.drawable.ic_arrow_forward);
//      setDrawerEnable(false);
      displayNotifbutton(false);
    }

    setDrawerEnable(canOpenDrawer(fragmentId));

    //show search icon in customer fragment
    if (fragmentId == CUSTOMER_FRAGMENT || fragmentId == VISITLINE_FRAGMENT_ID) {
      searchImg.setVisibility(View.VISIBLE);
    } else {
      searchImg.setVisibility(View.GONE);
    }
    if (fragmentId == VISITLINE_FRAGMENT_ID) {
      String saleType = new SettingServiceImpl().getSettingValue(ApplicationKeys.SETTING_SALE_TYPE);
      if (!ApplicationKeys.SALE_DISTRIBUTER.equals(saleType)) {
        filterImg.setVisibility(View.VISIBLE);
      }
    } else {
      filterImg.setVisibility(View.GONE);
    }
    //hide save icon in question list fragment
    if (fragmentId != QUESTION_LIST_FRAGMENT_ID) {
      saveImg.setVisibility(View.GONE);
    }

    //hide timer in visit detail fragment
    if (fragmentId != VISIT_DETAIL_FRAGMENT_ID) {
      hideTimer();
    }

//    customizeToolbar(fragmentId);
    switch (fragmentId) {
      case FEATURE_FRAGMENT_ID:
        fragment = FeaturesFragment.newInstance();
        break;
      case CHAT_FRAGMENT:
        fragment = ChatFragment.newInstance();
        break;
      case SETTING_FRAGMENT:
        fragment = SettingFragment.newInstance();
        break;
      case VISIT_DETAIL_FRAGMENT_ID:
        fragment = VisitDetailFragment.newInstance();
        break;
      case PHONE_VISIT_DETAIL_FRAGMENT_ID:
        fragment = PhoneVisitDetailFragment.newInstance();
        break;
      case ORDER_INFO_FRAGMENT:
        fragment = OrderInfoFragment.newInstance();
        break;
      case REGISTER_PAYMENT_FRAGMENT:
        fragment = RegisterPaymentFragment.newInstance();
        break;
      case ADMIN_FRAGMENT_ID:
        fragment = AdminFragment.newInstance();
        break;

      case CUSTOMER_FRAGMENT:
        fragment = CustomerFragment.newInstance();
        break;
      case CUSTOMER_SEARCH_FRAGMENT:
        fragment = CustomerSearchFragment.newInstance();
        break;
      case NEW_CUSTOMER_DETAIL_FRAGMENT_ID:
        fragment = AddCustomerFragment.newInstance();
        break;
      case REPORT_FRAGMENT:
        fragment = ReportFragment.newInstance();
        break;
      case ABOUT_US_FRAGMENT_ID:
        fragment = new AboutUsFragment();
        break;
      case GOODS_LIST_FRAGMENT_ID:
        fragment = OrderFragment.newInstance();
        break;
      case SAVE_LOCATION_FRAGMENT_ID:
        fragment = new SaveLocationFragment();
        break;
      case QUESTIONNAIRE_CATEGORY_FRAGMENT_ID:
        fragment = QuestionnairesCategoryFragment.newInstance();
        break;
      case QUESTIONNAIRE_LIST_FRAGMENT_ID:
        fragment = QuestionnaireListFragment.newInstance();
        break;
      case QUESTION_LIST_FRAGMENT_ID:
        fragment = QuestionsListFragment.newInstance();
        break;
      case USER_TRACKING_FRAGMENT_ID://20
        fragment = new UserTrackingFragment();
        break;
      case VISITLINE_FRAGMENT_ID:
        fragment = VisitLineFragment.newInstance();
        break;
      case ANONYMOUS_QUESTIONNAIRE_FRAGMENT_ID:
        fragment = AnonymousQuestionnaireFragment.newInstance();
        break;
      case VISITLINE_DETAIL_FRAGMENT_ID:
        fragment = VisitLineDetailFragment.newInstance();
        break;
    }
    Analytics.logContentView("Fragment " + String.valueOf(fragmentId));
    return fragment;
  }

  private void displayNotifbutton(boolean show) {
    notifImg.setVisibility(show ? View.VISIBLE : View.GONE);
  }

  protected abstract void setDrawerEnable(boolean isLock);


  public abstract void closeDrawer();

  @Override
  protected void attachBaseContext(Context newBase) {
    super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
  }

  public void showMenu() {
    navigationImg.setVisibility(View.VISIBLE);
    navigationImg.setImageResource(R.drawable.ic_menu);
  }

  public void showNav() {
    navigationImg.setVisibility(View.VISIBLE);
    navigationImg.setImageResource(R.drawable.ic_arrow_forward);
  }

  public void searchImageVisibility(int visibility) {
    if (visibility == View.VISIBLE) {
      hideTimer();
    }
    searchImg.setVisibility(visibility);
  }

  public boolean isPhoneVisit() {
    return phoneVisit;
  }

  public void setPhoneVisit(boolean phoneVisit) {
    this.phoneVisit = phoneVisit;
  }
}
