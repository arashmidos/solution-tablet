package com.parsroyal.solutiontablet.data.helper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
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

/**
 * Created by Mahyar on 6/4/2015.
 */
public class CommerDatabaseHelper extends SQLiteOpenHelper {

  public static final String TAG = CommerDatabaseHelper.class.getSimpleName();
  private static final String DATABASE_NAME = "Commer";
  private static final Integer DATABASE_VERSION = 12;
  private static final String SQL_ADD_COLUMN = "ALTER TABLE %s ADD COLUMN %s %s ";

  private static CommerDatabaseHelper sInstance;

  public CommerDatabaseHelper(Context context) {
    super(context, DATABASE_NAME, null, DATABASE_VERSION);
  }

  public static synchronized CommerDatabaseHelper getInstance(Context context) {
    if (sInstance == null) {
      sInstance = new CommerDatabaseHelper(context.getApplicationContext());
    }
    return sInstance;
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
  }
}
