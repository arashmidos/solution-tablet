package com.parsroyal.solutiontablet.data.dao.impl;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.parsroyal.solutiontablet.constants.BaseInfoTypes;
import com.parsroyal.solutiontablet.constants.SendStatus;
import com.parsroyal.solutiontablet.data.dao.PaymentDao;
import com.parsroyal.solutiontablet.data.entity.BaseInfo;
import com.parsroyal.solutiontablet.data.entity.Customer;
import com.parsroyal.solutiontablet.data.entity.Payment;
import com.parsroyal.solutiontablet.data.helper.CommerDatabaseHelper;
import com.parsroyal.solutiontablet.data.listmodel.PaymentListModel;
import com.parsroyal.solutiontablet.data.searchobject.PaymentSO;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Arash on 11/8/2016.
 */
public class PaymentDaoImpl extends AbstractDao<Payment, Long> implements PaymentDao {

  private Context context;

  public PaymentDaoImpl(Context context) {
    this.context = context;
  }

  @Override
  protected Context getContext() {
    return context;
  }

  @Override
  protected ContentValues getContentValues(Payment entity) {
    ContentValues contentValues = new ContentValues();
    contentValues.put(Payment.COL_ID, entity.getId());
    contentValues.put(Payment.COL_CUSTOMER_BACKEND_ID, entity.getCustomerBackendId());
    contentValues.put(Payment.COL_VISIT_BACKEND_ID, entity.getVisitBackendId());
    contentValues.put(Payment.COL_AMOUNT, entity.getAmount());
    contentValues.put(Payment.COL_PAYMENT_TYPE_ID, entity.getPaymentTypeId());
    contentValues.put(Payment.COL_CHEQUE_DATE, entity.getChequeDate());
    contentValues.put(Payment.COL_CHEQUE_ACC_NUMBER, entity.getChequeAccountNumber());
    contentValues.put(Payment.COL_CHEQUE_NUMBER, entity.getChequeNumber());
    contentValues.put(Payment.COL_CHEQUE_BANK, entity.getChequeBank());
    contentValues.put(Payment.COL_CHEQUE_BRANCH, entity.getChequeBranch());
    contentValues.put(Payment.COL_SALESMAN_ID, entity.getSalesmanId());
    contentValues.put(Payment.COL_DESCRIPTION, entity.getDescription());
    contentValues.put(Payment.COL_BACKEND_ID, entity.getBackendId());
    contentValues.put(Payment.COL_STATUS, entity.getStatus());
    contentValues.put(Payment.COL_CREATE_DATE_TIME, entity.getCreateDateTime());
    contentValues.put(Payment.COL_UPDATE_DATE_TIME, entity.getUpdateDateTime());
    contentValues.put(Payment.COL_TRACKING_NO, entity.getTrackingNo());
    contentValues.put(Payment.COL_CHEQUE_OWNER, entity.getChequeOwner());
    return contentValues;
  }

  @Override
  protected String getTableName() {
    return Payment.TABLE_NAME;
  }

  @Override
  protected String getPrimaryKeyColumnName() {
    return Payment.COL_ID;
  }

  @Override
  protected String[] getProjection() {
    return new String[]{
        Payment.COL_ID,
        Payment.COL_CUSTOMER_BACKEND_ID,
        Payment.COL_VISIT_BACKEND_ID,
        Payment.COL_AMOUNT,
        Payment.COL_PAYMENT_TYPE_ID, //4
        Payment.COL_CHEQUE_DATE,
        Payment.COL_CHEQUE_ACC_NUMBER,
        Payment.COL_CHEQUE_NUMBER,
        Payment.COL_CHEQUE_BANK,
        Payment.COL_CHEQUE_BRANCH,//9
        Payment.COL_SALESMAN_ID,
        Payment.COL_DESCRIPTION,
        Payment.COL_BACKEND_ID,
        Payment.COL_STATUS,
        Payment.COL_CREATE_DATE_TIME,//14
        Payment.COL_UPDATE_DATE_TIME,
        Payment.COL_TRACKING_NO,
        Payment.COL_CHEQUE_OWNER
    };
  }

  @Override
  protected Payment createEntityFromCursor(Cursor cursor) {
    Payment payment = new Payment();

    payment.setId(cursor.getLong(0));
    payment.setCustomerBackendId(cursor.getLong(1));
    payment.setVisitBackendId(cursor.getLong(2));
    payment.setAmount(cursor.getLong(3));
    payment.setPaymentTypeId(cursor.getLong(4));
    payment.setChequeDate(cursor.getString(5));
    payment.setChequeAccountNumber(cursor.getString(6));
    payment.setChequeNumber(cursor.getString(7));
    payment.setChequeBank(cursor.getLong(8));
    payment.setChequeBranch(cursor.getString(9));
    payment.setSalesmanId(cursor.getLong(10));
    payment.setDescription(cursor.getString(11));
    payment.setBackendId(cursor.getLong(12));
    payment.setStatus(cursor.getLong(13));
    payment.setCreateDateTime(cursor.getString(14));
    payment.setUpdateDateTime(cursor.getString(15));
    payment.setTrackingNo(cursor.getString(16));
    payment.setChequeOwner(cursor.getString(17));
    return payment;
  }

