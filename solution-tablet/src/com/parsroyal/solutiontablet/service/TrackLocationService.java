package com.parsroyal.solutiontablet.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import com.crashlytics.android.Crashlytics;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.parsroyal.solutiontablet.data.dao.KeyValueDao;
import com.parsroyal.solutiontablet.data.dao.impl.KeyValueDaoImpl;
import com.parsroyal.solutiontablet.data.entity.Position;
import com.parsroyal.solutiontablet.service.impl.PositionServiceImpl;
import com.parsroyal.solutiontablet.service.impl.SettingServiceImpl;
import com.parsroyal.solutiontablet.util.Empty;
import com.parsroyal.solutiontablet.util.NetworkUtil;
import com.parsroyal.solutiontablet.util.NotificationUtil;
import com.parsroyal.solutiontablet.util.constants.ApplicationKeys;

public class TrackLocationService extends Service implements GoogleApiClient.ConnectionCallbacks,
    GoogleApiClient.OnConnectionFailedListener, LocationListener {

  public static final String TAG = TrackLocationService.class.getSimpleName();

  private SettingService settingService;
  private PositionService positionService;
  private Location bestLocation;
  private boolean isGps;
  private GoogleApiClient googleApiClient;
  private Location currentLocation;
  private Integer interval;
  private boolean instantUpdate;
  private KeyValueDao keyValueDao;
  private long salesmanId;

  /**
   * Creates an IntentService.  Invoked by your subclass's constructor.
   */
  public TrackLocationService() {
    super();
  }

  @Override
  public void onCreate() {
    super.onCreate();
    Log.d(TAG, "Service.onCreate");
    settingService = new SettingServiceImpl(this);
    positionService = new PositionServiceImpl(this);
    keyValueDao = new KeyValueDaoImpl(this);
    try {
      salesmanId = Long
          .parseLong(keyValueDao.retrieveByKey(ApplicationKeys.SALESMAN_ID).getValue());

      interval = Integer
          .valueOf(settingService.getSettingValue(ApplicationKeys.SETTING_GPS_INTERVAL));
      instantUpdate = settingService.getSettingValue(ApplicationKeys.SETTING_GPS_ENABLE)
          .equals("1");

      googleApiClient = new GoogleApiClient.Builder(this)
          .addConnectionCallbacks(this)
          .addOnConnectionFailedListener(this)
          .addApi(LocationServices.API).build();
    } catch (Exception ex) {
      Crashlytics.log(Log.ERROR, "Location Service", "Error in creating LocationService " + ex.getMessage());
      Log.d(TAG, "Error in creating google api client");
    }

  }

  @Override
  public IBinder onBind(Intent intent) {
    return null;
  }

  /**
   * Called by the system every time a client explicitly starts the service by calling
   * {@link Context#startService}, providing the arguments it supplied and a
   * unique integer token representing the start request.  Do not call this method directly.
   * <p>
   * <p>For backwards compatibility, the default implementation calls
   * {@link #onStart} and returns either {@link #START_STICKY}
   * or {@link #START_STICKY_COMPATIBILITY}.
   * <p>
   * <p>If you need your application to run on platform versions prior to API
   * level 5, you can use the following model to handle the older {@link #onStart}
   * callback in that case.  The <code>handleCommand</code> method is implemented by
   * you as appropriate:
   * <p>
   * {@sample development/samples/ApiDemos/src/com/example/android/apis/app/ForegroundService.java
   * start_compatibility}
   * <p>
   * <p class="caution">Note that the system calls this on your
   * service's main thread.  A service's main thread is the same
   * thread where UI operations take place for Activities running in the
   * same process.  You should always avoid stalling the main
   * thread's event loop.  When doing long-running operations,
   * network calls, or heavy disk I/O, you should kick off a new
   * thread, or use {@link AsyncTask}.</p>
   *
   * @param intent The Intent supplied to {@link Context#startService}, as given.  This may be null
   * if the service is being restarted after its process has gone away, and it had previously
   * returned anything except {@link #START_STICKY_COMPATIBILITY}.
   * @param flags Additional data about this start request.  Currently either 0, {@link
   * #START_FLAG_REDELIVERY}, or {@link #START_FLAG_RETRY}.
   * @param startId A unique integer representing this specific request to start.  Use with {@link
   * #stopSelfResult(int)}.
   * @return The return value indicates what semantics the system should use for the service's
   * current started state.  It may be one of the constants associated with the {@link
   * #START_CONTINUATION_MASK} bits.
   * @see #stopSelfResult(int)
   */
  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    super.onStartCommand(intent, flags, startId);

    Log.d(TAG,
        "Service Started with intent" + (intent == null ? "Null" : intent.getExtras().toString()));

    googleApiClient.connect();
    return START_STICKY;
  }

  @Override
  public void onDestroy() {
    Log.d(TAG, "Service.onDestroy");
    if (googleApiClient != null && googleApiClient.isConnected()) {
      googleApiClient.disconnect();
    }
  }

  private void saveLocationToDb(Location l) {
    if (Empty.isEmpty(l)) {
      NotificationUtil.showGPSDisabled(this);
      return;
    }

    Log.d(TAG, "Location:" + (l == null ? "Null" : l.toString()));
    Log.d(TAG, "Exact Location:" + l.getLatitude() + "-" + l.getLongitude() + "Provider" + l
        .getProvider());
    try {
      final Position position = new Position(l);
      position.setSalesmanId(salesmanId);
      long id = positionService.savePosition(position);
      position.setId(id);
      instantUpdate = settingService.getSettingValue(ApplicationKeys.SETTING_GPS_ENABLE)
          .equals("1");
      if (instantUpdate) {
        Thread sender = new Thread(() -> {
          if (NetworkUtil.isNetworkAvailable(getApplicationContext())) {
            positionService.sendPosition(position);
          }
        });
        sender.start();
      }
    } catch (Exception ex) {
      Crashlytics.log(Log.ERROR, "Location Service", "Error in saving data into db " + ex.getMessage());
      ex.printStackTrace();
    }
  }

  @Override
  public void onConnected(@Nullable Bundle bundle) {
    Log.d(TAG, "Gplay Connected");

    currentLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
    LocationServices.FusedLocationApi
        .requestLocationUpdates(googleApiClient, createLocationRequest(), this);

    saveLocationToDb(currentLocation);
  }

  @Override
  public void onConnectionSuspended(int i) {
    Log.d(TAG, "Gpaly onConnectionSuspended");
  }

  @Override
  public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    Log.d(TAG, "Gpaly onConnectionFailed");
  }

  protected LocationRequest createLocationRequest() {
    LocationRequest mLocationRequest = new LocationRequest();
    mLocationRequest.setInterval(interval * 1000);
    mLocationRequest.setFastestInterval(interval * 1000);
    mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    mLocationRequest.setSmallestDisplacement(10);
    return mLocationRequest;
  }

  @Override
  public void onLocationChanged(Location location) {
    Log.d(TAG, "New location found");
    saveLocationToDb(location);
  }
}