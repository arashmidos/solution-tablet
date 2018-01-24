package com.parsroyal.solutiontablet;

import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;
import android.support.v7.app.AppCompatDelegate;
import android.util.DisplayMetrics;
import android.util.Log;
import com.crashlytics.android.Crashlytics;
import com.instacart.library.truetime.TrueTime;
import com.parsroyal.solutiontablet.constants.Constants;
import io.fabric.sdk.android.Fabric;
import java.io.IOException;
import java.util.Locale;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

/**
 * Created by Mahyar on 7/8/2015.
 */
public class SolutionTabletApplication extends MultiDexApplication {

  public static SolutionTabletApplication sInstance;

  public static SharedPreferences sPreference;

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

    if (!BuildConfig.DEBUG) {
      Fabric.with(this, new Crashlytics());
    }

    MultiDex.install(this);
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
          .setDefaultFontPath("fonts/IRANSansMobile.ttf")
          .setFontAttrId(R.attr.fontPath)
          .build());
    }

    AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

    setLanguage();

    new Thread(() -> {
      boolean syncNetworkTime = false;
      while (!syncNetworkTime) {
        try {
          TrueTime.build().withServerResponseDelayMax(500).initialize();
          syncNetworkTime = true;
          Log.i("Network Time", "**Synced with network");
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }).start();
  }

  public void setLanguage() {
    Locale locale = new Locale(Constants.DEFAULT_LANGUAGE);

    Resources res = getResources();
    DisplayMetrics dm = res.getDisplayMetrics();
    Configuration conf = res.getConfiguration();
    conf.locale = locale;
    res.updateConfiguration(conf, dm);
  }
}
