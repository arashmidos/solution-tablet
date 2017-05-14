package com.parsroyal.solutiontablet.util;

import android.app.Activity;
import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.view.View;

import com.parsroyal.solutiontablet.R;
import com.parsroyal.solutiontablet.exception.BusinessException;

import java.text.MessageFormat;

import de.mateware.snacky.Snacky;

/**
 * Created by Mahyar on 6/23/2015.
 */
public class ToastUtil
{

    public static void toastMessage(Activity activity, String message)
    {
        Snacky.Builder builder = Snacky.builder();
        builder.setActivty(activity);
        builder.setText(message);
        builder.setActionTextColor(Color.WHITE);
        builder.setTextSize(20);
        builder.setDuration(Snacky.LENGTH_LONG);
        final Snackbar snack = builder.info();
        snack.show();
    }

    public static void toastMessage(Activity activity, int messageResource)
    {
        String message = activity.getString(messageResource);
        toastMessage(activity, message);
    }

    public static void toastSuccess(Activity activity, String message)
    {
        Snacky.Builder builder = Snacky.builder();
        builder.setActivty(activity);
        builder.setText(message);
        builder.setActionTextColor(Color.BLACK);
        builder.setTextSize(20);
        builder.setDuration(Snacky.LENGTH_LONG);
        final Snackbar snack = builder.success();
        snack.show();
    }

    public static void toastSuccess(Activity activity, int messageResource)
    {
        String message = activity.getString(messageResource);
        toastSuccess(activity, message);
    }

    public static void toastError(Activity activity, int messageResource)
    {
        String message = activity.getString(messageResource);
        toastError(activity, message);
    }

    public static void toastError(final Activity activity, String message)
    {
        Snacky.Builder builder = Snacky.builder();
        builder.setActivty(activity);
        builder.setText(message);
        builder.setTextColor(Color.WHITE);
        builder.setActionTextColor(Color.BLACK);
        builder.setBackgroundColor(Color.RED);
        builder.setTextSize(20);
        builder.setActionTextSize(20);
        builder.setDuration(Snacky.LENGTH_INDEFINITE);
        final Snackbar snack = builder.build();
        snack.setAction(activity.getString(R.string.button_ok), new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                snack.dismiss();
            }
        });
        snack.show();
    }

    public static void toastError(Activity activity, BusinessException ex)
    {
        String message = getErrorString(activity, ex);
        if (Empty.isNotEmpty(message))
        {
            message = MessageFormat.format(message, ex.getArgs());
        }
        toastError(activity, message);
    }

    public static String getStringResourceByName(Activity activity, String aString)
    {
        String packageName = activity.getPackageName();
        int resId = activity.getResources().getIdentifier(aString, "string", packageName);
        return activity.getString(resId);
    }

    private static String getErrorString(Activity activity, Exception ex)
    {
        return getStringResourceByName(activity, ex.getClass().getCanonicalName());
    }
}
