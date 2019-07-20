package com.parsroyal.solutiontablet;

import android.Manifest.permission;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.location.Location;
import android.preference.PreferenceManager;
import androidx.multidex.MultiDex;
import androidx.multidex.MultiDexApplication;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AppCompatDelegate;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import com.crashlytics.android.Crashlytics;
import com.github.anrwatchdog.ANRWatchDog;
import com.instacart.library.truetime.TrueTimeRx;
import com.parsroyal.solutiontablet.constants.Authority;
import com.parsroyal.solutiontablet.constants.Constants;
import com.parsroyal.solutiontablet.data.entity.Position;
import com.parsroyal.solutiontablet.util.Empty;
import com.parsroyal.solutiontablet.util.PreferenceHelper;
import io.fabric.sdk.android.Fabric;
import io.github.inflationx.calligraphy3.CalligraphyConfig.Builder;
import io.github.inflationx.calligraphy3.CalligraphyInterceptor;
import io.github.inflationx.viewpump.ViewPump;
import io.reactivex.schedulers.Schedulers;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import timber.log.Timber;
import timber.log.Timber.DebugTree;

/**
 * Created by Arash on 2018-01-25
 */
public class SolutionTabletApplication extends MultiDexApplication {

  public static SolutionTabletApplication sInstance;

  public static SharedPreferences sPreference;
  private ArrayList<String> authorities = new ArrayList<>();
  private Location lastKnownLocation;
  private Position lastSavedPosition;

  public static SolutionTabletApplication getInstance() {
    return sInstance;
  }

  public static SharedPreferences getPreference() {
    if (sPreference == null) {
      sPreference = PreferenceManager.getDefaultSharedPreferences(
          sInstance.getApplicationContext());
    }

    return sPreference;
  }

  @Override
  public void onCreate() {
    super.onCreate();
    sInstance = this;
    Fabric.with(this, new Crashlytics());
    if (!BuildConfig.DEBUG) {
    } else {
      Timber.plant(new DebugTree());
    }

    new ANRWatchDog(10_000).setANRListener(error -> {
      Timber.e(error);
      Crashlytics.log(error.toString());
      Crashlytics.logException(error);
    }).setIgnoreDebugger(true).start();
//    Pushe.initialize(this, true);
    MultiDex.install(this);

    ViewPump.init(ViewPump.builder()
        .addInterceptor(new CalligraphyInterceptor(new
            Builder()
            .setDefaultFontPath("fonts/IRANSansMobile.ttf")
            .setFontAttrId(R.attr.fontPath)
            .build()))
        .build());

    AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

    setLanguage();

    reSyncTrueTime();

    Timber.d(getInstanceId());
  }

  @SuppressLint("CheckResult")
  public void reSyncTrueTime() {

//    new Thread(() -> {
    try {

      TrueTimeRx.build().withSharedPreferencesCache(this).withServerResponseDelayMax(2000)
          .withConnectionTimeout(60_000)
          .initializeRx("time.apple.com")
          .subscribeOn(Schedulers.io())
          .subscribe(date -> {
            Timber.d("TrueTime was initialized and we have a time:%s ", date);
          }, throwable -> {
          });
    } catch (Exception ex) {
      ex.printStackTrace();
      Timber.e("Network time not initialized");
    }
//    }).start();
  }

  public void setLanguage() {
    Locale locale = new Locale(Constants.DEFAULT_LANGUAGE);

    Resources res = getResources();
    DisplayMetrics dm = res.getDisplayMetrics();
    Configuration conf = res.getConfiguration();
    conf.locale = locale;
    res.updateConfiguration(conf, dm);
  }

  @SuppressLint("HardwareIds")
  public String getInstanceId() {
    TelephonyManager telephonyManager = (TelephonyManager) getSystemService(
        Context.TELEPHONY_SERVICE);
    if (ActivityCompat.checkSelfPermission(this, permission.READ_PHONE_STATE)
        != PackageManager.PERMISSION_GRANTED) {
      return "";
    }

    return telephonyManager != null ? telephonyManager.getDeviceId() : "";

//    return Secure.getString(getContentResolver(), Secure.ANDROID_ID);
  }

  public void loadAuthorities() {
    setAuthorities(PreferenceHelper.getAuthorities());
  }

  public void clearAuthorities() {
    setAuthorities(new HashSet<>());
  }

  public Location getLastKnownLocation() {
    return lastKnownLocation;
  }

  public void setLastKnownLocation(Location lastKnownLocation) {
    this.lastKnownLocation = lastKnownLocation;
  }

  public Position getLastSavedPosition() {
    return lastSavedPosition;
  }

  public void setLastSavedPosition(Position lastSavedPosition) {
    this.lastSavedPosition = lastSavedPosition;
  }

  private void setAuthorities(Set<String> authorities) {
    if (Empty.isNotEmpty(authorities)) {
      this.authorities.clear();
      this.authorities.addAll(authorities);
    }
  }

  public boolean hasAccess(Authority authority) {

    return BuildConfig.DEBUG || authorities.contains(String.valueOf(authority.getId()));
  }

  public Date getTrueTime() {
    try {
      if (TrueTimeRx.isInitialized()) {
        return TrueTimeRx.now();
      } else {
        reSyncTrueTime();
      }
    } catch (Exception ex) {
      ex.printStackTrace();
    }
    return new Date();
  }

}
