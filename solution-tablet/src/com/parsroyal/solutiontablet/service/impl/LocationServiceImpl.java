package com.parsroyal.solutiontablet.service.impl;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import com.parsroyal.solutiontablet.exception.GPSIsNotEnabledException;
import com.parsroyal.solutiontablet.service.LocationService;
import com.parsroyal.solutiontablet.ui.observer.FindLocationListener;
import com.parsroyal.solutiontablet.util.Empty;
import com.parsroyal.solutiontablet.util.GPSUtil;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Edited by Arash on 6/29/2016
 */
public class LocationServiceImpl implements LocationService, LocationListener {

  private static final Integer TIMEOUT = 15000;

  private Context context;
  private LocationListener locationListener;
  private LocationManager locationManager;
  private Timer timerTimeout;
  private FindLocationListener listener;

  public LocationServiceImpl(Context context) {
    this.context = context;
  }

  @Override
  public void findCurrentLocation(FindLocationListener listener) {
    this.listener = listener;
    if (GPSUtil.isGpsAvailable(context)) {
      requestForLocation();
    } else {
      throw new GPSIsNotEnabledException();
    }
  }

  @Override
  public void stopFindingLocation() {
    if (Empty.isNotEmpty(locationManager)) {
      locationManager.removeUpdates(LocationServiceImpl.this);
    }
  }

  private void requestForLocation() {
    locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
    List<String> allProviders = locationManager.getAllProviders();
    if (allProviders.contains(LocationManager.GPS_PROVIDER) && locationManager
        .isProviderEnabled(LocationManager.GPS_PROVIDER)) {
      locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
    }
    if (allProviders.contains(LocationManager.NETWORK_PROVIDER) && locationManager
        .isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
      locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
    }

    timerTimeout = new Timer();
    timerTimeout.schedule(new TimerTask() {
      @Override
      public void run() {
        locationManager.removeUpdates(LocationServiceImpl.this);
        LocationServiceImpl.this.listener.timeOut();
      }
    }, TIMEOUT);
  }

  @Override
  public void onLocationChanged(Location location) {
    if (Empty.isNotEmpty(timerTimeout)) {
      timerTimeout.cancel();
    }
    timerTimeout = null;
    locationManager.removeUpdates(this);
    listener.foundLocation(location);
  }

  @Override
  public void onStatusChanged(String provider, int status, Bundle extras) {

  }

  @Override
  public void onProviderEnabled(String provider) {

  }

  @Override
  public void onProviderDisabled(String provider) {

  }
}
