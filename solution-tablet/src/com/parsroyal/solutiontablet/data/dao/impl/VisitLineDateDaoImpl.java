package com.parsroyal.solutiontablet.data.dao.impl;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import com.parsroyal.solutiontablet.data.dao.VisitLineDateDao;
import com.parsroyal.solutiontablet.data.entity.VisitLineDate;
import com.parsroyal.solutiontablet.data.listmodel.VisitLineListModel;

/**
 * Created by Arash
 */
public class VisitLineDateDaoImpl extends AbstractDao<VisitLineDate, Long> implements
    VisitLineDateDao {

  private Context context;

  public VisitLineDateDaoImpl(Context context) {
    this.context = context;
  }

  @Override
  protected Context getContext() {
    return context;
  }

  @Override
  protected ContentValues getContentValues(VisitLineDate entity) {
    ContentValues contentValues = new ContentValues();
    contentValues.put(VisitLineDate.COL_ID, entity.getId());
    contentValues.put(VisitLineDate.COL_BACKEND_ID, entity.getBackendId());
    contentValues.put(VisitLineDate.COL_BACKEND_DATE, entity.getBackendDate());
    contentValues.put(VisitLineDate.COL_BACKEND_DATE_GREGORIAN, entity.getBackendDateGregorian());
    return contentValues;
  }

  @Override
  protected String getTableName() {
    return VisitLineDate.TABLE_NAME;
  }

  @Override
  protected String getPrimaryKeyColumnName() {
    return VisitLineDate.COL_ID;
  }

  @Override
  protected String[] getProjection() {
    return new String[]{
        VisitLineDate.COL_ID,
        VisitLineDate.COL_BACKEND_ID,
        VisitLineDate.COL_BACKEND_DATE,
        VisitLineDate.COL_BACKEND_DATE_GREGORIAN};
  }

  @Override
  protected VisitLineDate createEntityFromCursor(Cursor cursor) {
    VisitLineDate visitLine = new VisitLineDate();
    visitLine.setId(cursor.getLong(0));
    visitLine.setBackendId(cursor.getLong(1));
    visitLine.setBackendDate(cursor.getString(2));
    visitLine.setBackendDateGregorian(cursor.getString(3));
    return visitLine;
  }


  private VisitLineListModel createListModelFromCursor(Cursor cursor) {
    VisitLineListModel listModel = new VisitLineListModel();
    listModel.setPrimaryKey(cursor.getLong(0));
    listModel.setCode(cursor.getString(1));
    listModel.setTitle(cursor.getString(2));
    listModel.setCustomerCount(cursor.getInt(3));
    return listModel;
  }
}
