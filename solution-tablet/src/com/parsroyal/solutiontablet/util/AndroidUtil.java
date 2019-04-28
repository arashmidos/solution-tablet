package com.parsroyal.solutiontablet.util;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import com.parsroyal.solutiontablet.R;
import com.parsroyal.solutiontablet.ui.activity.MainActivity;
import java.util.Locale;

public class AndroidUtil {

  public static void navigateByWaze(Context context, double lat, double lng) {
    try {
      Intent i = new Intent(Intent.ACTION_VIEW,
          Uri.parse(String.format(Locale.UK, "waze://?ll=%s,%s&navigate=yes", lat, lng)));
      context.startActivity(i);
    } catch (ActivityNotFoundException ex) {
      // If Waze is not installed, open it in Google Play:
      Intent intent = new Intent(Intent.ACTION_VIEW,
          Uri.parse("market://details?id=com.waze"));
      context.startActivity(intent);
    }
  }

  public static void navigateByGoogle(MainActivity context, double latitude, double longitude) {
    Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(
        "google.navigation:q=" + latitude + "," + longitude));
    i.setPackage("com.google.android.apps.maps");
    if (i.resolveActivity(context.getPackageManager()) != null) {
      context.startActivity(i);
    } else {
      ToastUtil.toastError(context, context.getString(R.string.error_google_not_installed));
//      ToastUtil.toastError(mainLay, context.getString(R.string.error_google_not_installed));
    }
  }

  public static void navigate(MainActivity context, double latitude, double longitude) {
    if ("google".equals(PreferenceHelper.getDefaultNavigator())) {
      AndroidUtil.navigateByGoogle(context, latitude, longitude);
    } else {
      AndroidUtil.navigateByWaze(context, latitude, longitude);
    }
  }
}
