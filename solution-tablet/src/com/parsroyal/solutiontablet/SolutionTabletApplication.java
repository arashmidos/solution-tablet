package com.parsroyal.solutiontablet;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatDelegate;
import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

/**
 * Created by Mahyar on 7/8/2015.
 */
public class SolutionTabletApplication extends Application {

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

    Fabric.with(this, new Crashlytics());

    CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
        .setDefaultFontPath("fonts/IRANSansMobile2.ttf")
        .setFontAttrId(R.attr.fontPath)
        .build());

    AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
  }
}
