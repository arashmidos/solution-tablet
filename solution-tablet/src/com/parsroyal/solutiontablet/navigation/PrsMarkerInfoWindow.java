package com.parsroyal.solutiontablet.navigation;

import android.view.MotionEvent;
import android.view.View;
import com.parsroyal.solutiontablet.R;
import com.parsroyal.solutiontablet.data.listmodel.CustomerListModel;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.infowindow.MarkerInfoWindow;

public class PrsMarkerInfoWindow extends MarkerInfoWindow {

  private OnInfoWindowClickListener listener;
  private CustomerListModel customerModel;

  /**
   * @param layoutResId layout that must contain these ids: bubble_title,bubble_description,
   * bubble_subdescription, bubble_image
   */
  public PrsMarkerInfoWindow(int layoutResId, MapView mapView,
      CustomerListModel customerListModel, OnInfoWindowClickListener listener) {
    super(layoutResId, mapView);
    this.customerModel = customerListModel;
    this.listener = listener;
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
        listener.onClick(customerModel);
        close();
      }
      return true;
    });

  }


  public interface OnInfoWindowClickListener {

    void onClick(CustomerListModel model);
  }
}
