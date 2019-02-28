package com.parsroyal.storemanagement.util;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;

/**
 * Created by arash on 1/17/18.
 */

public class DeviceUtil {

  private boolean isMyServiceRunning(Context context, Class<?> serviceClass) {
    try {
      ActivityManager manager = (ActivityManager) context
          .getSystemService(Context.ACTIVITY_SERVICE);
      for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
        if (serviceClass.getName().equals(service.service.getClassName())) {
          return true;
        }
      }
      return false;

    } catch (Exception ignore) {
      return false;
    }
  }
}
