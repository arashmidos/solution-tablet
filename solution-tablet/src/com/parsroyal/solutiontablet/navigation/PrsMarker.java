package com.parsroyal.solutiontablet.navigation;

import com.parsroyal.solutiontablet.vrp.model.LocationResponse;
import java.util.ArrayList;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

public class PrsMarker extends Marker {

  private LocationResponse location;
  private int index;
  private ArrayList<GeoPoint> shape;

  public PrsMarker(MapView mapView) {
    super(mapView);
  }

  public void setLocation(LocationResponse location) {
    this.location = location;
  }

  public LocationResponse getLocation() {
    return location;
  }

  public void setIndex(int index) {
    this.index = index;
  }

  public int getIndex() {
    return index;
  }

  public void setShape(ArrayList<GeoPoint> shape) {
    this.shape = shape;
  }

  public ArrayList<GeoPoint> getShape() {
    return shape;
  }
}
