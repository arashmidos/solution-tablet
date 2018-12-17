package com.parsroyal.solutiontablet.data.helper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;
import com.parsroyal.solutiontablet.constants.Constants;
import com.parsroyal.solutiontablet.data.entity.BaseInfo;
import com.parsroyal.solutiontablet.data.entity.City;
import com.parsroyal.solutiontablet.data.entity.Customer;
import com.parsroyal.solutiontablet.data.entity.CustomerPic;
import com.parsroyal.solutiontablet.data.entity.Goods;
import com.parsroyal.solutiontablet.data.entity.GoodsGroup;
import com.parsroyal.solutiontablet.data.entity.KeyValue;
import com.parsroyal.solutiontablet.data.entity.Payment;
import com.parsroyal.solutiontablet.data.entity.Position;
import com.parsroyal.solutiontablet.data.entity.Province;
import com.parsroyal.solutiontablet.data.entity.QAnswer;
import com.parsroyal.solutiontablet.data.entity.Question;
import com.parsroyal.solutiontablet.data.entity.Questionnaire;
import com.parsroyal.solutiontablet.data.entity.SaleOrder;
import com.parsroyal.solutiontablet.data.entity.SaleOrderItem;
import com.parsroyal.solutiontablet.data.entity.VisitInformation;
import com.parsroyal.solutiontablet.data.entity.VisitInformationDetail;
import com.parsroyal.solutiontablet.data.entity.VisitLine;
import com.parsroyal.solutiontablet.data.entity.VisitLineDate;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by Arash on 1/16/2018.
 */
public class CommerDatabaseHelper extends SQLiteOpenHelper {

  public static final String TAG = CommerDatabaseHelper.class.getSimpleName();
  public static final String OUTPUT_PATH = Environment.getExternalStoragePublicDirectory(
      Environment.DIRECTORY_DOWNLOADS) + "/" + Constants.APPLICATION_NAME + "/Backup/";
  private static final String DATABASE_NAME = "Commer";
  private static final Integer DATABASE_VERSION = 23;
  private static final String SQL_ADD_COLUMN = "ALTER TABLE %s ADD COLUMN %s %s ";
  private static String DATABASE_PATH = "/data/data/com.parsroyal.solutionmobile/databases/";
  private static CommerDatabaseHelper sInstance;
  private final Context context;

  public CommerDatabaseHelper(Context context) {
    super(context, DATABASE_NAME, null, DATABASE_VERSION);
    this.context = context;
  }

  public static synchronized CommerDatabaseHelper getInstance(Context context) {
    if (sInstance == null) {
      sInstance = new CommerDatabaseHelper(context.getApplicationContext());
    }
    return sInstance;
  }

  public boolean copyDataBase() {
    try {
      File root = new File(OUTPUT_PATH);
      if (!root.exists()) {
        root.mkdirs();
      }
      //Open your local db as the input stream
      InputStream myInput = new FileInputStream(DATABASE_PATH + DATABASE_NAME);

      // Path to the just created empty db

      //Open the empty db as the output stream
//   DateUtil.getCurrentGregorianFullWithDate()
      OutputStream myOutput = new FileOutputStream(OUTPUT_PATH + "/" + DATABASE_NAME);

      //transfer bytes from the inputfile to the outputfile
      byte[] buffer = new byte[1024];
      int length;
      while ((length = myInput.read(buffer)) > 0) {
        myOutput.write(buffer, 0, length);
      }

      //Close the streams
      myOutput.flush();
      myOutput.close();
      myInput.close();
      return true;
    } catch (Exception ex) {
      return false;
    }
  }

  @Override
  public void onCreate(SQLiteDatabase db) {
    Log.i(TAG, "start creating tables");
    db.execSQL(KeyValue.CREATE_TABLE_SQL);
    db.execSQL(Province.CREATE_TABLE_SQL);
    db.execSQL(City.CREATE_TABLE_SQL);
    db.execSQL(Customer.CREATE_TABLE_SQL);
    db.execSQL(BaseInfo.CREATE_TABLE_SQL);
    db.execSQL(VisitLine.CREATE_TABLE_SQL);
    db.execSQL(VisitInformation.CREATE_TABLE_SQL);
    db.execSQL(Questionnaire.CREATE_TABLE_SQL);
    db.execSQL(Question.CREATE_TABLE_SQL);
    db.execSQL(QAnswer.CREATE_TABLE_SQL);
    db.execSQL(GoodsGroup.CREATE_TABLE_SQL);
    db.execSQL(Goods.CREATE_TABLE_SQL);
    db.execSQL(SaleOrder.CREATE_TABLE_SCRIPT);
    db.execSQL(SaleOrderItem.CREATE_TABLE_SCRIPT);
    db.execSQL(CustomerPic.CREATE_TABLE_SQL);
    db.execSQL(Payment.CREATE_TABLE_SCRIPT);
    db.execSQL(Position.CREATE_TABLE_SCRIPT);
    db.execSQL(VisitInformationDetail.CREATE_TABLE_SQL);
    db.execSQL(VisitLineDate.CREATE_TABLE_SQL);
    Log.i(TAG, "end of creating tables");
  }

  @Override
  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    Log.i(TAG, "on Database Upgrade");
    if (oldVersion < 3) {
      db.execSQL(VisitInformationDetail.CREATE_TABLE_SQL);
    }
    if (oldVersion < 4) {
      db.execSQL(String.format(SQL_ADD_COLUMN, Goods.TABLE_NAME, Goods.COL_SALE_RATE, "INTEGER"));
      db.execSQL(String
          .format(SQL_ADD_COLUMN, SaleOrderItem.TABLE_NAME, SaleOrderItem.COL_GOODS_COUNT_2,
              "INTEGER"));
    }
    if (oldVersion < 5) {
      db.execSQL(String.format(SQL_ADD_COLUMN, Position.TABLE_NAME, Position.COL_ACCURACY, "REAL"));
    }

