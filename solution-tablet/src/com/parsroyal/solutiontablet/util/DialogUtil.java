package com.parsroyal.solutiontablet.util;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import com.parsroyal.solutiontablet.R;
import com.parsroyal.solutiontablet.constants.Constants;

/**
 * Created by Mahyar on 6/23/2015.
 */
public class DialogUtil {

  public static void showConfirmDialog(Context context, String title, String message,
      DialogInterface.OnClickListener positiveBtnOnClickListener) {
    showCustomDialog(context, title, message, "", positiveBtnOnClickListener, "",
        (dialog, i) -> dialog.dismiss(), Constants.ICON_MESSAGE);
  }

  public static void showCustomDialog(Context context, String title, String message,
      String positiveChoice, DialogInterface.OnClickListener positiveBtnOnClickListener,
      String negativeChoice, DialogInterface.OnClickListener negativeBtnOnClickListener,
      int iconType)

  {
    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
    alertDialogBuilder.setTitle(title);
    alertDialogBuilder.setMessage(message);
    alertDialogBuilder.setIcon(iconType);
    alertDialogBuilder.setPositiveButton(
        Empty.isEmpty(positiveChoice) ? context.getString(R.string.yes) : positiveChoice,
        positiveBtnOnClickListener);
    alertDialogBuilder.setNegativeButton(
        Empty.isEmpty(negativeChoice) ? context.getString(R.string.no) : negativeChoice,
        negativeBtnOnClickListener);
    alertDialogBuilder.create().show();
  }

  /**
   * Show an Alert Dialog with Ok button
   */
  public static void showMessageDialog(Context context, String title, String message) {
    Dialog dialog = new AlertDialog.Builder(context)
        .setTitle(title)
        .setMessage(message)
        .setPositiveButton(context.getString(R.string.button_ok),
            (dialog1, which) -> dialog1.dismiss())
        .create();
    dialog.show();
  }
}
