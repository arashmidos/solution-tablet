package com.parsroyal.solutiontablet.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootReceiver extends BroadcastReceiver {

  TrackerAlarmReceiver alarm = new TrackerAlarmReceiver();

  @Override
  public void onReceive(Context context, Intent intent) {
    if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
      alarm.setAlarm(context);
    }
  }
}