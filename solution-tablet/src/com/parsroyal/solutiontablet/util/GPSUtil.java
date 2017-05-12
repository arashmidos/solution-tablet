package com.parsroyal.solutiontablet.util;

import android.content.Context;
import android.location.LocationManager;

/**
 * Created by Mahyar on 6/22/2015.
 */
public class GPSUtil {

  public static boolean isGpsAvailable(Context context) {
    LocationManager locationManager = (LocationManager) context
        .getSystemService(Context.LOCATION_SERVICE);
    return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
  }
}
