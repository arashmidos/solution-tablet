package com.parsroyal.solutiontablet.util;

import android.os.Environment;
import android.util.Log;
import com.crashlytics.android.Crashlytics;
import com.parsroyal.solutiontablet.BuildConfig;
import com.parsroyal.solutiontablet.data.entity.Position;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

/**
 * Created by arash on 8/9/17.
 */

public class Logger {

  public static void logToFile(Position position) {
    try {
      File dir = new File(Environment.getExternalStorageDirectory() + "/Download");
      if (!dir.exists()) {
        dir.mkdir();
      }
      File output = new File(dir, "position.csv");
      if (!output.exists()) {
        output.createNewFile();
      }

      FileWriter fileWriter = new FileWriter(output, true);
      BufferedWriter writer = new BufferedWriter(fileWriter);
      writer.write(
          position.getId() + "," + position.getLatitude() + "," + position.getLongitude() + ","
              + position.getSpeed() + ","
              + position.getAccuracy() + "," + position.getMode() + "," + position.getDate() + ","
              + position.getSalesmanId()
      );
      writer.newLine();
      writer.flush();
      writer.close();
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  public static void sendError(String page, String exception) {
    if (!BuildConfig.DEBUG) {
      Crashlytics.log(Log.ERROR, page, exception);
    }
  }
}
