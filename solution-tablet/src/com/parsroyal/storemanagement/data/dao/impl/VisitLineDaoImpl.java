package com.parsroyal.storemanagement.data.dao.impl;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.parsroyal.storemanagement.data.dao.VisitLineDao;
import com.parsroyal.storemanagement.data.entity.VisitLine;
import com.parsroyal.storemanagement.data.helper.CommerDatabaseHelper;
import com.parsroyal.storemanagement.data.listmodel.VisitLineListModel;
import com.parsroyal.storemanagement.data.model.LabelValue;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by Mahyar on 7/6/2015.
 */
public class VisitLineDaoImpl extends AbstractDao<VisitLine, Long> implements VisitLineDao {

  private Context context;

  public VisitLineDaoImpl(Context context) {
    this.context = context;
  }

  @Override
  protected Context getContext() {
    return context;
  }

  @Override
  protected ContentValues getContentValues(VisitLine entity) {
    ContentValues contentValues = new ContentValues();
    contentValues.put(VisitLine.COL_ID, entity.getId());
    contentValues.put(VisitLine.COL_BACKEND_ID, entity.getBackendId());
    contentValues.put(VisitLine.COL_CODE, entity.getCode());
    contentValues.put(VisitLine.COL_TITLE, entity.getTitle());
    return contentValues;
  }

  @Override
  protected String getTableName() {
    return VisitLine.TABLE_NAME;
  }

  @Override
  protected String getPrimaryKeyColumnName() {
    return VisitLine.COL_ID;
  }

  @Override
  protected String[] getProjection() {
    return new String[]{VisitLine.COL_ID, VisitLine.COL_BACKEND_ID, VisitLine.COL_CODE,
        VisitLine.COL_TITLE};
  }

  @Override
  protected VisitLine createEntityFromCursor(Cursor cursor) {
    VisitLine visitLine = new VisitLine();
    visitLine.setId(cursor.getLong(0));
    visitLine.setBackendId(cursor.getLong(1));
    visitLine.setCode(cursor.getInt(2));
    visitLine.setTitle(cursor.getString(3));
    return visitLine;
  }

  @Override
  public List<VisitLineListModel> getAllVisitLineListModel() {
    CommerDatabaseHelper databaseHelper = CommerDatabaseHelper.getInstance(getContext());
    SQLiteDatabase db = databaseHelper.getReadableDatabase();

    String sql =
        "select vl.BACKEND_ID, vl.CODE, vl.TITLE,count(cu._id) COUNT from COMMER_VISIT_LINE vl " +
            "LEFT OUTER JOIN COMMER_CUSTOMER cu on cu.VISIT_LINE_BACKEND_ID = vl.BACKEND_ID " +
            "GROUP BY vl.BACKEND_ID, vl.CODE, vl.TITLE ORDER BY vl.BACKEND_ID DESC";

    List<VisitLineListModel> entities = new ArrayList<>();
    Cursor cursor = db.rawQuery(sql, null);
    while (cursor.moveToNext()) {
      entities.add(createListModelFromCursor(cursor));
    }
    cursor.close();
    return entities;
  }

  @Override
  public List<VisitLineListModel> getAllVisitLineListModel(Date from, Date to) {
    CommerDatabaseHelper databaseHelper = CommerDatabaseHelper.getInstance(getContext());
    SQLiteDatabase db = databaseHelper.getReadableDatabase();

    String sql = "select vl.BACKEND_ID, vl.CODE, vl.TITLE," +
        "(select count(*) from COMMER_CUSTOMER cu where vl.BACKEND_ID = cu.VISIT_LINE_BACKEND_ID)" +
        " from COMMER_VISIT_LINE vl LEFT JOIN COMMER_VISITLINE_DATE vd on vl.BACKEND_ID = vd.BACKEND_ID"
        + " where " + "BACKEND_DATE_GREGORIAN BETWEEN ? AND ? OR vl.BACKEND_ID = 0 " +
        "group by vl.CODE, vl.TITLE, vl.BACKEND_ID " +
        "ORDER BY vl.BACKEND_ID DESC";

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

    String[] args = {sdf.format(from), sdf.format(to)};

    List<VisitLineListModel> entities = new ArrayList<>();
    Cursor cursor = db.rawQuery(sql, args);

    while (cursor.moveToNext()) {
      entities.add(createListModelFromCursor(cursor));
    }
    cursor.close();
    return entities;
  }

  @Override
  public List<LabelValue> getAllVisitLineLabelValue() {
    CommerDatabaseHelper databaseHelper = CommerDatabaseHelper.getInstance(getContext());
    SQLiteDatabase db = databaseHelper.getReadableDatabase();

    String sql =
        "select vl.BACKEND_ID, vl.CODE, vl.TITLE,count(cu._id) COUNT from COMMER_VISIT_LINE vl " +
            "LEFT OUTER JOIN COMMER_CUSTOMER cu on cu.VISIT_LINE_BACKEND_ID = vl.BACKEND_ID " +
            "GROUP BY vl.BACKEND_ID, vl.CODE, vl.TITLE ORDER BY vl.BACKEND_ID DESC";

    List<LabelValue> entities = new ArrayList<>();
    Cursor cursor = db.rawQuery(sql, null);
    while (cursor.moveToNext()) {
      entities.add(createLabelValueFromCursor(cursor));
    }
    cursor.close();
    return entities;
  }

  @Override
  public VisitLineListModel getVisitLineListModelByBackendId(long visitlineBackendId) {
    CommerDatabaseHelper databaseHelper = CommerDatabaseHelper.getInstance(getContext());
    SQLiteDatabase db = databaseHelper.getReadableDatabase();

    String sql =
        "select vl.BACKEND_ID, vl.CODE, vl.TITLE,count(cu._id) COUNT from COMMER_VISIT_LINE vl " +
            "LEFT OUTER JOIN COMMER_CUSTOMER cu on cu.VISIT_LINE_BACKEND_ID = vl.BACKEND_ID " +
            "where vl.BACKEND_ID = " + visitlineBackendId +
            " GROUP BY vl.BACKEND_ID, vl.CODE, vl.TITLE";

    VisitLineListModel entity = null;
    Cursor cursor = db.rawQuery(sql, null);
    if (cursor.moveToNext()) {
      entity = createListModelFromCursor(cursor);
    }
    cursor.close();
    return entity;
  }

  @Override
  public VisitLine getVisitLineByBackendId(Long backendId) {
    CommerDatabaseHelper databaseHelper = CommerDatabaseHelper.getInstance(getContext());
    SQLiteDatabase db = databaseHelper.getReadableDatabase();
    String selection = VisitLine.COL_BACKEND_ID + " = ?";
    String[] args = {String.valueOf(backendId)};
    Cursor cursor = db.query(getTableName(), getProjection(), selection, args, null, null, null);
    VisitLine entity = null;
    if (cursor.moveToFirst()) {
      entity = createEntityFromCursor(cursor);
    }
    cursor.close();
    return entity;
  }

  private VisitLineListModel createListModelFromCursor(Cursor cursor) {
    VisitLineListModel listModel = new VisitLineListModel();
    listModel.setPrimaryKey(cursor.getLong(0));
    listModel.setCode(cursor.getString(1));
    listModel.setTitle(cursor.getString(2));
    listModel.setCustomerCount(cursor.getInt(3));
    return listModel;
  }

  private LabelValue createLabelValueFromCursor(Cursor cursor) {

    return new LabelValue(cursor.getLong(0), cursor.getString(2), cursor.getString(1));
  }
}
