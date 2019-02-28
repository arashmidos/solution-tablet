package com.parsroyal.storemanagement.data.dao.impl;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.parsroyal.storemanagement.data.dao.BaseInfoDao;
import com.parsroyal.storemanagement.data.entity.BaseInfo;
import com.parsroyal.storemanagement.data.helper.CommerDatabaseHelper;
import com.parsroyal.storemanagement.data.model.LabelValue;
import com.parsroyal.storemanagement.util.Empty;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mahyar on 6/20/2015.
 */
public class BaseInfoDaoImpl extends AbstractDao<BaseInfo, Long> implements BaseInfoDao {

  private Context context;

  public BaseInfoDaoImpl(Context context) {
    this.context = context;
  }

  @Override
  protected Context getContext() {
    return context;
  }

  @Override
  protected ContentValues getContentValues(BaseInfo entity) {
    ContentValues contentValues = new ContentValues();
    contentValues.put(BaseInfo.COL_ID, entity.getId());
    contentValues.put(BaseInfo.COL_BACKEND_ID, entity.getBackendId());
    contentValues.put(BaseInfo.COL_CODE, entity.getCode());
    contentValues.put(BaseInfo.COL_TITLE, entity.getTitle());
    contentValues.put(BaseInfo.COL_TYPE, entity.getType());
    return contentValues;
  }

  @Override
  protected String getTableName() {
    return BaseInfo.TABLE_NAME;
  }

  @Override
  protected String getPrimaryKeyColumnName() {
    return BaseInfo.COL_ID;
  }

  @Override
  protected String[] getProjection() {
    String[] projection = {BaseInfo.COL_ID, BaseInfo.COL_BACKEND_ID, BaseInfo.COL_CODE,
        BaseInfo.COL_TITLE, BaseInfo.COL_TYPE};
    ;
    return projection;
  }

  @Override
  protected BaseInfo createEntityFromCursor(Cursor cursor) {
    BaseInfo baseInfo = new BaseInfo();
    baseInfo.setId(cursor.getLong(0));
    baseInfo.setBackendId(cursor.getLong(1));
    baseInfo.setCode(cursor.getInt(2));
    baseInfo.setTitle(cursor.getString(3));
    baseInfo.setType(cursor.getLong(4));
    return baseInfo;
  }

  @Override
  public List<LabelValue> getAllBaseInfosLabelValuesByTypeId(Long typeId, Long backendId) {
    String selection = " " + BaseInfo.COL_TYPE + " = ?";
    String[] args = {String.valueOf(typeId)};
    if (Empty.isNotEmpty(backendId)) {
      selection = selection + " AND " + BaseInfo.COL_BACKEND_ID + " = ? ";
      args = new String[]{String.valueOf(typeId), String.valueOf(backendId)};
    }
    CommerDatabaseHelper databaseHelper = CommerDatabaseHelper.getInstance(getContext());
    SQLiteDatabase db = databaseHelper.getReadableDatabase();
    String orderBy = BaseInfo.COL_CODE;
    Cursor cursor = db.query(getTableName(), getProjection(), selection, args, null, null, orderBy);
    List<LabelValue> entities = new ArrayList<>();
    while (cursor.moveToNext()) {
      entities.add(new LabelValue(cursor.getLong(1), cursor.getString(3)));
    }
    cursor.close();
    return entities;
  }

  @Override
  public List<LabelValue> search(Long type, String constraint) {
    String selection = " " + BaseInfo.COL_TYPE + " = ? AND " + BaseInfo.COL_TITLE + " LIKE ? ";
    String[] args = {String.valueOf(type), "%" + constraint + "%"};

    CommerDatabaseHelper databaseHelper = CommerDatabaseHelper.getInstance(getContext());
    SQLiteDatabase db = databaseHelper.getReadableDatabase();
    Cursor cursor = db.query(getTableName(), getProjection(), selection, args, null, null, null);
    List<LabelValue> entities = new ArrayList<>();
    while (cursor.moveToNext()) {
      entities.add(new LabelValue(cursor.getLong(1), cursor.getString(3)));
    }
    cursor.close();
    return entities;
  }
}
