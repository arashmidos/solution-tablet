package com.parsroyal.solutiontablet.pushe;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import co.ronash.pushe.PusheListenerService;
import com.parsroyal.solutiontablet.R;
import com.parsroyal.solutiontablet.data.model.PushNotification;
import com.parsroyal.solutiontablet.ui.activity.SplashActivity;
import com.parsroyal.solutiontablet.util.BadgerHelper;
import com.parsroyal.solutiontablet.util.NetworkUtil;
import com.parsroyal.solutiontablet.util.PushUtility;
import org.json.JSONObject;
import timber.log.Timber;

public class MyPushListener extends PusheListenerService {

  @Override
  public void onMessageReceived(JSONObject customContent, JSONObject pushMessage) {
    Timber.tag("Pushe").d("Custom jsonn Message: %s", customContent.toString());

    try {
      PushNotification push = PushUtility.deserializeJsonFromPushe(customContent);
      NotificationCompat.Builder mBuilder;
      if (push != null && !NetworkUtil.isTokenExpired()) {
        Intent intent = new Intent(this, SplashActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        mBuilder = new NotificationCompat.Builder(getApplicationContext(), "pars")
            .setSmallIcon(R.drawable.ic_launcher)
            .setContentTitle(push.getPushTitle())
            .setContentText(push.getPushData())
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
          CharSequence name = getString(R.string.app_name);
          int importance = NotificationManager.IMPORTANCE_DEFAULT;
          NotificationChannel channel = new NotificationChannel("pars", name, importance);
          NotificationManager notificationManager = getSystemService(NotificationManager.class);
          notificationManager.createNotificationChannel(channel);
        }
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(1, mBuilder.build());
        BadgerHelper.addBadge(getApplicationContext(), 1);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}