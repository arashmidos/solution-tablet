
package com.parsroyal.solutiontablet.data.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.android.gms.maps.model.LatLng;
import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MapRoadResponse {

  private List<SnappedPoint> snappedPoints = null;
  private String warningMessage;

  public List<SnappedPoint> getSnappedPoints() {
    return snappedPoints;
  }

  public void setSnappedPoints(List<SnappedPoint> snappedPoints) {
    this.snappedPoints = snappedPoints;
  }

  public String getWarningMessage() {
    return warningMessage;
  }

  public void setWarningMessage(String warningMessage) {
    this.warningMessage = warningMessage;
  }

  public List<LatLng> getLatLngList() {
    List<LatLng> list = new ArrayList<>();
    if (snappedPoints != null) {
      for (SnappedPoint point: snappedPoints) {
        list.add(point.getLatLng());
      }
    }
    return list;
  }
}
