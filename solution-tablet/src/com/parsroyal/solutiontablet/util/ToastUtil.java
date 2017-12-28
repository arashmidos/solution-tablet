package com.parsroyal.solutiontablet.util;

import android.app.Activity;
import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import com.parsroyal.solutiontablet.R;
import com.parsroyal.solutiontablet.exception.BusinessException;
import de.mateware.snacky.Snacky;
import java.text.MessageFormat;

/**
 * Created by Arash on 6/23/2015.
 */
public class ToastUtil {

  //*********************** Toast Messages ***********************/
  private static void toastMessage(Activity activity, View view, String message) {
    Snacky.Builder builder = Snacky.builder();
    boolean isTablet;
    if (activity != null) {
      builder.setActivty(activity);
      isTablet = MultiScreenUtility.isTablet(activity);
    } else if (view != null) {
      builder.setView(view);
      isTablet = MultiScreenUtility.isTablet(view);
    } else {
      return;
    }

    builder.setText(message);
    builder.setActionTextColor(Color.WHITE);
    builder.setTextSize(isTablet ? 20 : 14);
    builder.setDuration(Snacky.LENGTH_LONG);
    final Snackbar snack = builder.info();
    if (isTablet) {
      View snackView = snack.getView();
      FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) snackView.getLayoutParams();
      params.gravity = Gravity.TOP;
      params.width = LayoutParams.MATCH_PARENT;
      snackView.setLayoutParams(params);
    }
    snack.show();
  }

  public static void toastMessage(Activity activity, int messageResource) {
    String message = activity.getString(messageResource);
    toastMessage(activity, null, message);
  }

  public static void toastMessage(View view, int messageResource) {
    String message = view.getResources().getString(messageResource);
    toastMessage(null, view, message);
  }

  public static void toastMessage(Activity activity, String message) {
    toastMessage(activity, null, message);
  }

  public static void toastMessage(View view, String message) {
    toastMessage(null, view, message);
  }

  //*********************** Toast Success ***********************/
  private static void toastSuccess(Activity activity, View view, String message) {
    Snacky.Builder builder = Snacky.builder();
    boolean isTablet;
    if (activity != null) {
      builder.setActivty(activity);
      isTablet = MultiScreenUtility.isTablet(activity);
    } else if (view != null) {
      builder.setView(view);
      isTablet = MultiScreenUtility.isTablet(view);
    } else {
      return;
    }
    builder.setText(message);
    builder.setActionTextColor(Color.BLACK);
    builder.setTextSize(isTablet ? 20 : 14);
    builder.setDuration(Snacky.LENGTH_LONG);
    final Snackbar snack = builder.success();
    if (isTablet) {
      View snackView = snack.getView();
      FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) snackView.getLayoutParams();
      params.gravity = Gravity.TOP;
      params.width = LayoutParams.MATCH_PARENT;
      snackView.setLayoutParams(params);
    }
    snack.show();
  }

  public static void toastSuccess(Activity activity, int messageResource) {
    String message = activity.getString(messageResource);
    toastSuccess(activity, null, message);
  }

  public static void toastSuccess(View view, int messageResource) {
    String message = view.getResources().getString(messageResource);
    toastSuccess(null, view, message);
  }

  public static void toastSuccess(Activity activity, String message) {
    toastSuccess(activity, null, message);
  }

  public static void toastSuccess(View view, String message) {
    toastSuccess(null, view, message);
  }

  //*********************** Toast ERROR ***********************/
  private static void toastError(Activity activity, View view, String message,
      OnClickListener listener) {
    Snacky.Builder builder = Snacky.builder();
    boolean isTablet;
    String okButton;
    if (activity != null) {
      builder.setActivty(activity);
      isTablet = MultiScreenUtility.isTablet(activity);
      okButton = activity.getString(R.string.button_ok);
    } else if (view != null) {
      builder.setView(view);
      isTablet = MultiScreenUtility.isTablet(view);
      okButton = view.getResources().getString(R.string.button_ok);
    } else {
      return;
    }
    builder.setText(message);
    builder.setTextColor(Color.WHITE);
    builder.setActionTextColor(Color.BLACK);
    builder.setBackgroundColor(Color.RED);
    builder.setTextSize(isTablet ? 20 : 14);
    builder.setActionTextSize(isTablet ? 20 : 14);
    builder.setDuration(Snacky.LENGTH_INDEFINITE);
    final Snackbar snack = builder.build();
    if (isTablet) {
      View snackView = snack.getView();
      FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) snackView.getLayoutParams();
      params.gravity = Gravity.TOP;
      params.width = LayoutParams.MATCH_PARENT;
      snackView.setLayoutParams(params);
    }
    snack.setAction(okButton, v -> {
      snack.dismiss();
      if (Empty.isNotEmpty(listener)) {
        listener.onClick(null);
      }
    });
    snack.show();
  }


  public static void toastError(Activity activity, int messageResource) {
    String message = activity.getString(messageResource);
    toastError(activity, null, message, null);
  }

  public static void toastError(Activity activity, String message) {
    toastError(activity, null, message, null);
  }

  public static void toastError(View view, int messageResource) {
    String message = view.getResources().getString(messageResource);
    toastError(null, view, message, null);
  }

  public static void toastError(View view, String message) {
    toastError(null, view, message, null);
  }

  public static void toastError(Activity activity, String message, OnClickListener listener) {
    toastError(activity, null, message, listener);
  }

  public static void toastError(Activity activity, BusinessException ex) {
    String message = getErrorString(activity, ex);
    if (Empty.isNotEmpty(message)) {
      message = MessageFormat.format(message, ex.getArgs());
    }
    toastError(activity, null, message, null);
  }

  public static void toastError(View view, BusinessException ex) {
    String message = getErrorString(view, ex);
    if (Empty.isNotEmpty(message)) {
      message = MessageFormat.format(message, ex.getArgs());
    }
    toastError(null, view, message, null);
  }

  private static String getStringResourceByName(Activity activity, String aString) {
    String packageName = activity.getPackageName();
    int resId = activity.getResources().getIdentifier(aString, "string", packageName);
    return activity.getString(resId);
  }

  private static String getStringResourceByName(View view, String aString) {
    String packageName = view.getContext().getPackageName();
    int resId = view.getResources().getIdentifier(aString, "string", packageName);
    return view.getResources().getString(resId);
  }

  private static String getErrorString(Activity activity, Exception ex) {
    return getStringResourceByName(activity, ex.getClass().getCanonicalName());
  }

  private static String getErrorString(View view, Exception ex) {
    return getStringResourceByName(view, ex.getClass().getCanonicalName());
  }
}
