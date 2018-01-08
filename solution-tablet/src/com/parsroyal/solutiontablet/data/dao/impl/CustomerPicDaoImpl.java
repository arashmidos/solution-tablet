package com.parsroyal.solutiontablet.data.dao.impl;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import com.parsroyal.solutiontablet.constants.CustomerStatus;
import com.parsroyal.solutiontablet.data.dao.CustomerPicDao;
import com.parsroyal.solutiontablet.data.entity.CustomerPic;
import com.parsroyal.solutiontablet.data.entity.VisitInformationDetail;
import com.parsroyal.solutiontablet.data.helper.CommerDatabaseHelper;
import com.parsroyal.solutiontablet.data.searchobject.CustomerPictureSO;
import com.parsroyal.solutiontablet.util.Empty;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Arash on 6/6/2016.
 */
public class CustomerPicDaoImpl extends AbstractDao<CustomerPic, Long> implements CustomerPicDao {

  private Context context;

  public CustomerPicDaoImpl(Context context) {
    this.context = context;
  }

  @Override
  protected Context getContext() {
    return context;
  }

  @Override
  protected ContentValues getContentValues(CustomerPic entity) {
    ContentValues contentValues = new ContentValues();
    contentValues.put(CustomerPic.COL_ID, entity.getId());
    contentValues.put(CustomerPic.COL_BACKEND_ID, entity.getBackendId());
    contentValues.put(CustomerPic.COL_CUSTOMER_BACKEND_ID, entity.getCustomerBackendId());
    contentValues.put(CustomerPic.COL_TITLE, entity.getTitle());
    contentValues.put(CustomerPic.COL_STATUS, entity.getStatus());
    contentValues.put(CustomerPic.COL_VISIT_ID, entity.getVisitId());
    return contentValues;
  }

  @Override
  protected String getTableName() {
    return CustomerPic.TABLE_NAME;
  }

  @Override
  protected String getPrimaryKeyColumnName() {
    return CustomerPic.COL_ID;
  }

  @Override
  protected String[] getProjection() {

    return new String[]{
        CustomerPic.COL_ID,
        CustomerPic.COL_BACKEND_ID,
        CustomerPic.COL_CUSTOMER_BACKEND_ID,
        CustomerPic.COL_TITLE,
        CustomerPic.COL_CREATE_DATE_TIME,
        CustomerPic.COL_STATUS,
        CustomerPic.COL_VISIT_ID
    };
  }

  @Override
  protected CustomerPic createEntityFromCursor(Cursor cursor) {
    CustomerPic customerPic = new CustomerPic();
    customerPic.setId(cursor.getLong(0));
    customerPic.setBackendId(cursor.getLong(1));
    customerPic.setCustomerBackendId(cursor.getLong(2));
    customerPic.setTitle(cursor.getString(3));
    customerPic.setCreateDateTime(cursor.getString(4));
    customerPic.setStatus(cursor.getLong(5));
    customerPic.setVisitId(cursor.getLong(6));
    return customerPic;
  }

  @Override
  public List<String> getAllCustomerPicForSend() {
    String selection =
        " " + CustomerPic.COL_STATUS + " = ? " + " AND (" + CustomerPic.COL_BACKEND_ID
            + " is null or " + CustomerPic.COL_BACKEND_ID + " = 0)";
    String[] args = {String.valueOf(CustomerStatus.NEW.getId())};
    List<CustomerPic> result = retrieveAll(selection, args, null, null, null);
    List<String> retVal = new ArrayList<>();

    for (int i = 0; i < result.size(); i++) {
      CustomerPic o = result.get(i);
      retVal.add(o.getTitle());
    }

    return retVal;
  }

  @Override
  public List<String> getAllCustomerPicForSendByVisitId(Long visitId) {
    String selection =
        " " + CustomerPic.COL_STATUS + " = ? " + " AND " + CustomerPic.COL_VISIT_ID + " = ? ";
    String[] args = {String.valueOf(CustomerStatus.NEW.getId()), String.valueOf(visitId)};
    List<CustomerPic> result = retrieveAll(selection, args, null, null, null);
    List<String> retVal = new ArrayList<>();

    for (int i = 0; i < result.size(); i++) {
      CustomerPic o = result.get(i);
      retVal.add(o.getTitle());
    }

    return retVal;
  }

