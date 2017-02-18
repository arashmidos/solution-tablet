package com.conta.comer;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.conta.comer.util.TypefaceUtil;
import com.crashlytics.android.Crashlytics;

import io.fabric.sdk.android.Fabric;

/**
 * Created by Mahyar on 7/8/2015.
 */
public class CommerApplication extends Application
{
    public static CommerApplication sInstance;

    public static SharedPreferences sPreference;

    public static CommerApplication getInstance()
    {
        return sInstance;
    }

    public static SharedPreferences getPreference()
    {
        if (sPreference == null)
            sPreference = PreferenceManager.getDefaultSharedPreferences(
                    sInstance.getApplicationContext());

        return sPreference;
    }

    @Override
    public void onCreate()
    {
        super.onCreate();
        sInstance = this;

        if (BuildConfig.DEBUG)
        {
            Fabric.with(this, new Crashlytics());
        }
        try
        {
            TypefaceUtil.overrideFont(getApplicationContext(), "SERIF", "fonts/IRANSansMobile.ttf");
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
