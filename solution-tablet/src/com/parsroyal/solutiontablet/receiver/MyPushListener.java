package com.parsroyal.solutiontablet.receiver;

import co.ronash.pushe.PusheListenerService;
import org.json.JSONObject;

/**
 * Created by shkbhbb on 10/20/18.
 */

public class MyPushListener extends PusheListenerService {

  @Override
  public void onMessageReceived(JSONObject customContent, JSONObject pushMessage) {
    android.util.Log.i("Pushe", "Custom json Message: " + customContent.toString());
    // Your Code
  }
}
