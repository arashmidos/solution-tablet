package com.parsroyal.solutiontablet.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Base64;
import android.util.Log;
import com.google.gson.Gson;
import com.parsroyal.solutiontablet.SolutionTabletApplication;
import com.parsroyal.solutiontablet.data.dao.KeyValueDao;
import com.parsroyal.solutiontablet.data.dao.impl.KeyValueDaoImpl;
import com.parsroyal.solutiontablet.data.response.UserInfoResponse;
import com.parsroyal.solutiontablet.service.SettingService;
import com.parsroyal.solutiontablet.service.impl.SettingServiceImpl;
import com.parsroyal.solutiontablet.util.constants.ApplicationKeys;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import org.springframework.util.Base64Utils;

/**
 * Created by Mahyar on 6/9/2015.
 */
public class NetworkUtil {

  public static final String TAG = NetworkUtil.class.getSimpleName();

  static public boolean isURLReachable(Context context, String toTestUrl) {
    ConnectivityManager cm = (ConnectivityManager) context
        .getSystemService(Context.CONNECTIVITY_SERVICE);
    NetworkInfo netInfo = cm.getActiveNetworkInfo();
    KeyValueDao keyValueDao = new KeyValueDaoImpl(context);

    String username = keyValueDao.retrieveByKey(ApplicationKeys.SETTING_USERNAME).getValue();
    String password = keyValueDao.retrieveByKey(ApplicationKeys.SETTING_PASSWORD).getValue();

    if (netInfo != null && netInfo.isConnected()) {
      try {
        URL url = new URL(toTestUrl);   // Change to "http://google.com" for www  test.
        HttpURLConnection urlc = (HttpURLConnection) url.openConnection();

        String encoded
            = Base64
            .encodeToString((username + ":" + password).getBytes("UTF-8"), Base64.NO_WRAP);
        //urlc.setRequestProperty("Authorization", "Basic " + encoded);
        urlc.setRequestProperty("Authorization", "Basic " + encoded);
        urlc.setConnectTimeout(20 * 1000);          // 10 s.

        String userCredentials = username + ":" + password;
        String basicAuth = "Basic " + new String(Base64Utils.encode(userCredentials.getBytes()));
        urlc.setRequestProperty("Authorization", basicAuth);

        urlc.connect();
        if (urlc.getResponseCode() == 200) {        // 200 = "OK" code (http connection is fine).
          Log.wtf("Connection", toTestUrl + " Success !");
          return true;
        } else {
          Log.wtf("Connection", toTestUrl + " Failed with responseCode: " + urlc.getResponseCode());
          return false;
        }
      } catch (MalformedURLException e1) {
        return false;
      } catch (IOException e) {
        return false;
      }
    }
    return false;
  }

  public static boolean hasActiveInternetConnection(Context context) {
    if (isNetworkAvailable(context)) {
      try {
        HttpURLConnection urlc = (HttpURLConnection) (new URL("http://www.google.com")
            .openConnection());
        urlc.setRequestProperty("User-Agent", "Test");
        urlc.setRequestProperty("Connection", "close");
        urlc.setConnectTimeout(1500);
        urlc.connect();
        return (urlc.getResponseCode() == 200);
      } catch (IOException e) {
        Log.e(TAG, "Error checking internet connection", e);
      }
    } else {
      Log.d(TAG, "No network available!");
    }
    return false;
  }

  public static boolean isNetworkAvailable(Context context) {
    ConnectivityManager connectivityManager
        = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
    return activeNetworkInfo != null;
  }

  public static UserInfoResponse extractUserInfo(String jwt) {
    String[] split = jwt.split("\\.");

    byte[] body = Base64.decode(split[1], Base64.URL_SAFE);
    String bodyText = "";
    try {
      bodyText = new String(body, "UTF-8");
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    }

    UserInfoResponse userInfoResponse = new Gson().fromJson(bodyText, UserInfoResponse.class);
    return userInfoResponse;
  }

  public static boolean isTokenExpired() {
    SettingService settingService = new SettingServiceImpl(SolutionTabletApplication.getInstance());
    String expire = settingService.getSettingValue(ApplicationKeys.TOKEN_EXPIRE_DATE);
    if (Empty.isEmpty(expire)) {
      return true;
    }
    Date expireDate = new Date(Long.valueOf(expire)*1000);

    return (expireDate.compareTo(new Date()) < 0);
  }
}