  @Override
  public Payment getPaymentById(Long paymentID) {
    CommerDatabaseHelper databaseHelper = CommerDatabaseHelper.getInstance(getContext());
    SQLiteDatabase db = databaseHelper.getReadableDatabase();
    String selection = getPrimaryKeyColumnName() + " = ?";
    String[] args = {String.valueOf(paymentID)};
    Cursor cursor = db.query(getTableName(), getProjection(), selection, args, null, null, null);
    Payment payment = null;
    if (cursor.moveToFirst()) {
      payment = createEntityFromCursor(cursor);
    }
    cursor.close();
    return payment;
  }

  private PaymentListModel createListModelFromCursor(Cursor cursor) {
    return new PaymentListModel(cursor.getLong(0),
        String.valueOf(cursor.getLong(1)), cursor.getString(2), cursor.getString(3),
        cursor.getLong(4), cursor.getString(5), cursor.getLong(6), cursor.getString(7),
        cursor.getString(8));
  }

  @Override
  public List<Payment> findPaymentsByStatusId(Long statusId) {
    CommerDatabaseHelper databaseHelper = CommerDatabaseHelper.getInstance(getContext());
    SQLiteDatabase db = databaseHelper.getReadableDatabase();
    String selection = Payment.COL_STATUS + " = ?";
    String[] args = {String.valueOf(statusId)};
    Cursor cursor = db.query(getTableName(), getProjection(), selection, args, null, null, null);

    List<Payment> paymentList = new ArrayList<>();

    while (cursor.moveToNext()) {
      paymentList.add(createEntityFromCursor(cursor));
    }

    cursor.close();
    return paymentList;
  }

  @Override
  public List<Payment> findPaymentsByVisitId(Long visitId) {
    CommerDatabaseHelper databaseHelper = CommerDatabaseHelper.getInstance(getContext());
    SQLiteDatabase db = databaseHelper.getReadableDatabase();
    String selection = Payment.COL_VISIT_BACKEND_ID + " = ? AND " + Payment.COL_STATUS + " = ?";
    String[] args = {String.valueOf(visitId), String.valueOf(SendStatus.NEW.getId())};
    Cursor cursor = db.query(getTableName(), getProjection(), selection, args, null, null, null);

    List<Payment> paymentList = new ArrayList<>();

    while (cursor.moveToNext()) {
      paymentList.add(createEntityFromCursor(cursor));
    }

    cursor.close();
    return paymentList;
  }

  @Override
  public List<PaymentListModel> searchForPayments(PaymentSO paymentSO) {
    CommerDatabaseHelper databaseHelper = CommerDatabaseHelper.getInstance(getContext());
    SQLiteDatabase db = databaseHelper.getReadableDatabase();
    String[] projection = {
        "DISTINCT p." + Payment.COL_ID,
        "p." + Payment.COL_AMOUNT,
        "p." + Payment.COL_CREATE_DATE_TIME,
        "p." + Payment.COL_PAYMENT_TYPE_ID,
        "p." + Payment.COL_CUSTOMER_BACKEND_ID,
        "cu." + Customer.COL_FULL_NAME,//5
        "p." + Payment.COL_STATUS,
        "bi." + BaseInfo.COL_TITLE,
        "p." + Payment.COL_CHEQUE_BRANCH
    };

    List<String> argsList = new ArrayList<>();

    String selection = " 1=1 ";

    if (paymentSO.getCustomerBackendId() != -1L) {
      selection = selection + " AND p." + Payment.COL_CUSTOMER_BACKEND_ID + " = ? ";
      argsList.add(String.valueOf(paymentSO.getCustomerBackendId()));
    }
    if (paymentSO.getStatus().equals(SendStatus.NEW.getId())) {
      selection =
          selection + " AND (p." + Payment.COL_STATUS + " = ? OR p." + Payment.COL_STATUS + " = ?)";
      argsList.add(String.valueOf(SendStatus.NEW.getId()));
      argsList.add(String.valueOf(SendStatus.SENT.getId()));
    } else {
      selection =
          selection + " AND p." + Payment.COL_STATUS + " = ? ";
      argsList.add(String.valueOf(paymentSO.getStatus()));
    }

    String[] args = {};

    String table = getTableName() + " p " +
        " INNER JOIN " + Customer.TABLE_NAME + " cu on p." + Payment.COL_CUSTOMER_BACKEND_ID +
        " = cu." + Customer.COL_BACKEND_ID
        + " LEFT OUTER JOIN " + BaseInfo.TABLE_NAME + " bi on p." + Payment.COL_CHEQUE_BANK +
        " = bi." + BaseInfo.COL_BACKEND_ID + " AND bi." + BaseInfo.COL_TYPE + " = "
        + BaseInfoTypes.BANK_NAME_TYPE.getId();

    Cursor cursor = db
        .query(table, projection, selection, argsList.toArray(args), null, null, null);

    List<PaymentListModel> entities = new ArrayList<>();
    while (cursor.moveToNext()) {
      entities.add(createListModelFromCursor(cursor));
    }
    cursor.close();
    return entities;
  }

  @Override
  public void deleteAllSentPayment() {
    CommerDatabaseHelper databaseHelper = CommerDatabaseHelper.getInstance(getContext());
    SQLiteDatabase db = databaseHelper.getWritableDatabase();
    db.beginTransaction();
    db.execSQL(String.format("DELETE FROM %s WHERE %s = %s"
        , getTableName(), Payment.COL_STATUS, SendStatus.SENT.getId()));
    db.setTransactionSuccessful();
    db.endTransaction();
  }
}
