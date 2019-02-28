package com.parsroyal.storemanagement.data.dao.impl;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.parsroyal.storemanagement.data.dao.VisitInformationDao;
import com.parsroyal.storemanagement.data.entity.Customer;
import com.parsroyal.storemanagement.data.entity.VisitInformation;
import com.parsroyal.storemanagement.data.helper.CommerDatabaseHelper;
import com.parsroyal.storemanagement.data.model.VisitInformationDto;
import com.parsroyal.storemanagement.data.model.VisitListModel;
import com.parsroyal.storemanagement.util.DateUtil;
import com.parsroyal.storemanagement.util.Empty;
import com.parsroyal.storemanagement.util.NumberUtil;
import com.parsroyal.storemanagement.util.PreferenceHelper;
import com.parsroyal.storemanagement.util.constants.ApplicationKeys;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Arash 2018-01-25
 */
public class VisitInformationDaoImpl extends AbstractDao<VisitInformation, Long> implements
    VisitInformationDao {

  private Context context;

  public VisitInformationDaoImpl(Context context) {
    this.context = context;
  }

  @Override
  protected Context getContext() {
    return context;
  }

  @Override
  protected ContentValues getContentValues(VisitInformation entity) {
    ContentValues cv = new ContentValues();
    cv.put(VisitInformation.COL_ID, entity.getId());
    cv.put(VisitInformation.COL_CUSTOMER_BACKEND_ID, entity.getCustomerBackendId());
    cv.put(VisitInformation.COL_RESULT, entity.getResult());
    cv.put(VisitInformation.COL_VISIT_DATE, entity.getVisitDate());
    cv.put(VisitInformation.COL_VISIT_BACKEND_ID, entity.getVisitBackendId());
    cv.put(VisitInformation.COL_START_TIME, entity.getStartTime());
    cv.put(VisitInformation.COL_END_TIME, entity.getEndTime());
    cv.put(VisitInformation.COL_X_LOCATION, entity.getxLocation());
    cv.put(VisitInformation.COL_Y_LOCATION, entity.getyLocation());
    cv.put(VisitInformation.COL_CREATE_DATE_TIME, entity.getCreateDateTime());
    cv.put(VisitInformation.COL_UPDATE_DATE_TIME, entity.getUpdateDateTime());
    cv.put(VisitInformation.COL_CUSTOMER_ID, entity.getCustomerId());
    cv.put(VisitInformation.COL_NETWORK_DATE, entity.getNetworkDate());
    cv.put(VisitInformation.COL_END_NETWORK_DATE, entity.getEndNetworkDate());
    cv.put(VisitInformation.COL_DISTANCE, entity.getDistance());
    cv.put(VisitInformation.COL_PHONE_VISIT, entity.getPhoneVisit() ? 1 : 0);
    return cv;
  }

  @Override
  protected String getTableName() {
    return VisitInformation.TABLE_NAME;
  }

  @Override
  protected String getPrimaryKeyColumnName() {
    return VisitInformation.COL_ID;
  }

  @Override
  protected String[] getProjection() {
    return new String[]{
        VisitInformation.COL_ID,
        VisitInformation.COL_CUSTOMER_BACKEND_ID,
        VisitInformation.COL_RESULT,
        VisitInformation.COL_VISIT_DATE,
        VisitInformation.COL_VISIT_BACKEND_ID,//4
        VisitInformation.COL_START_TIME,
        VisitInformation.COL_END_TIME,
        VisitInformation.COL_X_LOCATION,
        VisitInformation.COL_Y_LOCATION,
        VisitInformation.COL_CREATE_DATE_TIME,//9
        VisitInformation.COL_UPDATE_DATE_TIME,
        VisitInformation.COL_CUSTOMER_ID,
        VisitInformation.COL_NETWORK_DATE,
        VisitInformation.COL_END_NETWORK_DATE,
        VisitInformation.COL_DISTANCE,//14
        VisitInformation.COL_PHONE_VISIT
    };
  }

  @Override
  protected VisitInformation createEntityFromCursor(Cursor cursor) {
    VisitInformation entity = new VisitInformation();
    entity.setId(cursor.getLong(0));
    entity.setCustomerBackendId(cursor.getLong(1));
    entity.setResult(cursor.isNull(2) ? null : cursor.getLong(2));
    entity.setVisitDate(cursor.getString(3));
    entity.setVisitBackendId(cursor.isNull(4) ? null : cursor.getLong(4));
    entity.setStartTime(cursor.getString(5));
    entity.setEndTime(cursor.getString(6));
    entity.setxLocation(cursor.isNull(7) ? null : cursor.getDouble(7));
    entity.setyLocation(cursor.isNull(8) ? null : cursor.getDouble(8));
    entity.setCreateDateTime(cursor.getString(9));
    entity.setUpdateDateTime(cursor.getString(10));
    entity.setCustomerId(cursor.getLong(11));
    entity.setNetworkDate(cursor.getLong(12));
    entity.setEndNetworkDate(cursor.getLong(13));
    entity.setDistance(cursor.getLong(14));
    entity.setPhoneVisit(cursor.getInt(15) == 1);
    return entity;
  }

  @Override
  public List<VisitInformation> getAllVisitInformationForSend() {
    String selection = " " + VisitInformation.COL_VISIT_BACKEND_ID + " is null or "
        + VisitInformation.COL_VISIT_BACKEND_ID + " = 0";
    return retrieveAll(selection, null, null, null, null);
  }

  @Override
  public void updateLocation(Long visitInformationId, Double lat, Double lng) {
    CommerDatabaseHelper databaseHelper = CommerDatabaseHelper.getInstance(getContext());
    SQLiteDatabase db = databaseHelper.getWritableDatabase();
    db.beginTransaction();
    String whereClause = VisitInformation.COL_ID + " = ?";
    String[] args = {String.valueOf(visitInformationId)};
    ContentValues contentValues = new ContentValues();
    contentValues.put(VisitInformation.COL_X_LOCATION, lat);
    contentValues.put(VisitInformation.COL_Y_LOCATION, lng);
    db.update(getTableName(), contentValues, whereClause, args);
    db.setTransactionSuccessful();
    db.endTransaction();
  }

  @Override
  public VisitInformation retrieveForNewCustomer(Long customerId) {
    CommerDatabaseHelper databaseHelper = CommerDatabaseHelper.getInstance(getContext());
    SQLiteDatabase db = databaseHelper.getReadableDatabase();
    String selection =
        VisitInformation.COL_CUSTOMER_ID + " = ? AND " + VisitInformation.COL_RESULT + " = -1 ";
    String[] args = {String.valueOf(customerId)};
    Cursor cursor = db.query(getTableName(), getProjection(), selection, args, null, null, null);
    VisitInformation visitInformationList = null;// = new VisitInformation();
    if (cursor.moveToNext()) {
      visitInformationList = createEntityFromCursor(cursor);
    }
    cursor.close();
    return visitInformationList;

  }

  @Override
  public List<VisitInformationDto> getAllVisitInformationDtoForSend(Long visitId) {
    CommerDatabaseHelper databaseHelper = CommerDatabaseHelper.getInstance(getContext());
    SQLiteDatabase db = databaseHelper.getReadableDatabase();
    String selection = " (" + VisitInformation.COL_VISIT_BACKEND_ID + " is null or "
        + VisitInformation.COL_VISIT_BACKEND_ID + " = 0) ";
    if (Empty.isNotEmpty(visitId)) {
      selection = selection + " AND " + VisitInformation.COL_ID + " = " + visitId;
    }
    Cursor cursor = db.query(getTableName(), getProjection(), selection, null, null, null, null);
    List<VisitInformationDto> visitInformationList = new ArrayList<>();
    while (cursor.moveToNext()) {
      visitInformationList.add(createDtoFromCursor(cursor));
    }
    cursor.close();
    return visitInformationList;
  }

  @Override
  public List<VisitListModel> getAllVisitList() {
    CommerDatabaseHelper databaseHelper = CommerDatabaseHelper.getInstance(getContext());
    SQLiteDatabase db = databaseHelper.getReadableDatabase();

    String[] projection = new String[]{
        "vi." + VisitInformation.COL_ID,
        VisitInformation.COL_VISIT_DATE,
        VisitInformation.COL_START_TIME,
        VisitInformation.COL_END_TIME,
        VisitInformation.COL_NETWORK_DATE,
        VisitInformation.COL_END_NETWORK_DATE,//5
        VisitInformation.COL_DISTANCE,
        VisitInformation.COL_PHONE_VISIT,
        Customer.COL_FULL_NAME,
        VisitInformation.COL_VISIT_BACKEND_ID,
        Customer.COL_CODE//10
    };

    String table = getTableName() + " vi " +
        "JOIN " + Customer.TABLE_NAME + " c on c." + Customer.COL_BACKEND_ID + " = vi."
        + VisitInformation.COL_CUSTOMER_BACKEND_ID;

    String orderBy = "vi." + VisitInformation.COL_ID + " DESC ";

    String groupBy = "vi." + VisitInformation.COL_ID;
    Cursor cursor = db.query(table, projection, null, null, groupBy, null, orderBy);
    List<VisitListModel> visitInformationList = new ArrayList<>();
    while (cursor.moveToNext()) {
      visitInformationList.add(createModelFromCursor(cursor));
    }
    cursor.close();
    return visitInformationList;
  }

  @Override
  public void clearAllSent() {
    CommerDatabaseHelper databaseHelper = CommerDatabaseHelper.getInstance(getContext());
    SQLiteDatabase db = databaseHelper.getWritableDatabase();
    db.beginTransaction();
    db.execSQL(String.format("DELETE FROM %s WHERE %s is not null", getTableName(),
        VisitInformation.COL_VISIT_BACKEND_ID));
    db.setTransactionSuccessful();
    db.endTransaction();
  }

  private VisitInformationDto createDtoFromCursor(Cursor cursor) {
    VisitInformationDto entity = new VisitInformationDto();
    entity.setId(cursor.getLong(0));
    entity.setCustomerBackendId(cursor.getLong(1));
    String visitDate = cursor.getString(3);
    //Hot patch
    if (visitDate.contains("")) {

    }
    entity.setVisitDate(visitDate);
    entity.setStartTime(cursor.getString(5));
    entity.setEndTime(cursor.getString(6));
    entity.setxLocation(cursor.isNull(7) ? null : cursor.getDouble(7));
    entity.setyLocation(cursor.isNull(8) ? null : cursor.getDouble(8));
    entity.setSalesmanId(
        Long.valueOf(PreferenceHelper.retrieveByKey(ApplicationKeys.SALESMAN_ID).getValue()));
    Long networkDate = cursor.getLong(12);
    entity.setNetworkDate(NumberUtil.digitsToEnglish(DateUtil.getZonedDate(new Date(networkDate))));
    entity.setEndNetworkDate(
        NumberUtil.digitsToEnglish(DateUtil.getZonedDate(new Date(cursor.getLong(13)))));
    entity.setDistance(cursor.getLong(14));
    entity.setPhoneVisit(cursor.getInt(15) == 1);

    return entity;
  }

  private VisitListModel createModelFromCursor(Cursor cursor) {
    VisitListModel entity = new VisitListModel();
    entity.setId(cursor.getLong(0));
    entity.setVisitDate(cursor.getString(1));
    entity.setStartTime(cursor.getString(2));
    entity.setEndTime(cursor.getString(3));
    entity.setNetworkDate(
        NumberUtil.digitsToEnglish(DateUtil.getZonedDate(new Date(cursor.getLong(4)))));
    entity.setEndNetworkDate(
        NumberUtil.digitsToEnglish(DateUtil.getZonedDate(new Date(cursor.getLong(5)))));
    entity.setDistance(cursor.getLong(6));
    entity.setPhoneVisit(cursor.getInt(7) == 1);
    entity.setCustomer(cursor.getString(8));
    long status = cursor.getLong(9);

    entity.setSent(status != 0L);
    entity.setCustomerCode(cursor.getString(10));
    return entity;
  }
}
