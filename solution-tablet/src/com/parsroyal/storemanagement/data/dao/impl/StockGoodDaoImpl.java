package com.parsroyal.storemanagement.data.dao.impl;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.parsroyal.storemanagement.data.helper.CommerDatabaseHelper;
import com.parsroyal.storemanagement.data.model.StockGood;
import com.parsroyal.storemanagement.util.CharacterFixUtil;
import java.util.List;

/**
 * Created by Arash on 7/24/2015.
 */
public class StockGoodDaoImpl extends AbstractDao<StockGood, Long> {

  private Context context;

  public StockGoodDaoImpl(Context context) {
    this.context = context;
  }

  @Override
  protected Context getContext() {
    return context;
  }

  @Override
  protected ContentValues getContentValues(StockGood entity) {
    ContentValues contentValues = new ContentValues();

    contentValues.put(StockGood.COL_GLS, entity.getGls());
    contentValues.put(StockGood.COL_GSN_GLS, entity.getGsnGLS());
    contentValues.put(StockGood.COL_GOOD_CODE_GLS, entity.getGoodCdeGLS());
    contentValues.put(StockGood.COL_GOOD_NAME_GLS, entity.getGoodNamGLS());
    contentValues.put(StockGood.COL_IS_ACTIVE_GLS, entity.isActiveGLS() ? 1 : 0);
    contentValues.put(StockGood.COL_IS_ACTIVE_GLS_NAME, entity.getIsActiveGLSName());//5
    contentValues.put(StockGood.COL_ASN, entity.getAsn());
    contentValues.put(StockGood.COL_CODE_STK, entity.getCodeSTK());
    contentValues.put(StockGood.COL_NAME_STK, entity.getNameSTK());
    contentValues.put(StockGood.COL_USN_SERIAL_GLS, entity.getUsnSerialGLS());
    contentValues.put(StockGood.COL_UNAME, entity.getuName());//10
    contentValues.put(StockGood.COL_USN_SERIAL2_GLS, entity.getUsnSerial2GLS());
    contentValues.put(StockGood.COL_UNAME1, entity.getuName1());//12
    contentValues.put(StockGood.COL_USN_SERIAL2_2GLS, entity.getUsnSerial2_2GLS());
    contentValues.put(StockGood.COL_UNAME2, entity.getuName2());//14
    contentValues.put(StockGood.COL_URATE_GLS, entity.getuRateGLS());
    contentValues.put(StockGood.COL_BRATE_GLS, entity.getbRateGLS());
    contentValues.put(StockGood.COL_URATE_2GLS, entity.getuRate_2GLS());
    contentValues.put(StockGood.COL_BRATE_2GLS, entity.getbRate_2GLS());
    contentValues.put(StockGood.COL_COUNTED, entity.getCounted());//19

    return contentValues;
  }

  @Override
  protected String getTableName() {
    return StockGood.TABLE_NAME;
  }

  @Override
  protected String getPrimaryKeyColumnName() {
    return StockGood.COL_GLS;
  }

  @Override
  protected String[] getProjection() {
    return new String[]{
        StockGood.COL_GLS,
        StockGood.COL_GSN_GLS,
        StockGood.COL_GOOD_CODE_GLS,
        StockGood.COL_GOOD_NAME_GLS,
        StockGood.COL_IS_ACTIVE_GLS,
        StockGood.COL_IS_ACTIVE_GLS_NAME,//5
        StockGood.COL_ASN,
        StockGood.COL_CODE_STK,
        StockGood.COL_NAME_STK,
        StockGood.COL_USN_SERIAL_GLS,
        StockGood.COL_UNAME,//10
        StockGood.COL_USN_SERIAL2_GLS,
        StockGood.COL_UNAME1,
        StockGood.COL_USN_SERIAL2_2GLS,
        StockGood.COL_UNAME2,
        StockGood.COL_URATE_GLS,
        StockGood.COL_BRATE_GLS,
        StockGood.COL_URATE_2GLS,
        StockGood.COL_BRATE_2GLS,
        StockGood.COL_COUNTED,

    };
  }

  @Override
  protected StockGood createEntityFromCursor(Cursor cursor) {
    StockGood StockGood = new StockGood();

    StockGood.setGls(cursor.getLong(0));
    StockGood.setGsnGLS(cursor.getLong(1));
    StockGood.setGoodCdeGLS(cursor.getLong(2));
    StockGood.setGoodNamGLS(cursor.getString(3));
    StockGood.setActiveGLS(cursor.getLong(4) == 1);
    StockGood.setIsActiveGLSName(cursor.getString(5));
    StockGood.setAsn(cursor.getLong(6));
    StockGood.setCodeSTK(cursor.getLong(7));
    StockGood.setNameSTK(cursor.getString(8));
    StockGood.setUsnSerialGLS(cursor.getLong(9));
    StockGood.setuName(cursor.getString(10));
    StockGood.setUsnSerial2GLS(cursor.getLong(11));
    StockGood.setuName1(cursor.getString(12));
    StockGood.setUsnSerial2_2GLS(cursor.getLong(13));
    StockGood.setuName2(cursor.getString(14));
    StockGood.setuRateGLS(cursor.getLong(15));
    StockGood.setbRateGLS(cursor.getLong(16));
    StockGood.setuRate_2GLS(cursor.getLong(17));
    StockGood.setbRate_2GLS(cursor.getLong(18));
    StockGood.setCounted(cursor.getLong(19));
    return StockGood;
  }

  public StockGood retrieveByBackendId(Long gls) {
    CommerDatabaseHelper databaseHelper = CommerDatabaseHelper.getInstance(getContext());
    SQLiteDatabase db = databaseHelper.getReadableDatabase();
    String selection = StockGood.COL_GLS + " = ?";
    String[] args = {String.valueOf(gls)};
    Cursor cursor = db.query(getTableName(), getProjection(), selection, args, null, null, null);
    StockGood StockGood = null;
    if (cursor.moveToFirst()) {
      StockGood = createEntityFromCursor(cursor);
    }
    cursor.close();
    return StockGood;
  }


  public void bulkInsert(List<StockGood> list) {
    CommerDatabaseHelper databaseHelper = new CommerDatabaseHelper(getContext());
    SQLiteDatabase db = databaseHelper.getWritableDatabase();
    db.beginTransaction();
    try {
      for (StockGood stockGood : list) {
        stockGood.setGoodNamGLS(CharacterFixUtil.fixString(stockGood.getGoodNamGLS()));

        db.insert(getTableName(), null, getContentValues(stockGood));
      }
      db.setTransactionSuccessful();
    } finally {
      db.endTransaction();
    }
  }
}
