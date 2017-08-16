package com.parsroyal.solutiontablet.util;

import android.content.Context;
import com.parsroyal.solutiontablet.R;

/**
 * Created by arash on 8/15/17.
 */

public class MultiScreenUtility {

  public static boolean isTablet(Context context) {

    return context.getResources().getBoolean(R.bool.isTablet);
  }
}
