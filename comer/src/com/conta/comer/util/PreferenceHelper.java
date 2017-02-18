package com.conta.comer.util;

import com.conta.comer.CommerApplication;

public class PreferenceHelper
{
    private static final String LATEST_VERSION = "LATEST_VERSION";
    private static final String UPDATE_URI = "UPDATE_URI";

    public static int getLatestVersion()
    {
        return CommerApplication.getPreference().getInt(LATEST_VERSION, 0);
    }

    public static void setLatestVersion(int latestVersion)
    {
        CommerApplication.getPreference().edit().putInt(LATEST_VERSION, latestVersion).apply();
    }

    public static String getUpdateUri()
    {
        return CommerApplication.getPreference().getString(UPDATE_URI, "");
    }

    public static void setUpdateUri(String uri)
    {
        CommerApplication.getPreference().edit().putString(UPDATE_URI, uri).apply();
    }

}
