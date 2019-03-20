package com.parsroyal.storemanagement.util;

import static com.parsroyal.storemanagement.util.constants.ApplicationKeys.TOKEN;

import android.content.SharedPreferences;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.parsroyal.storemanagement.SolutionTabletApplication;
import com.parsroyal.storemanagement.data.entity.KeyValue;
import com.parsroyal.storemanagement.data.entity.Stock;
import com.parsroyal.storemanagement.util.constants.ApplicationKeys;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;

public class PreferenceHelper {

  private static final String FORCE_EXIT = "FORCE_EXIT";
  private static final String LATEST_VERSION = "LATEST_VERSION";
  private static final String UPDATE_URI = "UPDATE_URI";
  private static final String AUTHORITIES = "setting.authorities";
  private static final String DEF_NAVIGATOR = "DEF_NAVIGATOR";
  private static final String BADGER = "BADGER";
  private static final String STOCK_LIST = "STOCK_LIST";
  private static final String SELECTED_STOCK = "SELECTED_STOCK";

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

  public static Long getStockKey() {
    String asnSerial = SolutionTabletApplication.getPreference()
        .getString(ApplicationKeys.SETTING_STOCK_ID, "");
    if (Empty.isEmpty(asnSerial) || "null".equals(asnSerial)) {
      return null;
    }
    return Long.valueOf(asnSerial);
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

  public static ArrayList<Stock> getStockList() {

    ArrayList<Stock> stocks = null;
    try {
      Gson gson = new Gson();
      String userToString = SolutionTabletApplication.getPreference().getString(STOCK_LIST, "");
      Type listType = new TypeToken<ArrayList<Stock>>() {
      }.getType();
      stocks = new Gson().fromJson(userToString, listType);
    } catch (JsonSyntaxException e) {
      e.printStackTrace();
      return null;
    }
    return stocks;
  }


  public static void saveStockList(List<Stock> stocks) {
    if (Empty.isEmpty(stocks)) {
      return;
    }
    Gson gson = new Gson();
    String jsonQueue = gson.toJson(stocks);
    SolutionTabletApplication.getPreference().edit().putString(STOCK_LIST, jsonQueue).apply();

  }

  public static String getToken() {
    return SolutionTabletApplication.getPreference().getString(TOKEN, "");
  }

  public static void setToken(String token) {
    SolutionTabletApplication.getPreference().edit().putString(TOKEN, token).apply();
  }

  public static int getSelectedStock() {
    return SolutionTabletApplication.getPreference().getInt(SELECTED_STOCK, 0);
  }

  public static void setSelectedStock(int selectedStock) {
    SolutionTabletApplication.getPreference().edit().putInt(SELECTED_STOCK, selectedStock).apply();
  }

  public static Long getSelectedStockAsn() {
    ArrayList<Stock> stocksList = getStockList();
    int selectedStock = getSelectedStock();
    if (Empty.isEmpty(stocksList) || Empty.isEmpty(stocksList.get(selectedStock))) {
      return null;
    }

    return stocksList.get(selectedStock).getAsn();
  }

  public static String getSelectedStockName() {
    ArrayList<Stock> stocksList = getStockList();
    int selectedStock = getSelectedStock();
    if (Empty.isEmpty(stocksList) || Empty.isEmpty(stocksList.get(selectedStock))) {
      return "<نامشخص>";
    }

    return stocksList.get(selectedStock).getNameSTK();
  }
}
