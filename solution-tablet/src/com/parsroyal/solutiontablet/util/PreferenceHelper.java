package com.parsroyal.solutiontablet.util;

import android.content.SharedPreferences;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.parsroyal.solutiontablet.BuildConfig;
import com.parsroyal.solutiontablet.SolutionTabletApplication;
import com.parsroyal.solutiontablet.constants.Constants;
import com.parsroyal.solutiontablet.data.entity.KeyValue;
import com.parsroyal.solutiontablet.util.constants.ApplicationKeys;
import com.parsroyal.solutiontablet.vrp.model.OptimizedRouteResponse;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Set;
import java.util.SortedSet;

public class PreferenceHelper {

  private static final String FORCE_EXIT = "FORCE_EXIT";
  private static final String LATEST_VERSION = "LATEST_VERSION";
  private static final String UPDATE_URI = "UPDATE_URI";
  private static final String AUTHORITIES = "setting.authorities";
  private static final String OPTIMIZED_ROUTE = "OPTIMIZED_ROUTE";
  private static final String DEF_NAVIGATOR = "DEF_NAVIGATOR";
  private static final String BADGER = "BADGER";

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

  public static int getBadger() {
    return SolutionTabletApplication.getPreference().getInt(BADGER, 0);
  }

  public static void setBadger(int badger) {
    SolutionTabletApplication.getPreference().edit().putInt(BADGER, badger).apply();
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

  public static boolean isDistributor() {
    String type = SolutionTabletApplication.getPreference()
        .getString(ApplicationKeys.SETTING_SALE_TYPE, "");
    return type.equals(ApplicationKeys.SALE_DISTRIBUTER);
  }

  public static boolean isVisitor() {
    String type = SolutionTabletApplication.getPreference()
        .getString(ApplicationKeys.SETTING_SALE_TYPE, "");
    return type.equals(ApplicationKeys.SALE_COLD);
  }

  public static String getSaleType() {
    return SolutionTabletApplication.getPreference()
        .getString(ApplicationKeys.SETTING_SALE_TYPE, "");
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

  public static Long getSalesmanId() {
    String salesmanId = SolutionTabletApplication.getPreference()
        .getString(ApplicationKeys.SALESMAN_ID, "");
    return Empty.isEmpty(salesmanId) ? null : Long.parseLong(salesmanId);
  }

  public static void setOptimizedRoute(long visitLineBackendId, OptimizedRouteResponse result) {

    result.setVisitlineBackendId(visitLineBackendId);
    Set<OptimizedRouteResponse> routeSet = getOptimizedRoutes();

    routeSet.add(result);
    Gson gson = new Gson();
    String json = gson.toJson(routeSet);
    SolutionTabletApplication.getPreference().edit().putString(OPTIMIZED_ROUTE, json).apply();
  }

  public static Set<OptimizedRouteResponse> getOptimizedRoutes() {

    String json = SolutionTabletApplication.getPreference().getString(OPTIMIZED_ROUTE, "");

    Type type = new TypeToken<Set<OptimizedRouteResponse>>() {
    }.getType();
    Gson gson = new Gson();

    Set<OptimizedRouteResponse> routes = gson.fromJson(json, type);
    return Empty.isEmpty(routes) ? new HashSet<>() : routes;
  }

  public static OptimizedRouteResponse getOptimizedRoute(long visitlineBackendId) {
    Set<OptimizedRouteResponse> routes = getOptimizedRoutes();
    for (OptimizedRouteResponse orr : routes) {
      if (orr.getVisitlineBackendId() == visitlineBackendId) {
        return orr;
      }
    }
    return null;
  }

  public static void clearRoutes() {
    SolutionTabletApplication.getPreference().edit().putString(OPTIMIZED_ROUTE, "").apply();
  }

  public static boolean distanceServiceEnabled() {
    if (BuildConfig.DEBUG) {
      return false;
    }
    KeyValue keyValue = retrieveByKey(ApplicationKeys.SETTING_CALCULATE_DISTANCE_ENABLE);
    if (keyValue == null) {
      return false;
    }
    return Boolean.valueOf(keyValue.getValue());

  }

  public static float getAllowedDistance() {
    String distance = SolutionTabletApplication.getPreference()
        .getString(ApplicationKeys.SETTING_DISTANCE_CUSTOMER_VALUE, "");

    try {
      return
          Empty.isEmpty(distance) || "null".equals(distance) ? Constants.MAX_DISTANCE
              : Float.valueOf(distance);
    } catch (NumberFormatException ex) {
      ex.printStackTrace();
      return Constants.MAX_DISTANCE;
    }
  }

  public static boolean isDistributorPhoneConfirmEnabled() {
    String result = SolutionTabletApplication.getPreference()
        .getString(ApplicationKeys.SETTING_CHECK_DISTRIBUTOR_PHONE_CONFIRM_ENABLE, "");
    if (Empty.isEmpty(result)) {
      return false;
    }
    try {
      return Boolean.valueOf(result);
    } catch (Exception ex) {
      return false;
    }
  }

  public static boolean smsEnabled() {
    String result = SolutionTabletApplication.getPreference()
        .getString(ApplicationKeys.SETTING_CHECK_SMS_CONFIRM_ENABLE, "");
    if (Empty.isEmpty(result)) {
      return false;
    }
    try {
      return Boolean.valueOf(result);
    } catch (Exception ex) {
      return false;
    }
  }

  public static boolean isCreditEnabled() {

    String result = SolutionTabletApplication.getPreference()
        .getString(ApplicationKeys.SETTING_CHECK_CREDIT_ENABLE, "");
    if (Empty.isEmpty(result)) {
      return false;
    }
    try {
      return Boolean.valueOf(result);
    } catch (Exception ex) {
      return false;
    }
  }
}
