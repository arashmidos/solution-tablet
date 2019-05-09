package com.parsroyal.solutiontablet.util;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import java.util.ArrayList;
import java.util.Locale;
import org.osmdroid.util.BoundingBox;
import org.osmdroid.util.GeoPoint;

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

  public static BoundingBox computeArea(ArrayList<GeoPoint> points) {

    double nord = 0, sud = 0, ovest = 0, est = 0;

    for (int i = 0; i < points.size(); i++) {
      if (points.get(i) == null) {
        continue;
      }

      double lat = points.get(i).getLatitude();
      double lon = points.get(i).getLongitude();

      if ((i == 0) || (lat > nord)) {
        nord = lat;
      }
      if ((i == 0) || (lat < sud)) {
        sud = lat;
      }
      if ((i == 0) || (lon < ovest)) {
        ovest = lon;
      }
      if ((i == 0) || (lon > est)) {
        est = lon;
      }

    }

    return new BoundingBox(nord, est, sud, ovest);

  }
}
