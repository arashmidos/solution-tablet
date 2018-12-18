package com.parsroyal.solutiontablet.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import com.parsroyal.solutiontablet.SolutionTabletApplication;
import com.parsroyal.solutiontablet.data.dao.KeyValueDao;
import com.parsroyal.solutiontablet.data.dao.impl.KeyValueDaoImpl;
import com.parsroyal.solutiontablet.data.entity.KeyValue;
import com.parsroyal.solutiontablet.data.entity.Position;
import com.parsroyal.solutiontablet.data.event.GPSEvent;
import com.parsroyal.solutiontablet.service.impl.PositionServiceImpl;
import com.parsroyal.solutiontablet.util.Empty;
import com.parsroyal.solutiontablet.util.GPSUtil;
import com.parsroyal.solutiontablet.util.LocationUtil;
import com.parsroyal.solutiontablet.util.constants.ApplicationKeys;
import org.greenrobot.eventbus.EventBus;

public class SaveLocationService extends IntentService {

  public static final String TAG = SaveLocationService.class.getSimpleName();

  private PositionService positionService;
  private KeyValueDao keyValueDao;

  /**
   * Creates an IntentService.  Invoked by your subclass's constructor.
   */
  public SaveLocationService() {
    super("Save Location Service");
    positionService = new PositionServiceImpl(this);
    keyValueDao = new KeyValueDaoImpl();
  }

  @Override
  public void onCreate() {
    super.onCreate();
  }


  @Override
  public IBinder onBind(Intent intent) {
    return null;
  }

  @Override
  protected void onHandleIntent(@Nullable Intent intent) {

    if (Empty.isEmpty(intent)) {
      return;
    }

//    boolean firstPosition = intent.getBooleanExtra(Constants.FIRST_POSITION, false);
    long salesmanId = 0;
//    if (!firstPosition) {
    //If users clears data, new user, or not entered correct information
    KeyValue salesmanIdKeyValue = keyValueDao.retrieveByKey(ApplicationKeys.SALESMAN_ID);
    if (Empty.isEmpty(salesmanIdKeyValue)) {
      return;
    }

    salesmanId = Long.parseLong(salesmanIdKeyValue.getValue());
//    }

    Position position;
    Location location = intent.getParcelableExtra(LocationUpdatesService.EXTRA_LOCATION);
    Log.i(TAG, "Saving location " + location);
    if (location == null) {
      position = (Position) intent.getSerializableExtra(LocationUpdatesService.EXTRA_POSITION);
      if (position == null) {
        return;
      }
    } else {
      position = new Position(location);
    }

    try {
      position.setMockLocation(GPSUtil.isLocationMock(this, location) ? 1 : 0);
    } catch (Exception ex) {
      ex.printStackTrace();
    }

    position.setSalesmanId(salesmanId);
    Location lastPosition = SolutionTabletApplication.getInstance().getLastKnownLocation();
    if (lastPosition != null) {
      float distanceInMeter = LocationUtil.distanceBetween(
          position.getLatitude(), position.getLongitude(),
          lastPosition.getLatitude(), lastPosition.getLongitude());
      position.setDistanceInMeter(distanceInMeter);
    } else {
      position.setDistanceInMeter(0);
    }
    positionService.savePosition(position);
    SolutionTabletApplication.getInstance().setLastSavedPosition(position);
    EventBus.getDefault().post(new GPSEvent(location));

    Intent service = new Intent(this, SendLocationService.class);
    startService(service);
  }

  /**
   * Called by the system every time a client explicitly starts the service by calling {@link
   * Context#startService}, providing the arguments it supplied and a unique integer token
   * representing the start request.  Do not call this method directly.
   * <p>
   * <p>For backwards compatibility, the default implementation calls
   * {@link #onStart} and returns either {@link #START_STICKY} or {@link
   * #START_STICKY_COMPATIBILITY}.
   * <p>
   * <p>If you need your application to run on platform versions prior to API
   * level 5, you can use the following model to handle the older {@link #onStart} callback in that
   * case.  The <code>handleCommand</code> method is implemented by you as appropriate:
   * <p>
   * {@sample development/samples/ApiDemos/src/com/example/android/apis/app/ForegroundService.java
   * start_compatibility}
   * <p>
   * <p class="caution">Note that the system calls this on your
   * service's main thread.  A service's main thread is the same thread where UI operations take
   * place for Activities running in the same process.  You should always avoid stalling the main
   * thread's event loop.  When doing long-running operations, network calls, or heavy disk I/O, you
   * should kick off a new thread, or use {@link AsyncTask}.</p>
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

    return START_STICKY;
  }
}