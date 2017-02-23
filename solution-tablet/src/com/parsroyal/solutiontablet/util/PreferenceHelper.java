package com.parsroyal.solutiontablet.util;

import com.parsroyal.solutiontablet.SolutionTabletApplication;

public class PreferenceHelper
{
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

    public static String getUpdateUri()
    {
        return SolutionTabletApplication.getPreference().getString(UPDATE_URI, "");
    }

    public static void setUpdateUri(String uri)
    {
        SolutionTabletApplication.getPreference().edit().putString(UPDATE_URI, uri).apply();
    }

}
