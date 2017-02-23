package com.parsroyal.solutiontablet.util;

import android.content.Context;
import android.widget.Toast;

import com.parsroyal.solutiontablet.exception.BusinessException;

import java.text.MessageFormat;

/**
 * Created by Mahyar on 6/23/2015.
 */
public class ToastUtil
{
    public static void toastMessage(Context context, String message)
    {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public static void toastError(Context context, int messageResource)
    {
        String message = context.getString(messageResource);
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public static void toastError(Context context, String message)
    {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public static void toastError(Context context, BusinessException ex)
    {
        String message = getErrorString(context, ex);
        if (Empty.isNotEmpty(message))
        {
            message = MessageFormat.format(message, ex.getArgs());
        }
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public static String getStringResourceByName(Context context, String aString)
    {
//        aString = aString.replace(".", "_");
        String packageName = context.getPackageName();
        int resId = context.getResources().getIdentifier(aString, "string", packageName);
        return context.getString(resId);
    }

    private static String getErrorString(Context context, Exception ex)
    {
        return getStringResourceByName(context, ex.getClass().getCanonicalName());
    }
}
