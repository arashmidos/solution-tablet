package com.conta.comer;

import android.app.Application;

import com.conta.comer.util.TypefaceUtil;
import com.crashlytics.android.Crashlytics;

import io.fabric.sdk.android.Fabric;

/**
 * Created by Mahyar on 7/8/2015.
 */
public class CommerApplication extends Application
{
    @Override
    public void onCreate()
    {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        try
        {
            TypefaceUtil.overrideFont(getApplicationContext(), "SERIF", "fonts/IRANSansMobile.ttf");
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
