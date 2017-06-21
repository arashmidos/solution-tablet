package com.parsroyal.solutiontablet.util;

import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import com.crashlytics.android.Crashlytics;
import com.parsroyal.solutiontablet.constants.Constants;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Created by Mahyar on 9/4/2015.
 */
public class MediaUtil {

  public static final int MEDIA_TYPE_IMAGE = 1;
  public static final int MEDIA_TYPE_VIDEO = 2;
  public static final int MEDIA_TYPE_ZIP = 3;
  private static final int BUFFER_SIZE = 512;

  public static Uri getOutputMediaFileUri(int type, String directoryName, String fileName) {
    return Uri.fromFile(getOutputMediaFile(type, directoryName, fileName));
  }

  /**
   * Create a File for saving an image or video
   */
  public static File getOutputMediaFile(int type, String directoryName, String fileName) {
    // To be safe, you should check that the SDCard is mounted
    // using Environment.getExternalStorageState() before doing this.

    File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
        Environment.DIRECTORY_PICTURES), Constants.APPLICATION_NAME);
    // This location works best if you want the created images to be shared
    // between applications and persist after your app has been uninstalled.

    // Create the storage directory if it does not exist
    if (!mediaStorageDir.exists()) {
      if (!mediaStorageDir.mkdirs()) {
        Log.d("MyCameraApp", "failed to create directory");
        return null;
      }
    }

    File directory = new File(mediaStorageDir.getPath(), directoryName);
    if (!directory.exists()) {
      if (!directory.mkdirs()) {
        Log.d("MyCameraApp", "failed to create directory");
        return null;
      }
    }

    File mediaFile;
    if (type == MEDIA_TYPE_IMAGE) {
      mediaFile = new File(directory.getPath() + File.separator +
          fileName + ".jpg");
    } else if (type == MEDIA_TYPE_VIDEO) {
      mediaFile = new File(directory.getPath() + File.separator +
          fileName + ".mp4");
    } else if (type == MEDIA_TYPE_ZIP) {
      mediaFile = new File(directory.getPath() + File.separator + fileName + ".zip");
    } else {
      return null;
    }

    return mediaFile;
  }

  public static File zipFiles(String[] files) {
    BufferedInputStream origin = null;
    ZipOutputStream out = null;
    File zip = null;
    try {
      String timeStamp = String.valueOf((new Date().getTime()) % 1000);
      zip = getOutputMediaFile(MEDIA_TYPE_ZIP, Constants.CUSTOMER_PICTURE_DIRECTORY_NAME,
          "IMG_ZIP_" + timeStamp);

      out = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(zip)));

      byte data[] = new byte[BUFFER_SIZE];

      for (String file : files) {
        File f = new File(file);
        //If picture deleted before sending to server
        if (!f.exists()) {
          continue;
        }

        FileInputStream fi = new FileInputStream(f);
        origin = new BufferedInputStream(fi, BUFFER_SIZE);
        try {
          ZipEntry entry = new ZipEntry(file.substring(file.lastIndexOf("/") + 1));
          out.putNextEntry(entry);
          int count;
          while ((count = origin.read(data, 0, BUFFER_SIZE)) != -1) {
            out.write(data, 0, count);
          }
        } catch (IOException e) {
          e.printStackTrace();
        } finally {
          try {
            origin.close();
          } catch (IOException e) {
            e.printStackTrace();
          }
        }
      }
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } finally {
      try {
        out.close();
      } catch (IOException e) {
        e.printStackTrace();
        Crashlytics.log(Log.ERROR, "Zip images", e.getMessage());
      }
    }
    return zip;
  }
}
