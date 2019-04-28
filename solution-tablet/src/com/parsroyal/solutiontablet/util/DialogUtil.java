package com.parsroyal.solutiontablet.util;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.parsroyal.solutiontablet.BuildConfig;
import com.parsroyal.solutiontablet.R;
import com.parsroyal.solutiontablet.constants.Constants;
import com.parsroyal.solutiontablet.ui.activity.MainActivity;
import com.parsroyal.solutiontablet.ui.fragment.NavigationDrawerFragment.OnLoginListener;

/**
 * Created by Arash on 6/23/2015.
 */
public class DialogUtil {

  private static ProgressDialog progressDialog;

  public static void showConfirmDialog(Context context, String title, String message,
      DialogInterface.OnClickListener positiveBtnOnClickListener) {
    showCustomDialog(context, title, message, "", positiveBtnOnClickListener, "",
        (dialog, i) -> dialog.dismiss(), Constants.ICON_MESSAGE);
  }

  public static void showConfirmDialog(Context context, String title, String message,
      String positiveChoice,
      DialogInterface.OnClickListener positiveBtnOnClickListener, String negativeChoice) {
    showCustomDialog(context, title, message, positiveChoice, positiveBtnOnClickListener,
        negativeChoice, (dialog, i) -> dialog.dismiss(), Constants.ICON_MESSAGE);
  }

  public static void showCustomDialog(Context context, String title, String message,
      String positiveChoice, DialogInterface.OnClickListener positiveBtnOnClickListener,
      String negativeChoice, DialogInterface.OnClickListener negativeBtnOnClickListener,
      int iconType) {
    AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
    LayoutInflater inflater = ((AppCompatActivity) context).getLayoutInflater();
    View dialogView = inflater.inflate(R.layout.dialog_custom, null);
    dialogBuilder.setView(dialogView);

    dialogBuilder.setCancelable(false);
    TextView titleTv = dialogView.findViewById(R.id.title_tv);
    TextView bodyTv = dialogView.findViewById(R.id.body_tv);
    TextView negativeTv = dialogView.findViewById(R.id.negative_tv);
    Button positiveBtn = dialogView.findViewById(R.id.positive_btn);
    ImageView icon = dialogView.findViewById(R.id.icon_img);

    titleTv.setText(title);
    icon.setImageResource(iconType);
    bodyTv.setText(message);
    positiveBtn
        .setText(Empty.isEmpty(positiveChoice) ? context.getString(R.string.yes) : positiveChoice);
    if (negativeBtnOnClickListener == null) {
      negativeTv.setVisibility(View.GONE);
    } else {
      negativeTv
          .setText(Empty.isEmpty(negativeChoice) ? context.getString(R.string.no) : negativeChoice);
    }

    AlertDialog alertDialog = dialogBuilder.create();
    alertDialog.setCancelable(false);
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

  public static void showLoginDialog(Context context, OnLoginListener positiveBtnOnClickListener) {
    AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
    LayoutInflater inflater = ((AppCompatActivity) context).getLayoutInflater();
    View dialogView = inflater.inflate(R.layout.dialog_login_admin, null);
    dialogBuilder.setView(dialogView);

    dialogBuilder.setCancelable(false);
    EditText username = dialogView.findViewById(R.id.user_name_edt);
    EditText password = dialogView.findViewById(R.id.password_edt);
    TextView negativeTv = dialogView.findViewById(R.id.negative_tv);
    Button positiveBtn = dialogView.findViewById(R.id.positive_btn);
    if (BuildConfig.DEBUG) {
      username.setText("royal");
      password.setText("royal2018");
    }
    AlertDialog alertDialog = dialogBuilder.create();
    alertDialog.setCancelable(false);
    alertDialog.show();

    negativeTv.setOnClickListener(v -> alertDialog.dismiss());
    positiveBtn.setOnClickListener(v -> {
      positiveBtnOnClickListener
          .onLogin(username.getText().toString(), password.getText().toString());
      alertDialog.dismiss();
    });
  }

  public static void showProgressDialog(Context context, CharSequence message) {
    progressDialog = new ProgressDialog(context);
    progressDialog.setIndeterminate(true);
    progressDialog.setCancelable(Boolean.FALSE);

    progressDialog.setIcon(R.drawable.ic_info_outline_24dp);
    progressDialog.setTitle(R.string.message_please_wait);
    progressDialog.setMessage(message);

    progressDialog.show();
  }

  public static void showProgressDialog(Context context, int messageId) {
    showProgressDialog(context, context.getString(messageId));
  }

  public static void dismissProgressDialog() {
    if (progressDialog != null) {
      progressDialog.dismiss();
    }
  }

  public static void showOpenCustomerDialog(MainActivity context, int distance,
      DialogInterface.OnClickListener enter, DialogInterface.OnClickListener navigate) {
    AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
    LayoutInflater inflater1 = context.getLayoutInflater();
    View dialogView = inflater1.inflate(R.layout.bottom_sheet_map_chooser, null);
    TextView navTv = dialogView.findViewById(R.id.navigation_tv);
    LinearLayout enterLay = dialogView.findViewById(R.id.enter_layout);
    TextView distanceTv = dialogView.findViewById(R.id.distance_tv);
    if (distance <= 0) {
      distanceTv.setText(R.string.unknown_distance);
    } else {
      distanceTv.setText(NumberUtil.digitsToPersian(String.format(
          context.getString(R.string.distance_to_customer), String.valueOf(distance))));
    }
    dialogBuilder.setView(dialogView);
    AlertDialog alertDialog = dialogBuilder.create();
    alertDialog.show();

    enterLay.setOnClickListener(v -> {
      enter.onClick(alertDialog, 0);
      alertDialog.dismiss();
    });
    navTv.setOnClickListener(v -> {
      navigate.onClick(alertDialog, 1);
      alertDialog.dismiss();
    });
  }
}
