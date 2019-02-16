package com.parsroyal.solutiontablet.util;

import android.content.Context;

import com.parsroyal.solutiontablet.data.event.UpdateBadgerEvent;

import org.greenrobot.eventbus.EventBus;

import me.leolin.shortcutbadger.ShortcutBadger;

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
