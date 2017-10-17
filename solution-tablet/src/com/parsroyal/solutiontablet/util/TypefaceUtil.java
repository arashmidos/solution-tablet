package com.parsroyal.solutiontablet.util;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import com.crashlytics.android.Crashlytics;
import java.lang.reflect.Field;

/**
 * Created by Mahyar on 7/9/2015.
 */

public class TypefaceUtil {

  public static final String TAG = "TypeFaceUtil";

  /**
   * Using reflection to override default typeface NOTICE: DO NOT FORGET TO SET TYPEFACE FOR APP
   * THEME AS DEFAULT TYPEFACE WHICH WILL BE OVERRIDDEN
   *
   * @param context to work with assets
   * @param defaultFontNameToOverride for example "monospace"
   * @param customFontFileNameInAssets file name of the font from assets
   */
  public static void overrideFont(Context context, String defaultFontNameToOverride,
      String customFontFileNameInAssets) {
    try {
      final Typeface customFontTypeface = Typeface
          .createFromAsset(context.getAssets(), customFontFileNameInAssets);

      final Field defaultFontTypefaceField = Typeface.class
          .getDeclaredField(defaultFontNameToOverride);
      defaultFontTypefaceField.setAccessible(true);
      defaultFontTypefaceField.set(null, customFontTypeface);
    } catch (Exception e) {
      Logger.sendError( "Resource Exception", "Error in overriding fonts " + e.getMessage());
      Log.e("Can not set custom font " + customFontFileNameInAssets + " instead of "
          + defaultFontNameToOverride, TAG);
    }
  }
}

