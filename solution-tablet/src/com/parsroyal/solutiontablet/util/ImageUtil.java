package com.parsroyal.solutiontablet.util;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.util.Base64;
import android.util.Log;
import android.webkit.MimeTypeMap;
import com.crashlytics.android.Crashlytics;
import com.parsroyal.solutiontablet.R;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Arash on 27/07/2015.
 */
public class ImageUtil {

  public static String saveTempImage(Bitmap bitmap, File file) {
    if (file.exists()) {
      file.delete();
    }
    try {
      FileOutputStream out = new FileOutputStream(file);
      bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
      out.flush();
      out.close();
      return file.getAbsolutePath();
    } catch (Exception e) {
      Logger.sendError( "Storage Exception", "Error in Bsaving temp image " + e.getMessage());
      e.printStackTrace();
      return "";
    }
  }

  public static Bitmap imageOrientationValidator(Bitmap bitmap, String path) {
    ExifInterface ei;
    try {
      ei = new ExifInterface(path);
      int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
          ExifInterface.ORIENTATION_NORMAL);
      switch (orientation) {
        case ExifInterface.ORIENTATION_ROTATE_90:
          bitmap = rotateImage(bitmap, 90);
          break;
        case ExifInterface.ORIENTATION_ROTATE_180:
          bitmap = rotateImage(bitmap, 180);
          break;
        case ExifInterface.ORIENTATION_ROTATE_270:
          bitmap = rotateImage(bitmap, 270);
          break;
      }
    } catch (IOException e) {
      e.printStackTrace();
    }

    return bitmap;
  }

  public static Bitmap rotateImage(Bitmap source, float angle) {
    Bitmap bitmap = null;
    Matrix matrix = new Matrix();
    matrix.postRotate(angle);
    try {
      bitmap = Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
          matrix, true);
    } catch (OutOfMemoryError err) {
      err.printStackTrace();
    }
    return bitmap;
  }

  public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth,
      int reqHeight) {
    final int height = options.outHeight;
    final int width = options.outWidth;
    int inSampleSize = 1;

    if (height > reqHeight || width > reqWidth) {
      final int halfHeight = height / 2;
      final int halfWidth = width / 2;

      while ((halfHeight / inSampleSize) > reqHeight
          && (halfWidth / inSampleSize) > reqWidth) {
        inSampleSize *= 2;
      }
    }

    return inSampleSize;
  }

  public static Bitmap decodeSampledBitmapFromUri(Context context, Uri uri) {
    int width = context.getResources().getInteger(R.integer.upload_image_width);
    int height = context.getResources().getInteger(R.integer.upload_image_height);

    final BitmapFactory.Options options = new BitmapFactory.Options();
    options.inJustDecodeBounds = true;
    try {
      BitmapFactory.decodeStream(context.getContentResolver().openInputStream(uri), null, options);
    } catch (FileNotFoundException e) {
      return null;
    }

    options.inSampleSize = calculateInSampleSize(options, width, height);
    options.inJustDecodeBounds = false;
    try {
      return BitmapFactory.decodeStream(context.getContentResolver()
          .openInputStream(uri), null, options);
    } catch (FileNotFoundException e) {
      return null;
    }
  }

  public static Bitmap getScaledBitmap(Context context, Bitmap bitmap) {
    if (bitmap == null) {
      return null;
    }

    int original_height = bitmap.getHeight();
    int original_width = bitmap.getWidth();
    int bound_width = context.getResources().getInteger(R.integer.upload_image_width);
    int bound_height = context.getResources().getInteger(R.integer.upload_image_height);

    int new_width = original_width;
    int new_height = original_height;

    if (original_width > bound_width) {
      new_width = bound_width;
      new_height = (new_width * original_height) / original_width;
    }

    if (new_height > bound_height) {
      new_height = bound_height;
      new_width = (new_height * original_width) / original_height;
    }

    if ((new_height == original_height) && (new_width == original_width)) {
      return bitmap;
    } else {
      return Bitmap.createScaledBitmap(bitmap, new_width, new_height, true);
    }
  }

  public static String getExtension(Context context, Uri uri) {
    ContentResolver cR = context.getContentResolver();
    MimeTypeMap mime = MimeTypeMap.getSingleton();
    return mime.getExtensionFromMimeType(cR.getType(uri));
  }

  public static String encodeImage(Bitmap bitmap) {
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    bitmap.compress(Bitmap.CompressFormat.PNG, 100
        , outputStream);
    byte[] byteArray = outputStream.toByteArray();
    return Base64.encodeToString(byteArray
        , Base64.DEFAULT);
  }

  public static Bitmap getBitmapFromVectorDrawable(Context context, int drawableId) {
    Drawable drawable = ContextCompat.getDrawable(context, drawableId);
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
      drawable = (DrawableCompat.wrap(drawable)).mutate();
    }

    Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
        drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
    Canvas canvas = new Canvas(bitmap);
    drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
    drawable.draw(canvas);

    return bitmap;
  }
}
