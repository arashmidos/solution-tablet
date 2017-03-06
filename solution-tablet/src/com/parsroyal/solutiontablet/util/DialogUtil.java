package com.parsroyal.solutiontablet.util;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

import com.parsroyal.solutiontablet.R;

/**
 * Created by Mahyar on 6/23/2015.
 */
public class DialogUtil
{
    public static void showConfirmDialog(Context context, String title, String message,
                                         DialogInterface.OnClickListener positiveBtnOnClickListener)
    {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setTitle(title);
        alertDialogBuilder.setMessage(message);
        alertDialogBuilder.setPositiveButton(R.string.yes, positiveBtnOnClickListener);
        alertDialogBuilder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.dismiss();
            }
        });
        alertDialogBuilder.create().show();
    }

    /**
     * Show an Alert Dialog with Ok button
     * @param context
     * @param title
     * @param message
     */
    public static void showMessageDialog(Context context, String title, String message)
    {
        Dialog dialog = new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(context.getString(R.string.button_ok), new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        dialog.dismiss();
                    }
                })
                .create();
        dialog.show();
    }
}
