package com.parsroyal.solutiontablet.pushe;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import co.ronash.pushe.Pushe;
import co.ronash.pushe.PusheListenerService;

public class MyPushListener extends PusheListenerService {
    @Override
    public void onMessageReceived(JSONObject customContent, JSONObject pushMessage) {
        Log.i("Pushe", "Custom json Message: " + customContent.toString());
        try {
            String s1 = customContent.getString("title");
            String s2 = pushMessage.getString("title");
            Log.i("Pushe", "Message: " + s1);
            Log.i("Pushe", "Message2: " + s2);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Your Code
    }
}