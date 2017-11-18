package com.parsroyal.solutiontablet.service;

import android.Manifest;
import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.location.Location;
import android.os.Binder;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.parsroyal.solutiontablet.R;
import com.parsroyal.solutiontablet.constants.Constants;
import com.parsroyal.solutiontablet.constants.StatusCodes;
import com.parsroyal.solutiontablet.data.entity.Position;
import com.parsroyal.solutiontablet.data.event.ErrorEvent;
import com.parsroyal.solutiontablet.service.impl.PositionServiceImpl;
import com.parsroyal.solutiontablet.ui.MainActivity;
import com.parsroyal.solutiontablet.util.Empty;
import com.parsroyal.solutiontablet.util.GPSUtil;
import com.parsroyal.solutiontablet.util.LocationUtil;
import org.greenrobot.eventbus.EventBus;

/**
 * @author Arash
 *
 *         This class is a long-running service for location updates. When an activity is bound to
 *         this service, frequent location updates are permitted. When the activity is removed from
 *         the foreground, the service promotes itself to a foreground service, and location updates
 *         continue. When the activity comes back to the foreground, the foreground service stops,
 *         and the notification assocaited with that service is removed.
 */
public class LocationUpdatesService extends Service {

  /**
   * The desired interval for location updates. Inexact. Updates may be more or less frequent.
   */
  public static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;
  /**
   * The fastest rate for active location updates. Updates will never be more frequent
   * than this value.
   */
  public static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = UPDATE_INTERVAL_IN_MILLISECONDS;
  private static final String PACKAGE_NAME = "com.parsroyal.solutiontablet.service";
  public static final String ACTION_BROADCAST = PACKAGE_NAME + ".broadcast";
  public static final String EXTRA_LOCATION = PACKAGE_NAME + ".location";
  private static final String TAG = LocationUpdatesService.class.getSimpleName();
  private static final String EXTRA_STARTED_FROM_NOTIFICATION = PACKAGE_NAME +
      ".started_from_notification";
  private static final float MAX_ACCEPTED_DISTANCE_IN_METER = 1000.0f;
  private static final float MIN_ACCEPTED_DISTANCE_IN_METER = 20.0f;
  private static final float MAX_ACCEPTED_ACCURACY_IN_METER = 30.0f;
  private static final float MIN_ACCEPTED_SPEED_IN_MS = 0.9f;
  /**
   * The identifier for the notification displayed for the foreground service.
   */
  private static final int NOTIFICATION_ID = 12345678;
  private final IBinder mBinder = new LocalBinder();
  /**
   * Used to check whether the bound activity has really gone away and not unbound as part of an
   * orientation change. We create a foreground service notification only if the former takes
   * place.
   */
  private boolean changingConfiguration = false;

  private NotificationManager notificationManager;

  /**
   * Contains parameters used by {@link com.google.android.gms.location.FusedLocationProviderApi}.
   */
  private LocationRequest locationRequest;

  /**
   * Provides access to the Fused Location Provider API.
   */
  private FusedLocationProviderClient fusedLocationClient;

  /**
   * Callback for changes in location.
   */
  private LocationCallback locationCallback;

  private Handler serviceHandler;

  /**
   * The current location.
   */
  private Location lastLocation;

  public LocationUpdatesService() {
  }

  @Override
  public void onCreate() {
    try {
      if (!checkPermissions()) {
        EventBus.getDefault().post(new ErrorEvent(StatusCodes.PERMISSION_DENIED));
        return;
      }
      fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

      locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
          super.onLocationResult(locationResult);
          onNewLocation(locationResult.getLastLocation());
        }
      };

      createLocationRequest();
      getLastLocation();

