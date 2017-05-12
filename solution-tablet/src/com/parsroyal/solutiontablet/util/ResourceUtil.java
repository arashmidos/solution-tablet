package com.parsroyal.solutiontablet.util;

import android.content.Context;

/**
 * Created by Mahyar on 8/25/2015.
 */
public class ResourceUtil {

  public static String getString(Context context, String resourceName) {
    String packageName = context.getPackageName();
    int resId = context.getResources().getIdentifier(resourceName, "string", packageName);
    return context.getString(resId);
  }
}
