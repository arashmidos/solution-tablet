package com.parsroyal.solutiontablet.data.dao.impl;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.parsroyal.solutiontablet.data.dao.CityDao;
import com.parsroyal.solutiontablet.data.entity.City;
import com.parsroyal.solutiontablet.data.helper.CommerDatabaseHelper;
import com.parsroyal.solutiontablet.data.model.LabelValue;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Arash on 6/19/2015.
 */
public class CityDaoImpl extends AbstractDao<City, Long> implements CityDao {

  private Context context;

  public CityDaoImpl(Context context) {
    this.context = context;
  }

  @Override
  protected Context getContext() {
    return context;
  }

  @Override
  protected ContentValues getContentValues(City entity) {
    ContentValues contentValues = new ContentValues();
    contentValues.put(City.COL_ID, entity.getId());
    contentValues.put(City.COL_BACKEND_ID, entity.getBackendId());
    contentValues.put(City.COL_CODE, entity.getCode());
    contentValues.put(City.COL_TITLE, entity.getTitle());
    contentValues.put(City.COL_PROVINCE_BACKEND_ID, entity.getProvinceBackendId());
    return contentValues;
  }

  @Override
  protected String getTableName() {
    return City.TABLE_NAME;
  }

  @Override
  protected String getPrimaryKeyColumnName() {
    return City.COL_ID;
  }

  @Override
  protected String[] getProjection() {
    return new String[]{City.COL_ID, City.COL_BACKEND_ID, City.COL_CODE, City.COL_TITLE,
        City.COL_PROVINCE_BACKEND_ID};
  }

  @Override
  protected City createEntityFromCursor(Cursor cursor) {
    City city = new City();
    city.setId(cursor.getLong(0));
    city.setBackendId(cursor.getLong(1));
    city.setCode(cursor.getInt(2));
    city.setTitle(cursor.getString(3));
    city.setProvinceBackendId(cursor.getLong(4));
    return city;
  }

  @Override
  public List<LabelValue> getAllCitiesLabelValuesForProvinceId(Long provinceId) {
    CommerDatabaseHelper databaseHelper = CommerDatabaseHelper.getInstance(getContext());
    SQLiteDatabase db = databaseHelper.getReadableDatabase();
    String selection = " " + City.COL_PROVINCE_BACKEND_ID + " = ?";
    String[] args = {String.valueOf(provinceId)};
    Cursor cursor = db.query(getTableName(), getProjection(), selection, args, null, null, null);
    List<LabelValue> labelValueModels = new ArrayList<>();
    while (cursor.moveToNext()) {
      labelValueModels.add(new LabelValue(cursor.getLong(1), cursor.getString(3)));
    }
    cursor.close();
    return labelValueModels;
  }

  @Override
  public List<LabelValue> searchCitiesLabelValuesForProvinceId(Long provinceId, String constraint) {
    CommerDatabaseHelper databaseHelper = CommerDatabaseHelper.getInstance(getContext());
    SQLiteDatabase db = databaseHelper.getReadableDatabase();
    String selection =
        " " + City.COL_PROVINCE_BACKEND_ID + " = ? AND " + City.COL_TITLE + " LIKE ?";
    String[] args = {String.valueOf(provinceId), "%" + constraint + "%"};
    Cursor cursor = db.query(getTableName(), getProjection(), selection, args, null, null, null);
    List<LabelValue> labelValueModels = new ArrayList<>();
    while (cursor.moveToNext()) {
      labelValueModels.add(new LabelValue(cursor.getLong(1), cursor.getString(3)));
    }
    cursor.close();
    return labelValueModels;
  }
}
