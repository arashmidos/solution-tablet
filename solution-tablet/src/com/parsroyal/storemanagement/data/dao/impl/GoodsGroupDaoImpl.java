package com.parsroyal.storemanagement.data.dao.impl;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.parsroyal.storemanagement.data.dao.GoodsGroupDao;
import com.parsroyal.storemanagement.data.entity.GoodsGroup;
import com.parsroyal.storemanagement.data.helper.CommerDatabaseHelper;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Arash on 7/24/2015.
 */
public class GoodsGroupDaoImpl extends AbstractDao<GoodsGroup, Long> implements GoodsGroupDao {

  private Context context;

  public GoodsGroupDaoImpl(Context context) {
    this.context = context;
  }

  @Override
  protected Context getContext() {
    return context;
  }

  @Override
  protected ContentValues getContentValues(GoodsGroup entity) {
    ContentValues contentValues = new ContentValues();
    contentValues.put(GoodsGroup.COL_ID, entity.getId());
    contentValues.put(GoodsGroup.COL_BACKEND_ID, entity.getBackendId());
    contentValues.put(GoodsGroup.COL_PARENT_BACKEND_ID, entity.getParentBackendId());
    contentValues.put(GoodsGroup.COL_TITLE, entity.getTitle());
    contentValues.put(GoodsGroup.COL_CODE, entity.getCode());
    contentValues.put(GoodsGroup.COL_LEVEL, entity.getLevel());
    contentValues.put(GoodsGroup.COL_CREATE_DATE_TIME, entity.getCreateDateTime());
    contentValues.put(GoodsGroup.COL_UPDATE_DATE_TIME, entity.getUpdateDateTime());
    return contentValues;
  }

  @Override
  protected String getTableName() {
    return GoodsGroup.TABLE_NAME;
  }

  @Override
  protected String getPrimaryKeyColumnName() {
    return GoodsGroup.COL_ID;
  }

  @Override
  protected String[] getProjection() {
    return new String[]{
        GoodsGroup.COL_ID,
        GoodsGroup.COL_BACKEND_ID,
        GoodsGroup.COL_PARENT_BACKEND_ID,
        GoodsGroup.COL_TITLE,
        GoodsGroup.COL_CODE,
        GoodsGroup.COL_LEVEL,
        GoodsGroup.COL_CREATE_DATE_TIME,
        GoodsGroup.COL_UPDATE_DATE_TIME
    };
  }

  @Override
  protected GoodsGroup createEntityFromCursor(Cursor cursor) {
    GoodsGroup group = new GoodsGroup();
    group.setId(cursor.getLong(0));
    group.setBackendId(cursor.getLong(1));
    group.setParentBackendId(cursor.getLong(2));
    group.setTitle(cursor.getString(3));
    group.setCode(cursor.getString(4));
    group.setLevel(cursor.getInt(5));
    group.setCreateDateTime(cursor.getString(6));
    group.setUpdateDateTime(cursor.getString(7));
    return group;
  }

  @Override
  public List<GoodsGroup> getCategories() {

    CommerDatabaseHelper databaseHelper = CommerDatabaseHelper.getInstance(getContext());
    SQLiteDatabase db = databaseHelper.getReadableDatabase();
    String selection = GoodsGroup.COL_LEVEL + " = ?";
    String[] args = {String.valueOf(1)};
    Cursor cursor = db.query(getTableName(), getProjection(), selection, args, null, null, null);
    List<GoodsGroup> goodsGroups = new ArrayList<>();
    while (cursor.moveToNext()) {
      goodsGroups.add(createEntityFromCursor(cursor));
    }
    cursor.close();
    return goodsGroups;
  }

  @Override
  public List<GoodsGroup> getChilds(Long goodsGroupBackendId) {
    CommerDatabaseHelper databaseHelper = CommerDatabaseHelper.getInstance(getContext());
    SQLiteDatabase db = databaseHelper.getReadableDatabase();
    String selection = GoodsGroup.COL_PARENT_BACKEND_ID + " = ?";
    String[] args = {String.valueOf(goodsGroupBackendId)};
    Cursor cursor = db.query(getTableName(), getProjection(), selection, args, null, null, null);
    List<GoodsGroup> goodsGroups = new ArrayList<>();
    while (cursor.moveToNext()) {
      goodsGroups.add(createEntityFromCursor(cursor));
    }
    cursor.close();
    return goodsGroups;
  }

  @Override
  public GoodsGroup retrieveByBackendId(Long goodsGroupBackendId) {
    CommerDatabaseHelper databaseHelper = CommerDatabaseHelper.getInstance(getContext());
    SQLiteDatabase db = databaseHelper.getReadableDatabase();
    String selection = GoodsGroup.COL_BACKEND_ID + " = ?";
    String[] args = {String.valueOf(goodsGroupBackendId)};
    Cursor cursor = db.query(getTableName(), getProjection(), selection, args, null, null, null);
    GoodsGroup entity = null;
    if (cursor.moveToFirst()) {
      entity = createEntityFromCursor(cursor);
    }
    cursor.close();
    return entity;
  }
}
