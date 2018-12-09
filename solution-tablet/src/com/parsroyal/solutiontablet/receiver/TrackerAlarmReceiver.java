package com.parsroyal.solutiontablet.receiver;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;
import com.parsroyal.solutiontablet.constants.Constants;
import com.parsroyal.solutiontablet.service.SendLocationService;
import com.parsroyal.solutiontablet.service.SettingService;
import com.parsroyal.solutiontablet.service.impl.SettingServiceImpl;
import com.parsroyal.solutiontablet.util.Empty;
import com.parsroyal.solutiontablet.util.Logger;
import com.parsroyal.solutiontablet.util.constants.ApplicationKeys;
import java.util.Calendar;

/**
 * Created by Arash on 2016-09-10
 */
public class TrackerAlarmReceiver extends WakefulBroadcastReceiver {

  public static final String TAG = TrackerAlarmReceiver.class.getSimpleName();
  private AlarmManager alarmMgr;
  private PendingIntent alarmIntent;
  private SettingService settingService;

  @Override
  public void onReceive(Context context, Intent intent) {
    Log.i(TAG, "Received");
    settingService = new SettingServiceImpl();

    if (Empty.isNotEmpty(settingService.getSettingValue(ApplicationKeys.SALESMAN_ID))) {
      Log.i(TAG, "required information is available. trying to set alarm and run service");
      Intent service = new Intent(context, SendLocationService.class);
      startWakefulService(context, service);

    } else {
      Log.i(TAG, "required information is not available");
    }
  }

  public void setAlarm(Context context) {
    settingService = new SettingServiceImpl();

    Log.i(TAG, "check for required information");

    if (Empty.isNotEmpty(settingService.getSettingValue(ApplicationKeys.SALESMAN_ID))) {
      Log.i(TAG, "required information is available. trying to set alarm");
      alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
      Intent intent = new Intent(context, TrackerAlarmReceiver.class);
      alarmIntent = PendingIntent.getBroadcast(context.getApplicationContext(), 0, intent,
          PendingIntent.FLAG_UPDATE_CURRENT);
      alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, Calendar.getInstance().getTimeInMillis(),
          Constants.GPS_SEND_INTERVAL_IN_SECOND * 1000, alarmIntent);
      Log.d(TAG, "Alarm set successfully!");
    } else {
      Log.i(TAG, "required information is not available");
    }

    ComponentName receiver = new ComponentName(context, TrackerAlarmReceiver.class);
    PackageManager pm = context.getPackageManager();

    pm.setComponentEnabledSetting(receiver,
        PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
        PackageManager.DONT_KILL_APP);
  }

  public void cancelAlarm(Context context) {
    try {
      if (alarmMgr != null) {
        alarmMgr.cancel(alarmIntent);
      }

      ComponentName receiver = new ComponentName(context, TrackerAlarmReceiver.class);
      PackageManager pm = context.getPackageManager();

      pm.setComponentEnabledSetting(receiver,
          PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
          PackageManager.DONT_KILL_APP);
    } catch (Exception ex) {
      Logger.sendError("Alarm", "Error in canceling alarm " + ex.getMessage());
      Log.e(TAG, "error in canceling alarm", ex);
    }
  }
}
