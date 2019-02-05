package com.parsroyal.solutiontablet.util;

import android.util.Log;
import com.google.gson.Gson;
import com.parsroyal.solutiontablet.data.model.PushNotification;
import org.json.JSONObject;

public class PushUtility {

  public static PushNotification deserializeJsonFromPushe(JSONObject object) {
    if (object == null || object.length() == 0) {
      return null;
    }
    PushNotification pushNotification = new Gson()
        .fromJson(object.toString(), PushNotification.class);
    return pushNotification;
  }
}
