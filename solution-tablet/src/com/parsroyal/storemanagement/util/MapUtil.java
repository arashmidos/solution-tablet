package com.parsroyal.storemanagement.util;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import java.util.Locale;

public class MapUtil {

  public static void navigateTo(Context context, double lat, double lng)
      throws GooglePlayServicesNotAvailableException {
    if ("google".equals(PreferenceHelper.getDefaultNavigator())) {
      Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(
          "google.navigation:q=" + lat + "," + lng));
      i.setPackage("com.google.android.apps.maps");
      if (i.resolveActivity(context.getPackageManager()) != null) {
        context.startActivity(i);
      } else {
        throw new GooglePlayServicesNotAvailableException(-1);
      }
    } else {
      try {
        Intent i = new Intent(Intent.ACTION_VIEW,
            Uri.parse(String.format(Locale.UK, "waze://?ll=%s,%s&navigate=yes",
                lat, lng)));
        context.startActivity(i);
      } catch (ActivityNotFoundException ex) {
        // If Waze is not installed, open it in Google Play:
        Intent intent = new Intent(Intent.ACTION_VIEW,
            Uri.parse("market://details?id=com.waze"));
        context.startActivity(intent);
      }
    }
  }
}
