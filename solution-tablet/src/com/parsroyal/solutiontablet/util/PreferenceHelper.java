package com.parsroyal.solutiontablet.util;

import com.parsroyal.solutiontablet.SolutionTabletApplication;
import com.parsroyal.solutiontablet.data.entity.KeyValue;

public class PreferenceHelper
{

    public static final String FORCE_EXIT = "FORCE_EXIT";
    private static final String LATEST_VERSION = "LATEST_VERSION";
    private static final String UPDATE_URI = "UPDATE_URI";

    public static int getLatestVersion()
    {
        return SolutionTabletApplication.getPreference().getInt(LATEST_VERSION, 0);
    }

    public static void setLatestVersion(int latestVersion)
    {
        SolutionTabletApplication.getPreference().edit().putInt(LATEST_VERSION, latestVersion).apply();
    }

    public static boolean isForceExit()
    {
        return SolutionTabletApplication.getPreference().getBoolean(FORCE_EXIT, false);
    }

    public static void setForceExit(boolean forceExit)
    {
        SolutionTabletApplication.getPreference().edit().putBoolean(FORCE_EXIT, forceExit).apply();
    }

    public static String getUpdateUri()
    {
        return SolutionTabletApplication.getPreference().getString(UPDATE_URI, "");
    }

    public static void setUpdateUri(String uri)
    {
        SolutionTabletApplication.getPreference().edit().putString(UPDATE_URI, uri).apply();
    }

    public static void saveKey(KeyValue entity)
    {
        SolutionTabletApplication.getPreference().edit().putString(entity.getKey(), entity.getValue())
                .apply();
    }

    public static KeyValue retrieveByKey(String settingKey)
    {

        String value = SolutionTabletApplication.getPreference().getString(settingKey, "");
        if (value.equals(""))
        {
            return null;
        }
        return new KeyValue(0L, settingKey, value);
    }
}
