package com.conta.comer.receiver;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.SystemClock;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

import com.conta.comer.service.SettingService;
import com.conta.comer.service.TrackLocationService;
import com.conta.comer.service.impl.SettingServiceImpl;
import com.conta.comer.util.Empty;
import com.conta.comer.util.constants.ApplicationKeys;

/**
 * Created by Arash on 2016-09-10
 */
public class TrackerAlarmReceiver extends WakefulBroadcastReceiver
{
    public static final String TAG = TrackerAlarmReceiver.class.getSimpleName();
    private AlarmManager alarmMgr;
    private PendingIntent alarmIntent;
    private SettingService settingService;

    @Override
    public void onReceive(Context context, Intent intent)
    {
        Log.i(TAG, "Received");
        settingService = new SettingServiceImpl(context);

        if (Empty.isNotEmpty(settingService.getSettingValue(ApplicationKeys.SALESMAN_ID)))
        {
            Log.i(TAG, "required information is available. trying to set alarm and run service");
            Intent service = new Intent(context, TrackLocationService.class);
            startWakefulService(context, service);
        } else
        {
            Log.i(TAG, "required information is not available");
        }
    }

    public void setAlarm(Context context)
    {
        settingService = new SettingServiceImpl(context);

        Log.i(TAG, "check for required information");

        if (Empty.isNotEmpty(settingService.getSettingValue(ApplicationKeys.SETTING_USERNAME)))
        {
            Log.i(TAG, "required information is available. trying to set alarm");
            alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            Intent intent = new Intent(context, TrackerAlarmReceiver.class);
            alarmIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
            String timeInterval = settingService.getSettingValue(ApplicationKeys.SETTING_GPS_INTERVAL);
            if (Empty.isNotEmpty(timeInterval))
            {
                Log.d(TAG, "Alarm set successfully!");
                alarmMgr.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime(), Integer.parseInt(timeInterval) * 1000, alarmIntent);
            }

            ComponentName receiver = new ComponentName(context, TrackerAlarmReceiver.class);
            PackageManager pm = context.getPackageManager();

            pm.setComponentEnabledSetting(receiver,
                    PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                    PackageManager.DONT_KILL_APP);
        } else
        {
            Log.i(TAG, "required information is not available");
        }
    }

    public void cancelAlarm(Context context)
    {
        try
        {
            if (alarmMgr != null)
            {
                alarmMgr.cancel(alarmIntent);
            }

            ComponentName receiver = new ComponentName(context, TrackerAlarmReceiver.class);
            PackageManager pm = context.getPackageManager();

            pm.setComponentEnabledSetting(receiver,
                    PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                    PackageManager.DONT_KILL_APP);
        } catch (Exception ex)
        {
            Log.e(TAG, "error in canceling alarm", ex);
        }
    }
}