    if (oldVersion < 6) {
      db.execSQL(String
          .format(SQL_ADD_COLUMN, QAnswer.TABLE_NAME, QAnswer.COL_ANSWERS_GROUP_NO, "INTEGER"));
    }
    if (oldVersion < 7) {
      db.execSQL(
          String.format(SQL_ADD_COLUMN, Question.TABLE_NAME, Question.COL_REQUIRED, "INTEGER"));
      db.execSQL(
          String.format(SQL_ADD_COLUMN, Question.TABLE_NAME, Question.COL_PREREQUISITE, "INTEGER"));
    }
    if (oldVersion < 8) {
      db.execSQL(
          String.format(SQL_ADD_COLUMN, QAnswer.TABLE_NAME, Question.COL_STATUS, "INTEGER"));
    }
    if (oldVersion < 9) {
      db.execSQL(
          String.format(SQL_ADD_COLUMN, VisitInformationDetail.TABLE_NAME,
              VisitInformationDetail.COL_STATUS, "INTEGER"));
    }
    if (oldVersion < 10) {
      db.execSQL(String
          .format(SQL_ADD_COLUMN, CustomerPic.TABLE_NAME, CustomerPic.COL_VISIT_ID, "INTEGER"));
    }
    if (oldVersion < 11) {
      db.execSQL(String
          .format(SQL_ADD_COLUMN, Customer.TABLE_NAME, Customer.COL_DESCRIPTION, "TEXT"));
    }
    if (oldVersion < 12) {
      db.execSQL(String
          .format(SQL_ADD_COLUMN, CustomerPic.TABLE_NAME, CustomerPic.COL_CUSTOMER_ID, "INTEGER"));
    }
    if (oldVersion < 13) {
      db.execSQL(String
          .format(SQL_ADD_COLUMN, Customer.TABLE_NAME, Customer.COL_REMAINED_CREDIT, "INTEGER"));
      db.execSQL(String
          .format(SQL_ADD_COLUMN, Position.TABLE_NAME, Position.COL_MOCK_LOCATION, "INTEGER"));
      db.execSQL(String
          .format(SQL_ADD_COLUMN, Position.TABLE_NAME, Position.COL_ROOTED, "INTEGER"));
      db.execSQL(String
          .format(SQL_ADD_COLUMN, Position.TABLE_NAME, Position.COL_BATTERY_LEVEL, "INTEGER"));
      db.execSQL(String
          .format(SQL_ADD_COLUMN, Position.TABLE_NAME, Position.COL_BATTERY_STATUS, "TEXT"));
      db.execSQL(String
          .format(SQL_ADD_COLUMN, Position.TABLE_NAME, Position.COL_DISTANCE, "REAL"));
      db.execSQL(String
          .format(SQL_ADD_COLUMN, Position.TABLE_NAME, Position.COL_NETWORK_DATE, "INTEGER"));
      db.execSQL(String
          .format(SQL_ADD_COLUMN, VisitInformation.TABLE_NAME, VisitInformation.COL_NETWORK_DATE,
              "INTEGER"));
    }
    if (oldVersion < 14) {
      db.execSQL(String.format(SQL_ADD_COLUMN, Position.TABLE_NAME, Position.COL_IMEI, "TEXT"));
    }
    if (oldVersion < 15) {
      db.execSQL(String
          .format(SQL_ADD_COLUMN, SaleOrder.TABLE_NAME, SaleOrder.COL_REJECT_TYPE_BACKEND_ID,
              "INTEGER"));
      db.execSQL(String
          .format(SQL_ADD_COLUMN, SaleOrder.TABLE_NAME, SaleOrder.COL_VISITLINE_BACKEND_ID,
              "INTEGER"));
      db.execSQL(String
          .format(SQL_ADD_COLUMN, Payment.TABLE_NAME, Payment.COL_VISITLINE_BACKEND_ID, "INTEGER"));
    }
    if (oldVersion < 16) {
      db.execSQL(String.format(SQL_ADD_COLUMN, SaleOrderItem.TABLE_NAME,
          SaleOrderItem.COL_GOODS_DISCOUNT, "INTEGER"));
    }
    if (oldVersion < 17) {
      db.execSQL(String.format(SQL_ADD_COLUMN, VisitInformation.TABLE_NAME,
          VisitInformation.COL_END_NETWORK_DATE, "INTEGER"));
      db.execSQL(String.format(SQL_ADD_COLUMN, VisitInformation.TABLE_NAME,
          VisitInformation.COL_DISTANCE, "INTEGER"));
    }
    if (oldVersion < 18) {
      db.execSQL(String.format(SQL_ADD_COLUMN, SaleOrder.TABLE_NAME,
          SaleOrder.COL_SMS_CONFIRM, "INTEGER"));
    }
    if (oldVersion < 19) {
      db.execSQL(String.format(SQL_ADD_COLUMN, VisitInformation.TABLE_NAME,
          VisitInformation.COL_PHONE_VISIT, "INTEGER"));
    }
    if (oldVersion < 22) {
      db.execSQL(VisitLineDate.CREATE_TABLE_SQL);
    }
    if (oldVersion < 23) {
      db.execSQL(String.format(SQL_ADD_COLUMN, SaleOrderItem.TABLE_NAME,
          SaleOrderItem.COL_INC, "INTEGER"));
      db.execSQL(String.format(SQL_ADD_COLUMN, SaleOrderItem.TABLE_NAME,
          SaleOrderItem.COL_DEC, "INTEGER"));
    }
  }
}
