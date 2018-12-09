package com.parsroyal.solutiontablet.util;

import android.content.SharedPreferences;
import com.parsroyal.solutiontablet.SolutionTabletApplication;
import com.parsroyal.solutiontablet.data.entity.KeyValue;
import java.util.Set;
import java.util.SortedSet;

public class PreferenceHelper {

  public static final String FORCE_EXIT = "FORCE_EXIT";
  private static final String LATEST_VERSION = "LATEST_VERSION";
  private static final String UPDATE_URI = "UPDATE_URI";
  private static final String AUTHORITIES = "setting.authorities";
  private static final String DEF_NAVIGATOR = "DEF_NAVIGATOR";

  public static int getLatestVersion() {
    return SolutionTabletApplication.getPreference().getInt(LATEST_VERSION, 0);
  }

  public static void setLatestVersion(int latestVersion) {
    SolutionTabletApplication.getPreference().edit().putInt(LATEST_VERSION, latestVersion).apply();
  }

  public static String getDefaultNavigator() {
    return SolutionTabletApplication.getPreference().getString(DEF_NAVIGATOR, "google");
  }

  public static void setDefaultNavigator(String defNav) {
    SolutionTabletApplication.getPreference().edit().putString(DEF_NAVIGATOR, defNav).apply();
  }

  public static boolean isForceExit() {
    return SolutionTabletApplication.getPreference().getBoolean(FORCE_EXIT, false);
  }

  public static void setForceExit(boolean forceExit) {
    SolutionTabletApplication.getPreference().edit().putBoolean(FORCE_EXIT, forceExit).apply();
  }

  public static String getUpdateUri() {
    return SolutionTabletApplication.getPreference().getString(UPDATE_URI, "");
  }

  public static void setUpdateUri(String uri) {
    SolutionTabletApplication.getPreference().edit().putString(UPDATE_URI, uri).apply();
  }

  public static void saveKey(KeyValue entity) {
    SolutionTabletApplication.getPreference().edit().putString(entity.getKey(), entity.getValue())
        .apply();
  }

  public static KeyValue retrieveByKey(String settingKey) {

    String value = SolutionTabletApplication.getPreference().getString(settingKey, "");
    if (value.equals("")) {
      return null;
    }
    return new KeyValue(0L, settingKey, value);
  }

  public static void clearAllKeys() {
    SharedPreferences.Editor editor = SolutionTabletApplication.getPreference().edit();
    editor.clear();
    editor.apply();
  }

  public static Set<String> getAuthorities() {
    return SolutionTabletApplication.getPreference().getStringSet(AUTHORITIES, null);
  }

  public static void setAuthorities(SortedSet<String> authorities) {
    SolutionTabletApplication.getPreference().edit().putStringSet(AUTHORITIES, authorities)
        .apply();
  }
}
