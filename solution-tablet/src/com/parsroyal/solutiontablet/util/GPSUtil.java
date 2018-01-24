package com.parsroyal.solutiontablet.util;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.preference.PreferenceManager;
import android.provider.Settings;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Arash on 1/17/2018.
 */
public class GPSUtil {

  static final String KEY_REQUESTING_LOCATION_UPDATES = "requesting_locaction_updates";
  private static boolean deviceChecked = false;
  private static boolean deviceRooted;

  public static boolean isGpsAvailable(Context context) {
    LocationManager locationManager = (LocationManager) context
        .getSystemService(Context.LOCATION_SERVICE);
    return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
  }

  /**
   * Returns true if requesting location updates, otherwise returns false.
   *
   * @param context The {@link Context}.
   */
  public static boolean requestingLocationUpdates(Context context) {
    return PreferenceManager.getDefaultSharedPreferences(context)
        .getBoolean(KEY_REQUESTING_LOCATION_UPDATES, false);
  }

  /**
   * Stores the location updates state in SharedPreferences.
   *
   * @param requestingLocationUpdates The location updates state.
   */
  public static void setRequestingLocationUpdates(Context context,
      boolean requestingLocationUpdates) {
    PreferenceManager.getDefaultSharedPreferences(context)
        .edit()
        .putBoolean(KEY_REQUESTING_LOCATION_UPDATES, requestingLocationUpdates)
        .apply();
  }

  public static boolean isLocationMock(Context context, Location location) {
    if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
      return location.isFromMockProvider();
    } else {
      String mockLocation = "0";
      try {
        mockLocation = Settings.Secure
            .getString(context.getContentResolver(), Settings.Secure.ALLOW_MOCK_LOCATION);
      } catch (Exception e) {
        e.printStackTrace();
      }
      return !mockLocation.equals("0");
    }
  }

  public static List<String> getListOfFakeLocationApps(Context context) {
    PackageManager p = context.getPackageManager();
    final List<PackageInfo> appinstall = p.getInstalledPackages(PackageManager.GET_PERMISSIONS |
        PackageManager.GET_PROVIDERS);

    List<String> runningApps = new ArrayList<>();
    for (PackageInfo packageInfo : appinstall) {

      if (hasAppPermission(packageInfo) && !packageInfo.packageName.equals("com.parsroyal.solutionmobile")) {
        try {
          ApplicationInfo applicationInfo = p.getApplicationInfo(packageInfo.packageName, 0);
          runningApps.add(p.getApplicationLabel(applicationInfo).toString());
        } catch (NameNotFoundException e) {
          e.printStackTrace();
        }
      }
    }

    return runningApps;
  }

  private static boolean hasAppPermission(PackageInfo packageInfo) {

    if (packageInfo.requestedPermissions != null) {
      for (String requestedPermission : packageInfo.requestedPermissions) {
        if (requestedPermission.equals("android.permission.ACCESS_MOCK_LOCATION")) {
          return true;
        }
      }
    }
    return false;
  }

  /**
   * Checks if the device is rooted.
   *
   * @return <code>true</code> if the device is rooted, <code>false</code> otherwise.
   */
  public static boolean isDeviceRooted() {

    if (!deviceChecked) {
      deviceChecked = true;
    } else {
      return deviceRooted;
    }
    // Get the build tags info - See note below to know more about it
    String buildTags = android.os.Build.TAGS;
    if (buildTags != null && buildTags.contains("test-keys")) {
      deviceRooted = true;
      return deviceRooted;
    }
    // Check if Superuser.apk is present
    try {
      File file = new File("/system/app/Superuser.apk");
      if (file.exists()) {
        deviceRooted = true;
        return deviceRooted;
      }
    } catch (Exception e1) {
      // ignore
    }
    // try executing commands as a superUser
    deviceRooted = canExecuteCommand("/system/xbin/which su")
        || canExecuteCommand("/system/bin/which su") || canExecuteCommand("which su");
    return deviceRooted;
  }

  // Executes the specified string command in a separate process
  private static boolean canExecuteCommand(String command) {
    boolean executedSuccesfully;
    try {
      Runtime.getRuntime().exec(command);
      executedSuccesfully = true;
    } catch (Exception e) {
      executedSuccesfully = false;
    }
    return executedSuccesfully;
  }
}
