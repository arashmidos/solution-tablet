package com.parsroyal.solutiontablet.data.event;

import android.location.Location;
import com.parsroyal.solutiontablet.constants.StatusCodes;

public class GPSEvent extends Event {

  protected Location location;

  public GPSEvent(Location location) {
    this.location = location;
    statusCode = StatusCodes.NEW_GPS_LOCATION;
  }

  public Location getLocation() {
    return location;
  }

  public void setLocation(Location location) {
    this.location = location;
  }
}
