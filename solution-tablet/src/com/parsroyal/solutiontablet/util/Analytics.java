package com.parsroyal.solutiontablet.util;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.ContentViewEvent;
import com.crashlytics.android.answers.CustomEvent;
import com.crashlytics.android.answers.LoginEvent;
import com.crashlytics.android.answers.SearchEvent;
import com.parsroyal.solutiontablet.BuildConfig;

/**
 * Created by arash on 5/18/17.
 */

public class Analytics {

  /**
   * Logs that user hit a search query
   *
   * @param constraint search query
   */
  public static void logSearch(String constraint) {
    if (!BuildConfig.DEBUG) {
      Answers.getInstance().logSearch(new SearchEvent().putQuery(constraint));
    }
  }

  public static void logSearch(String constraint, String key, String value) {
    if (!BuildConfig.DEBUG) {
      Answers.getInstance().logSearch(new SearchEvent().putQuery(constraint)
          .putCustomAttribute(key, value));
    }
  }

  public static void logContentView(String contentName) {
    logContentView(contentName, "");
  }

  public static void logContentView(String contentName, String contentType) {
    if (!BuildConfig.DEBUG) {
      Answers.getInstance().logContentView(
          new ContentViewEvent().putContentName(contentName).putContentType(contentType));
    }
  }

  public static void logLogin(boolean isSuccess) {
    if (!BuildConfig.DEBUG) {
      Answers.getInstance().logLogin(new LoginEvent().putSuccess(isSuccess));
    }
  }

  public static void logCustom(String name, String[] keys, String... values) {
    CustomEvent customEvent = new CustomEvent(name);
    if (!BuildConfig.DEBUG) {
      for (int i = 0; i < keys.length; i++) {
        customEvent.putCustomAttribute(keys[i], values[i]);
      }
      Answers.getInstance().logCustom(customEvent);
    }
  }
}
