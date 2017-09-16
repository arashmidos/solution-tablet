package com.parsroyal.solutiontablet.util;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.parsroyal.solutiontablet.R;
import com.parsroyal.solutiontablet.constants.Constants;
import com.parsroyal.solutiontablet.ui.MainActivity;

/**
 * Created by Mahyar on 6/23/2015.
 */
public class DialogUtil {

  private static ProgressDialog progressDialog;

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
    AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
    LayoutInflater inflater = ((MainActivity) context).getLayoutInflater();
    View dialogView = inflater.inflate(R.layout.dialog_custom, null);
    dialogBuilder.setView(dialogView);

    TextView titleTv = (TextView) dialogView.findViewById(R.id.title_tv);
    TextView bodyTv = (TextView) dialogView.findViewById(R.id.body_tv);
    TextView negativeTv = (TextView) dialogView.findViewById(R.id.negative_tv);
    Button positiveBtn = (Button) dialogView.findViewById(R.id.positive_btn);
    ImageView icon = (ImageView) dialogView.findViewById(R.id.icon_img);

    titleTv.setText(title);
    icon.setImageResource(iconType);
    bodyTv.setText(message);
    positiveBtn
        .setText(Empty.isEmpty(positiveChoice) ? context.getString(R.string.yes) : positiveChoice);
    negativeTv
        .setText(Empty.isEmpty(negativeChoice) ? context.getString(R.string.no) : negativeChoice);

    AlertDialog alertDialog = dialogBuilder.create();
    alertDialog.show();

    negativeTv.setOnClickListener(v -> {
      negativeBtnOnClickListener.onClick(alertDialog, 0);
      alertDialog.dismiss();
    });
    positiveBtn.setOnClickListener(v -> {
      positiveBtnOnClickListener.onClick(alertDialog, 0);
      alertDialog.dismiss();
    });
  }
//Todo shakib: Change style

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

  public static void showProgressDialog(Context context, CharSequence message) {
    if (progressDialog == null) {
      progressDialog = new ProgressDialog(context);
      progressDialog.setIndeterminate(true);
      progressDialog.setCancelable(Boolean.FALSE);
    }
    progressDialog.setIcon(R.drawable.ic_info_outline_24dp);
    progressDialog.setTitle(R.string.message_please_wait);
    progressDialog.setMessage(message);
    progressDialog.show();
  }

  public static void dismissProgressDialog() {
    if (progressDialog != null) {
      progressDialog.dismiss();
    }
  }
}
