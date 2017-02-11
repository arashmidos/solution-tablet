package com.conta.comer;

import android.app.Application;

import com.conta.comer.util.BugSender;
import com.conta.comer.util.TypefaceUtil;

import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;
import org.acra.ACRA;
import org.acra.annotation.ReportsCrashes;

/**
 * Created by Mahyar on 7/8/2015.
 */
@ReportsCrashes(formUri = "")
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
            initACRABugReporter();
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void initACRABugReporter()
    {
        ACRA.init(this);
        BugSender bugSender = new BugSender();
        bugSender.setContext(this);
        ACRA.getErrorReporter().setReportSender(bugSender);
    }

}