  @Override
  public List<String> getAllCustomerPicForSendByCustomerId(Long customerId) {
    String selection =
        " " + CustomerPic.COL_STATUS + " = ? " + " AND " + CustomerPic.COL_CUSTOMER_ID + " = ? ";
    String[] args = {String.valueOf(CustomerStatus.NEW.getId()), String.valueOf(customerId)};
    List<CustomerPic> result = retrieveAll(selection, args, null, null, null);
    List<String> retVal = new ArrayList<>();

    for (int i = 0; i < result.size(); i++) {
      CustomerPic o = result.get(i);
      retVal.add(o.getTitle());
    }

    return retVal;
  }

  @Override
  public List<CustomerPic> getAllCustomerPicturesByBackendId(long customerBackendId) {
    String selection = " " + CustomerPic.COL_CUSTOMER_BACKEND_ID + " = ? ";
    String[] args = {String.valueOf(customerBackendId)};

    return retrieveAll(selection, args, null, null, null);
  }

  @Override
  public List<CustomerPic> findCustomerPictures(CustomerPictureSO customerPictureSO) {
    String selection = " ";
    if (Empty.isNotEmpty(customerPictureSO.getVisitId())) {
      selection = " " + CustomerPic.COL_VISIT_ID + " = ? ";
    }
    String[] args = {String.valueOf(customerPictureSO.getVisitId())};

    return retrieveAll(selection, args, null, null, null);
  }

  @Override
  public void updateAllPictures() {
    CommerDatabaseHelper databaseHelper = CommerDatabaseHelper.getInstance(context);
    SQLiteDatabase db = databaseHelper.getWritableDatabase();
    db.beginTransaction();
    String whereClause =
        CustomerPic.COL_STATUS + " = ? ";
    String[] args = {String.valueOf(CustomerStatus.NEW.getId())};
    ContentValues contentValues = new ContentValues();
    contentValues.put(VisitInformationDetail.COL_STATUS, CustomerStatus.SENT.getId());
    int rows = db.update(getTableName(), contentValues, whereClause, args);
    Log.d("VisitDetailUpdate", "row updated " + rows);
    db.setTransactionSuccessful();
    db.endTransaction();
  }

  @Override
  public void updatePicturesByVisitId(Long visitId) {
    CommerDatabaseHelper databaseHelper = CommerDatabaseHelper.getInstance(context);
    SQLiteDatabase db = databaseHelper.getWritableDatabase();
    db.beginTransaction();
    String whereClause =
        CustomerPic.COL_STATUS + " = ? AND " + CustomerPic.COL_VISIT_ID + " = ?";
    String[] args = {String.valueOf(CustomerStatus.NEW.getId()), String.valueOf(visitId)};
    ContentValues contentValues = new ContentValues();
    contentValues.put(VisitInformationDetail.COL_STATUS, CustomerStatus.SENT.getId());
    int rows = db.update(getTableName(), contentValues, whereClause, args);
    Log.d("VisitDetailUpdate", "row updated " + rows);
    db.setTransactionSuccessful();
    db.endTransaction();
  }

  @Override
  public void updateAllPicturesByCustomerId(Long customerId) {
    CommerDatabaseHelper databaseHelper = CommerDatabaseHelper.getInstance(context);
    SQLiteDatabase db = databaseHelper.getWritableDatabase();
    db.beginTransaction();
    String whereClause =
        CustomerPic.COL_STATUS + " = ? AND " + CustomerPic.COL_CUSTOMER_ID + " = ?";
    String[] args = {String.valueOf(CustomerStatus.NEW.getId()), String.valueOf(customerId)};
    ContentValues contentValues = new ContentValues();
    contentValues.put(VisitInformationDetail.COL_STATUS, CustomerStatus.SENT.getId());
    int rows = db.update(getTableName(), contentValues, whereClause, args);
    Log.d("VisitDetailUpdate", "row updated " + rows);
    db.setTransactionSuccessful();
    db.endTransaction();
  }

  @Override
  public void delete(String title, long customerId) {
    CommerDatabaseHelper databaseHelper = CommerDatabaseHelper.getInstance(getContext());
    SQLiteDatabase db = databaseHelper.getWritableDatabase();
    String sql =
        "DELETE FROM " + CustomerPic.TABLE_NAME + " WHERE " + CustomerPic.COL_TITLE + " = ? AND "
            + CustomerPic.COL_CUSTOMER_ID + " = ?";
    String[] args = {title, String.valueOf(customerId)};
    db.beginTransaction();
    db.rawQuery(sql, args);
    db.setTransactionSuccessful();
    db.endTransaction();
  }
}
