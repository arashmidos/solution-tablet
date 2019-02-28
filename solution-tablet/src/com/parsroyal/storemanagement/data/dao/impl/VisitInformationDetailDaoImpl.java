package com.parsroyal.storemanagement.data.dao.impl;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import com.parsroyal.storemanagement.constants.SendStatus;
import com.parsroyal.storemanagement.constants.VisitInformationDetailType;
import com.parsroyal.storemanagement.data.dao.VisitInformationDetailDao;
import com.parsroyal.storemanagement.data.entity.VisitInformationDetail;
import com.parsroyal.storemanagement.data.helper.CommerDatabaseHelper;
import com.parsroyal.storemanagement.data.model.VisitInformationDetailDto;
import com.parsroyal.storemanagement.data.searchobject.VisitInformationDetailSO;
import com.parsroyal.storemanagement.util.Empty;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Arash 2017-03-08
 */
public class VisitInformationDetailDaoImpl extends AbstractDao<VisitInformationDetail, Long>
    implements VisitInformationDetailDao {

  private Context context;

  public VisitInformationDetailDaoImpl(Context context) {
    this.context = context;
  }

  @Override
  protected Context getContext() {
    return context;
  }

  @Override
  protected ContentValues getContentValues(VisitInformationDetail entity) {
    ContentValues cv = new ContentValues();
    cv.put(VisitInformationDetail.COL_ID, entity.getId());
    cv.put(VisitInformationDetail.COL_TYPE, entity.getType());
    cv.put(VisitInformationDetail.COL_TYPE_ID, entity.getTypeId());
    cv.put(VisitInformationDetail.COL_EXTRA_DATA, entity.getExtraData());
    cv.put(VisitInformationDetail.COL_VISIT_INFORMATION_ID, entity.getVisitInformationId());
    cv.put(VisitInformationDetail.COL_CREATE_DATE_TIME, entity.getCreateDateTime());
    cv.put(VisitInformationDetail.COL_UPDATE_DATE_TIME, entity.getUpdateDateTime());
    cv.put(VisitInformationDetail.COL_STATUS, entity.getStatus());
    return cv;
  }

  @Override
  protected String getTableName() {
    return VisitInformationDetail.TABLE_NAME;
  }

  @Override
  protected String getPrimaryKeyColumnName() {
    return VisitInformationDetail.COL_ID;
  }

  @Override
  protected String[] getProjection() {
    return new String[]{
        VisitInformationDetail.COL_ID,
        VisitInformationDetail.COL_TYPE,
        VisitInformationDetail.COL_TYPE_ID,
        VisitInformationDetail.COL_EXTRA_DATA,
        VisitInformationDetail.COL_VISIT_INFORMATION_ID,
        VisitInformationDetail.COL_CREATE_DATE_TIME,
        VisitInformationDetail.COL_UPDATE_DATE_TIME,
        VisitInformationDetail.COL_STATUS
    };
  }

  @Override
  protected VisitInformationDetail createEntityFromCursor(Cursor cursor) {
    VisitInformationDetail entity = new VisitInformationDetail();
    entity.setId(cursor.getLong(0));
    entity.setType(cursor.getLong(1));
    entity.setTypeId(cursor.getLong(2));
    entity.setExtraData(cursor.getString(3));
    entity.setVisitInformationId(cursor.getLong(4));
    entity.setCreateDateTime(cursor.getString(5));
    entity.setUpdateDateTime(cursor.getString(6));
    entity.setStatus(cursor.getLong(7));
    return entity;
  }

  @Override
  public List<VisitInformationDetail> getAllVisitDetail(Long visitId) {
    String selection = " " + VisitInformationDetail.COL_VISIT_INFORMATION_ID + " = ?";
    String[] args = {String.valueOf(visitId)};

    return retrieveAll(selection, args, null, null, null);
  }

  @Override
  public void deleteVisitDetail(VisitInformationDetailType type, Long typeId) {
    CommerDatabaseHelper databaseHelper = CommerDatabaseHelper.getInstance(getContext());
    SQLiteDatabase db = databaseHelper.getWritableDatabase();
    db.beginTransaction();
    db.execSQL(String.format("DELETE FROM %s WHERE %s = %s AND %s = %s", getTableName(),
        VisitInformationDetail.COL_TYPE, type.getValue(), VisitInformationDetail.COL_TYPE_ID,
        typeId));
    db.setTransactionSuccessful();
    db.endTransaction();
  }


  @Override
  public void updateVisitDetailId(VisitInformationDetailType type, long id, long backendId) {
    CommerDatabaseHelper databaseHelper = CommerDatabaseHelper.getInstance(context);
    SQLiteDatabase db = databaseHelper.getWritableDatabase();
    db.beginTransaction();
    String whereClause =
        VisitInformationDetail.COL_TYPE + " = ? AND " + VisitInformationDetail.COL_TYPE_ID
            + " = ? AND " + VisitInformationDetail.COL_STATUS + " <> ? ";
    String[] args = {String.valueOf(type.getValue()), String.valueOf(id),
        String.valueOf(SendStatus.UPDATED.getId())};
    ContentValues contentValues = new ContentValues();
    contentValues.put(VisitInformationDetail.COL_TYPE_ID, backendId);
    contentValues.put(VisitInformationDetail.COL_STATUS, SendStatus.UPDATED.getId());
    int rows = db.update(getTableName(), contentValues, whereClause, args);
    Log.d("VisitDetailUpdate", "row updated " + rows);
    db.setTransactionSuccessful();
    db.endTransaction();

  }

  @Override
  public List<VisitInformationDetail> search(Long visitId, VisitInformationDetailType type,
      Long typeId) {
    String selection = " " + VisitInformationDetail.COL_VISIT_INFORMATION_ID + " = ? and " +
        VisitInformationDetail.COL_TYPE + " = ? and " + VisitInformationDetail.COL_TYPE_ID
        + " = ? ";

    String[] args = {String.valueOf(visitId), String.valueOf(type.getValue()),
        String.valueOf(typeId)};

    return retrieveAll(selection, args, null, null, null);
  }

  @Override
  public List<VisitInformationDetail> search(Long visitId, VisitInformationDetailType type) {
    String selection = " " + VisitInformationDetail.COL_VISIT_INFORMATION_ID + " = ? and " +
        VisitInformationDetail.COL_TYPE + " = ? ";

    String[] args = {String.valueOf(visitId), String.valueOf(type.getValue())};

    return retrieveAll(selection, args, null, null, null);
  }

  @Override
  public List<VisitInformationDetail> search(VisitInformationDetailSO visitInformationDetailSO) {
    String selection = " " + VisitInformationDetail.COL_VISIT_INFORMATION_ID + " = ?";/* and " +
        VisitInformationDetail.COL_TYPE + " = ? ";*/

    String[] args = {String.valueOf(visitInformationDetailSO.getVisitId())};

    String groupBy = null;
    String orderBy = VisitInformationDetail.COL_TYPE;
    if (Empty.isNotEmpty(visitInformationDetailSO.getGroupBy())) {
      groupBy = visitInformationDetailSO.getGroupBy();
    }
    return retrieveAll(selection, args, groupBy, null, orderBy);
  }

  @Override
  public List<VisitInformationDetailDto> getAllVisitDetailDto(Long visitId) {
    CommerDatabaseHelper databaseHelper = CommerDatabaseHelper.getInstance(getContext());
    SQLiteDatabase db = databaseHelper.getReadableDatabase();
    String selection = " " + VisitInformationDetail.COL_VISIT_INFORMATION_ID + " = ?";
    String[] args = {String.valueOf(visitId)};

    Cursor cursor = db.query(getTableName(), getProjection(), selection, args, null, null, null);
    List<VisitInformationDetailDto> visitInformationDetailList = new ArrayList<>();
    while (cursor.moveToNext()) {
      visitInformationDetailList.add(createDtoFromCursor(cursor));
    }
    cursor.close();
    return visitInformationDetailList;
  }

  private VisitInformationDetailDto createDtoFromCursor(Cursor cursor) {
    VisitInformationDetailDto entity = new VisitInformationDetailDto();
    entity.setId(cursor.getLong(0));
    entity.setType(VisitInformationDetailType.getByValue(cursor.getLong(1)));
    entity.setTypeId(cursor.getLong(2));
    entity.setData("");
    entity.setCustomerVisitId(cursor.getLong(4));
    return entity;
  }
}
