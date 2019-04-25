package com.parsroyal.solutiontablet.navigation;

import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;
import com.parsroyal.solutiontablet.R;
import com.parsroyal.solutiontablet.data.listmodel.CustomerListModel;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.infowindow.MarkerInfoWindow;

public class PrsMarkerInfoWindow extends MarkerInfoWindow {

  private CustomerListModel customerModel;

  /**
   * @param layoutResId layout that must contain these ids: bubble_title,bubble_description,
   * bubble_subdescription, bubble_image
   */
  public PrsMarkerInfoWindow(int layoutResId, MapView mapView,
      CustomerListModel customerListModel) {
    super(layoutResId, mapView);
    this.customerModel = customerListModel;
  }

  /**
   * @param layoutResId layout that must contain these ids: bubble_title,bubble_description,
   * bubble_subdescription, bubble_image
   */
  public PrsMarkerInfoWindow(int layoutResId, MapView mapView) {
    super(layoutResId, mapView);
  }

  @Override
  public void onOpen(Object item) {
    super.onOpen(item);
    View root = mView.findViewById(R.id.root);
    root.setOnTouchListener((v, event) -> {
      if (event.getAction() == MotionEvent.ACTION_UP) {

        Toast.makeText(mMapView.getContext(), "You liked ", Toast.LENGTH_SHORT).show();
        close();
      }
      return true;
    });

  }
}
