package com.parsroyal.storemanagement.util;

import android.content.Context;
import com.parsroyal.storemanagement.data.event.UpdateBadgerEvent;
import me.leolin.shortcutbadger.ShortcutBadger;
import org.greenrobot.eventbus.EventBus;


public class BadgerHelper {

  public static void removeBadge(Context context) {
    PreferenceHelper.setBadger(0);
    ShortcutBadger.removeCount(context);
    EventBus.getDefault().post(new UpdateBadgerEvent());
  }

  public static void addBadge(Context context, int count) {
    int currentBadger = PreferenceHelper.getBadger();
    PreferenceHelper.setBadger(currentBadger + count);
    ShortcutBadger.applyCount(context, currentBadger + count);
    EventBus.getDefault().post(new UpdateBadgerEvent());
  }
}
