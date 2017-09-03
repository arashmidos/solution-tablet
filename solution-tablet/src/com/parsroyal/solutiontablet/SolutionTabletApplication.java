package com.parsroyal.solutiontablet;

import android.app.Application;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatDelegate;
import android.util.DisplayMetrics;
import com.crashlytics.android.Crashlytics;
import com.parsroyal.solutiontablet.constants.Constants;
import io.fabric.sdk.android.Fabric;
import java.util.Locale;
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
        .setDefaultFontPath("fonts/IRANSansMobile.ttf")
        .setFontAttrId(R.attr.fontPath)
        .build());

    AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

setLanguage();
  }

  public void setLanguage()
  {
    String systemLocale = getResources().getConfiguration().locale.getLanguage();

    Locale locale = new Locale(Constants.DEFAULT_LANGUAGE);
    if (systemLocale.equalsIgnoreCase("it"))
    {
      locale = new Locale("it");
    }

    Resources res = getResources();
    DisplayMetrics dm = res.getDisplayMetrics();
    Configuration conf = res.getConfiguration();
    conf.locale = locale;
    res.updateConfiguration(conf, dm);
  }

}
