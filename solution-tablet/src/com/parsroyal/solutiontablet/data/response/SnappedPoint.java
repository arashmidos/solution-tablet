
package com.parsroyal.solutiontablet.data.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.android.gms.maps.model.LatLng;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SnappedPoint {

  private Location location;
  private Integer originalIndex;
  private String placeId;

  public Location getLocation() {
    return location;
  }

  public void setLocation(Location location) {
    this.location = location;
  }

  public Integer getOriginalIndex() {
    return originalIndex;
  }

  public void setOriginalIndex(Integer originalIndex) {
    this.originalIndex = originalIndex;
  }

  public String getPlaceId() {
    return placeId;
  }

  public void setPlaceId(String placeId) {
    this.placeId = placeId;
  }

  public LatLng getLatLng() {
    return new LatLng(location.getLatitude(), location.getLongitude());
  }
}
