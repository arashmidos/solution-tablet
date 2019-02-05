package com.parsroyal.solutiontablet.pushe;

import co.ronash.pushe.PusheListenerService;
import com.parsroyal.solutiontablet.data.model.PushNotification;
import com.parsroyal.solutiontablet.util.PushUtility;
import org.json.JSONObject;
import timber.log.Timber;

public class MyPushListener extends PusheListenerService {

  @Override
  public void onMessageReceived(JSONObject customContent, JSONObject pushMessage) {
    Timber.tag("Pushee").d("Custom jsonn Message: %s", customContent.toString());

    try {

      PushNotification push = PushUtility.deserializeJsonFromPushe(customContent);
      if (push != null) {
        //Do what you want
      }
    } catch (Exception e) {
      e.printStackTrace();
    }

    // Your Code
  }
}