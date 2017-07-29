package com.parsroyal.solutiontablet.util;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.preference.PreferenceManager;

/**
 * Created by Mahyar on 6/22/2015.
 */
public class GPSUtil {

  public static boolean isGpsAvailable(Context context) {
    LocationManager locationManager = (LocationManager) context
        .getSystemService(Context.LOCATION_SERVICE);
    return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
  }

  static final String KEY_REQUESTING_LOCATION_UPDATES = "requesting_locaction_updates";

  /**
   * Returns true if requesting location updates, otherwise returns false.
   *
   * @param context The {@link Context}.
   */
  public static boolean requestingLocationUpdates(Context context) {
    return PreferenceManager.getDefaultSharedPreferences(context)
        .getBoolean(KEY_REQUESTING_LOCATION_UPDATES, false);
  }

  /**
   * Stores the location updates state in SharedPreferences.
   *
   * @param requestingLocationUpdates The location updates state.
   */
  public static void setRequestingLocationUpdates(Context context,
      boolean requestingLocationUpdates) {
    PreferenceManager.getDefaultSharedPreferences(context)
        .edit()
        .putBoolean(KEY_REQUESTING_LOCATION_UPDATES, requestingLocationUpdates)
        .apply();
  }
}