      HandlerThread handlerThread = new HandlerThread(TAG);
      handlerThread.start();
      serviceHandler = new Handler(handlerThread.getLooper());
      notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
      requestLocationUpdates();
    } catch (SecurityException ex) {
      ex.printStackTrace();
      EventBus.getDefault().post(new ErrorEvent(StatusCodes.PERMISSION_DENIED));
    }
  }

  /**
   * Returns the current state of the permissions needed.
   */
  private boolean checkPermissions() {
    return PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(this,
        Manifest.permission.ACCESS_FINE_LOCATION);
  }

  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    Log.i(TAG, "Service started");

    boolean startedFromNotification = false;
    if (Empty.isNotEmpty(intent)) {

      startedFromNotification = intent.getBooleanExtra(EXTRA_STARTED_FROM_NOTIFICATION,
          false);
    }

    // We got here because the user decided to remove location updates from the notification.
    if (startedFromNotification) {
      removeLocationUpdates();
      stopSelf();
    }
    // Tells the system to not try to recreate the service after it has been killed.
    return START_NOT_STICKY;
  }

  @Override
  public void onConfigurationChanged(Configuration newConfig) {
    super.onConfigurationChanged(newConfig);
    changingConfiguration = true;
  }

  @Override
  public IBinder onBind(Intent intent) {
    // Called when a client (MainActivity in case of this sample) comes to the foreground
    // and binds with this service. The service should cease to be a foreground service
    // when that happens.
    Log.i(TAG, "in onBind()");
    stopForeground(true);
    changingConfiguration = false;
    return mBinder;
  }

  @Override
  public void onRebind(Intent intent) {
    // Called when a client (MainActivity in case of this sample) returns to the foreground
    // and binds once again with this service. The service should cease to be a foreground
    // service when that happens.
    Log.i(TAG, "in onRebind()");
    stopForeground(true);
    changingConfiguration = false;
    super.onRebind(intent);
  }

  @Override
  public boolean onUnbind(Intent intent) {
    Log.i(TAG, "Last client unbound from service");

    // Called when the last client (MainActivity in case of this sample) unbinds from this
    // service. If this method is called due to a configuration change in MainActivity, we
    // do nothing. Otherwise, we make this service a foreground service.
    Log.i(TAG, "Starting foreground service");
    startForeground(NOTIFICATION_ID, getNotification());

    return true; // Ensures onRebind() is called when a client re-binds.
  }

  @Override
  public void onDestroy() {
    try {
      if (serviceHandler != null) {
        serviceHandler.removeCallbacksAndMessages(null);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Makes a request for location updates. Note that in this sample we merely log the
   * {@link SecurityException}.
   */

  public void requestLocationUpdates() {
    Log.i(TAG, "Requesting location updates");
    GPSUtil.setRequestingLocationUpdates(this, true);

    startService(new Intent(getApplicationContext(), LocationUpdatesService.class));
    if (fusedLocationClient == null) {
      onCreate();
      return;
    }
    try {
      fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback,
          Looper.myLooper());
    } catch (SecurityException unlikely) {
      GPSUtil.setRequestingLocationUpdates(this, false);
      Log.e(TAG, "Lost location permission. Could not request updates. " + unlikely);
    }
  }

  /**
   * Removes location updates. Note that in this sample we merely log the
   * {@link SecurityException}.
   */
  public void removeLocationUpdates() {
    Log.i(TAG, "Removing location updates");
    try {
      fusedLocationClient.removeLocationUpdates(locationCallback);
      GPSUtil.setRequestingLocationUpdates(this, false);
      stopSelf();
    } catch (SecurityException unlikely) {
      GPSUtil.setRequestingLocationUpdates(this, true);
      Log.e(TAG, "Lost location permission. Could not remove updates. " + unlikely);
    } catch (Exception e) {
      e.printStackTrace();
      Log.e(TAG, "Location service couldn't finish successfully!");
    }
  }

  /**
   * Returns the {@link NotificationCompat} used as part of the foreground service.
   */
  private Notification getNotification() {
    CharSequence text = getString(
        R.string.notification_message_gps_service_active);

    // The PendingIntent to launch activity.
    PendingIntent activityPendingIntent = PendingIntent.getActivity(this, 0,
        new Intent(this, MainActivity.class), 0);

    return new NotificationCompat.Builder(this)
        .addAction(R.drawable.ic_launch, getString(R.string.launch_activity), activityPendingIntent)
        .setContentText(text)
        .setOngoing(true)
        .setPriority(Notification.PRIORITY_HIGH)
        .setSmallIcon(R.drawable.ic_launcher)
        .setTicker(text)
        .setWhen(System.currentTimeMillis()).build();
  }

  private void getLastLocation() {

    Position lastPosition = new PositionServiceImpl(getApplicationContext()).getLastPosition();
    if (Empty.isNotEmpty(lastPosition)) {
      lastLocation = new Location("dummyprovider");
      lastLocation.setLatitude(lastPosition.getLatitude());
      lastLocation.setLongitude(lastPosition.getLongitude());
      lastLocation.setSpeed(lastPosition.getSpeed());
      lastLocation.setAccuracy(lastPosition.getAccuracy());
      lastLocation.setTime(lastPosition.getDate().getTime());
    }
  }

  private void onNewLocation(Location location) {
    Log.i(TAG, "New location in service: " + location);

    if (isAccepted(location)) {
      Log.i(TAG, "location accepted");

      Intent intent = new Intent(this, SaveLocationService.class);
      intent.putExtra(EXTRA_LOCATION, location);

      if (Empty.isEmpty(lastLocation)) {
        intent.putExtra(Constants.FIRST_POSITION, true);
      }
      this.lastLocation = location;

      startService(intent);
    }

    // Update notification content if running as a foreground service.
    if (serviceIsRunningInForeground(this)) {
      notificationManager.notify(NOTIFICATION_ID, getNotification());
    }
  }

  private boolean isAccepted(Location location) {
    //Accept first position what ever it is
    if (Empty.isEmpty(lastLocation) && Empty.isNotEmpty(location)
        && location.getLongitude() != 0.0) {
      return true;
    }
    if ((Empty.isEmpty(location) || location.getAccuracy() > MAX_ACCEPTED_ACCURACY_IN_METER
        || location.getSpeed() < MIN_ACCEPTED_SPEED_IN_MS)) {
      return false;
    }

    if (Empty.isNotEmpty(lastLocation)) {

      float distance = LocationUtil.distanceBetween(lastLocation, location);
      if (distance > MAX_ACCEPTED_DISTANCE_IN_METER) {
        long lastTime = lastLocation.getTime();
        long currentTime = location.getTime();

        if (currentTime - lastTime < 30 * 1000) {
          return false;
        }
      }

      if (distance < lastLocation.getAccuracy() + location.getAccuracy()) {
        //Its probabely in the circle of past location
        return false;
      }
    }

    return true;
  }

  /**
   * Sets the location request parameters.
   */
  private void createLocationRequest() {
    locationRequest = new LocationRequest();
    locationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
    locationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
    locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
  }

  /**
   * Returns true if this is a foreground service.
   *
   * @param context The {@link Context}.
   */
  public boolean serviceIsRunningInForeground(Context context) {
    ActivityManager manager = (ActivityManager) context.getSystemService(
        Context.ACTIVITY_SERVICE);
    for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(
        Integer.MAX_VALUE)) {
      if (getClass().getName().equals(service.service.getClassName())) {
        if (service.foreground) {
          return true;
        }
      }
    }
    return false;
  }

  /**
   * Class used for the client Binder.  Since this service runs in the same process as its
   * clients, we don't need to deal with IPC.
   */
  public class LocalBinder extends Binder {

    public LocationUpdatesService getService() {
      return LocationUpdatesService.this;
    }
  }
}
