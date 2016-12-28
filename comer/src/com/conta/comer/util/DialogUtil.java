package com.conta.comer.util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import com.conta.comer.R;

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


}
