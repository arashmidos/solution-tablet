package com.parsroyal.solutiontablet.util;

import android.location.Location;

/**
 * Created by Arashmidos on 2017-04-12.
 */

public class LocationUtil {

  public static float distanceBetween(Location l1, Location l2) {
    return l1.distanceTo(l2);
  }

  public static float distanceBetween(Location l1, double lat2, double long2) {
    Location l2 = new Location("");
    l2.setLatitude(lat2);
    l2.setLongitude(long2);
    return distanceBetween(l1, l2);
  }

  /**
   * Calculate distance between two locations in meter
   *
   * @return distance in meter, 0.0 if location not set
   */
  public static Float distanceBetween(Double lat1, Double long1, double lat2, double long2) {
    //If it unknown location
    if (lat1 == 0.0f || long1 == 0.0f || lat2 == 0.0f || long2 == 0.0f) {
      //Set it to infinity
      return -1f;
    }

    Location l1 = new Location("");
    l1.setLatitude(lat1);
    l1.setLongitude(long1);

    Location l2 = new Location("");
    l2.setLatitude(lat2);
    l2.setLongitude(long2);

    return distanceBetween(l1, l2);
  }
}
