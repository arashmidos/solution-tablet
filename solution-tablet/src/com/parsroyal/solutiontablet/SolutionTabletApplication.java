package com.parsroyal.solutiontablet;

import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.preference.PreferenceManager;
import android.provider.Settings.Secure;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;
import android.support.v7.app.AppCompatDelegate;
import android.util.DisplayMetrics;
import android.util.Log;
import co.ronash.pushe.Pushe;
import com.crashlytics.android.Crashlytics;
import com.instacart.library.truetime.TrueTimeRx;
import com.parsroyal.solutiontablet.biz.impl.RestServiceImpl;
import com.parsroyal.solutiontablet.constants.Authority;
import com.parsroyal.solutiontablet.constants.Constants;
import com.parsroyal.solutiontablet.util.PreferenceHelper;
import io.fabric.sdk.android.Fabric;
import io.github.inflationx.calligraphy3.CalligraphyConfig.Builder;
import io.github.inflationx.calligraphy3.CalligraphyInterceptor;
import io.github.inflationx.viewpump.ViewPump;
import io.reactivex.schedulers.Schedulers;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Set;

/**
 * Created by Arash on 2018-01-25
 */
public class SolutionTabletApplication extends MultiDexApplication {

  public static SolutionTabletApplication sInstance;

  public static SharedPreferences sPreference;
  private ArrayList<String> authorities = new ArrayList<>();

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

  public ArrayList<String> getAuthorities() {
    return authorities;
  }

  private void setAuthorities(Set<String> authorities) {
    this.authorities.clear();
    this.authorities.addAll(authorities);
  }

  public boolean hasAccess(Authority authority) {
    return authorities.contains(String.valueOf(authority.getId()));
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
    return null;
  }

  @Override
  public void onCreate() {
    super.onCreate();
    sInstance = this;
    if (!BuildConfig.DEBUG) {
      Fabric.with(this, new Crashlytics());
    }

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

    try {
      Pushe.initialize(this, true);
      Log.d("Pushe", Pushe.getPusheId(this));
      new RestServiceImpl().updatePusheId(this, Pushe.getPusheId(this), "GCMToken");
    } catch (Exception ignore) {

    }
//    Log.d("DebugDB", "***>>> DB Address:"+DebugDB.getAddressLog());
  }

  private void reSyncTrueTime() {

//    new Thread(() -> {
    try {

      TrueTimeRx.build().withSharedPreferencesCache(this)
          .initializeRx("time.google.com")
          .subscribeOn(Schedulers.io())
          .subscribe(date -> {
                Log.d("Network Time", "TrueTime was initialized and we have a time: " + date);
              },
              Throwable::printStackTrace);
    } catch (Exception ex) {
      ex.printStackTrace();
      Log.d("Network Time", " not initialized");
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

  public String getInstanceId() {
    return Secure.getString(getContentResolver(), Secure.ANDROID_ID);
  }

  public void loadAuthorities() {
    setAuthorities(PreferenceHelper.getAuthorities());
  }
}
