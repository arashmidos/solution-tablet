package com.parsroyal.solutiontablet.util;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v7.app.NotificationCompat;
import android.view.View;

import com.parsroyal.solutiontablet.R;

/**
 * Created by Arashmidos on 2016-11-07.
 */

public class NotificationUtil
{
    private static final int GPS_DISABLED_NF = 1010;

    public static void showGPSDisabled(Context context)
    {
        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);

        PendingIntent pi = PendingIntent.getActivity(context, 0, intent, 0);

        Notification notification = new NotificationCompat.Builder(context)
                .setTicker(context.getString(R.string.gps_tracking_disabled))
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle(context.getString(R.string.gps_tracking_disabled))
                .setContentText(context.getString(R.string.error_gps_is_off))
                .setContentIntent(pi)
                .setAutoCancel(true)
                .build();

        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        nm.notify(GPS_DISABLED_NF, notification);
    }

    public static void makeSnack(View view, String msg)
    {
        final Snackbar snack = Snackbar.make(view, msg, Snackbar.LENGTH_INDEFINITE);
        snack.setAction("Ok", new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                snack.dismiss();
            }
        });
        snack.show();
    }
}
