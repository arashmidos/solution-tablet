package com.parsroyal.solutiontablet;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.parsroyal.solutiontablet.util.TypefaceUtil;

import io.fabric.sdk.android.Fabric;

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

    if (!BuildConfig.DEBUG) {
      Fabric.with(this, new Crashlytics());
    }
    try {
      TypefaceUtil.overrideFont(getApplicationContext(), "SERIF", "fonts/IRANSansMobile.ttf");
    } catch (Exception e) {
      Crashlytics.log(Log.ERROR, "Resource exception", "Error in overriding fonts" + e.getMessage());
      e.printStackTrace();
    }
  }
}
