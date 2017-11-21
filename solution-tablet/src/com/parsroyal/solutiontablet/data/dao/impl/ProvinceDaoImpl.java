package com.parsroyal.solutiontablet.data.dao.impl;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.parsroyal.solutiontablet.data.dao.ProvinceDao;
import com.parsroyal.solutiontablet.data.entity.Province;
import com.parsroyal.solutiontablet.data.helper.CommerDatabaseHelper;
import com.parsroyal.solutiontablet.data.model.LabelValue;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mahyar on 6/18/2015.
 */
public class ProvinceDaoImpl extends AbstractDao<Province, Long> implements ProvinceDao {

  private Context context;

  public ProvinceDaoImpl(Context context) {
    this.context = context;
  }

  @Override
  protected Context getContext() {
    return context;
  }

  @Override
  protected ContentValues getContentValues(Province entity) {
    ContentValues contentValues = new ContentValues();
    contentValues.put(Province.COL_ID, entity.getId());
    contentValues.put(Province.COL_BACKEND_ID, entity.getBackendId());
    contentValues.put(Province.COL_CODE, entity.getCode());
    contentValues.put(Province.COL_TITLE, entity.getTitle());
    return contentValues;
  }

  @Override
  protected String getTableName() {
    return Province.TABLE_NAME;
  }

  @Override
  protected String getPrimaryKeyColumnName() {
    return Province.COL_ID;
  }

  @Override
  protected String[] getProjection() {
    return new String[]{Province.COL_ID, Province.COL_BACKEND_ID, Province.COL_CODE,
        Province.COL_TITLE};
  }

  @Override
  protected Province createEntityFromCursor(Cursor cursor) {
    Province province = new Province();
    province.setId(cursor.getLong(0));
    province.setBackendId(cursor.getLong(1));
    province.setCode(cursor.getInt(2));
    province.setTitle(cursor.getString(3));
    return province;
  }

  @Override
  public List<LabelValue> getAllProvincesLabelValues() {
    CommerDatabaseHelper databaseHelper = CommerDatabaseHelper.getInstance(getContext());
    SQLiteDatabase db = databaseHelper.getReadableDatabase();
    Cursor cursor = db.query(getTableName(), getProjection(), null, null, null, null, null);
    List<LabelValue> labelValueModels = new ArrayList<>();
    while (cursor.moveToNext()) {
      labelValueModels.add(new LabelValue(cursor.getLong(1), cursor.getString(3)));
    }
    cursor.close();
    return labelValueModels;
  }

  @Override
  public List<LabelValue> searchProvincesLabelValues(String constraint) {
    CommerDatabaseHelper databaseHelper = CommerDatabaseHelper.getInstance(getContext());
    SQLiteDatabase db = databaseHelper.getReadableDatabase();
    String selection = " " + Province.COL_TITLE + " = ?";
    String[] args = {constraint};
    Cursor cursor = db.query(getTableName(), getProjection(), selection, args, null, null, null);
    List<LabelValue> labelValueModels = new ArrayList<>();
    while (cursor.moveToNext()) {
      labelValueModels.add(new LabelValue(cursor.getLong(1), cursor.getString(3)));
    }
    cursor.close();
    return labelValueModels;
  }
}
