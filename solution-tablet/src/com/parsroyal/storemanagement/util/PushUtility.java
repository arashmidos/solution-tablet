package com.parsroyal.storemanagement.util;

import com.google.gson.Gson;
import com.parsroyal.storemanagement.data.model.PushNotification;
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
