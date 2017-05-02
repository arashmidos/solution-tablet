package com.parsroyal.solutiontablet.data.dao.impl;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.parsroyal.solutiontablet.constants.SendStatus;
import com.parsroyal.solutiontablet.data.dao.PaymentDao;
import com.parsroyal.solutiontablet.data.entity.Customer;
import com.parsroyal.solutiontablet.data.entity.Payment;
import com.parsroyal.solutiontablet.data.helper.CommerDatabaseHelper;
import com.parsroyal.solutiontablet.data.listmodel.PaymentListModel;
import com.parsroyal.solutiontablet.data.searchobject.PaymentSO;
import com.parsroyal.solutiontablet.util.Empty;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Arash on 11/8/2016.
 */
public class PaymentDaoImpl extends AbstractDao<Payment, Long> implements PaymentDao
{

    private Context context;

    public PaymentDaoImpl(Context context)
    {
        this.context = context;
    }

    @Override
    protected Context getContext()
    {
        return context;
    }

    @Override
    protected ContentValues getContentValues(Payment entity)
    {
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
    protected String getTableName()
    {
        return Payment.TABLE_NAME;
    }

    @Override
    protected String getPrimaryKeyColumnName()
    {
        return Payment.COL_ID;
    }

    @Override
    protected String[] getProjection()
    {
        String[] projection = {
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
        return projection;
    }

    @Override
    protected Payment createEntityFromCursor(Cursor cursor)
    {
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
    public Payment getPaymentById(Long paymentID)
    {
        CommerDatabaseHelper databaseHelper = CommerDatabaseHelper.getInstance(getContext());
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        String selection = getPrimaryKeyColumnName() + " = ?";
        String[] args = {String.valueOf(paymentID)};
        Cursor cursor = db.query(getTableName(), getProjection(), selection, args, null, null, null);
        Payment payment = null;
        if (cursor.moveToFirst())
        {
            payment = createEntityFromCursor(cursor);
        }
        cursor.close();
        return payment;
    }

    @Override
    public List<PaymentListModel> getAllPaymentListModelByCustomerIdWithConstraint(Long customerBackendId, String constraint)
    {
        CommerDatabaseHelper databaseHelper = CommerDatabaseHelper.getInstance(getContext());
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        String[] projection = {Payment.COL_ID, Payment.COL_AMOUNT, Payment.COL_CREATE_DATE_TIME,
                Payment.COL_PAYMENT_TYPE_ID, Payment.COL_CUSTOMER_BACKEND_ID};
        String selection = " " + Payment.COL_CUSTOMER_BACKEND_ID + " = ? ";
        String[] args = {String.valueOf(customerBackendId)};
        Cursor cursor = null;
        if (Empty.isNotEmpty(constraint))
        {
//            selection = selection + " and ( " +
//                    Payment.COL_FULL_NAME + " like ? or " + Customer.COL_ADDRESS + " like ? or " +
//                    Customer.COL_PHONE_NUMBER + " like ? or " + Customer.COL_CELL_PHONE + " like ? or " + Customer.COL_CODE + " like ? )";
//            constraint = "%" + constraint + "%";
//            String[] args2 = {String.valueOf(visitLineId), constraint, constraint, constraint, constraint, constraint};
//            cursor = db.query(getTableName(), projection, selection, args2, null, null, null);
        } else
        {
            cursor = db.query(getTableName(), projection, selection, args, null, null, null);
        }

        List<PaymentListModel> entities = new ArrayList<>();
        while (cursor.moveToNext())
        {
            entities.add(createListModelFromCursor(cursor, true));
        }
        cursor.close();
        return entities;
    }

    private PaymentListModel createListModelFromCursor(Cursor cursor, boolean simpleModel)
    {
        PaymentListModel paymentListModel = new PaymentListModel();
        paymentListModel.setPrimaryKey(cursor.getLong(0));
        paymentListModel.setAmount(String.valueOf(cursor.getLong(1)));
        paymentListModel.setDate(cursor.getString(2));
        paymentListModel.setType(cursor.getString(3));
        paymentListModel.setCustomerBackendId(cursor.getLong(4));
        if (!simpleModel)
        {
            paymentListModel.setCustomerFullName(cursor.getString(5));
        }
        return paymentListModel;
    }

    @Override
    public List<Payment> findPaymentsByStatusId(Long statusId)
    {
        CommerDatabaseHelper databaseHelper = CommerDatabaseHelper.getInstance(getContext());
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        String selection = Payment.COL_STATUS + " = ?";
        String[] args = {String.valueOf(statusId)};
        Cursor cursor = db.query(getTableName(), getProjection(), selection, args, null, null, null);

        List<Payment> paymentList = new ArrayList<>();

        while (cursor.moveToNext())
        {
            paymentList.add(createEntityFromCursor(cursor));
        }

        cursor.close();
        return paymentList;
    }

    @Override
    public List<Payment> findPaymentByCustomerId(Long customerID)
    {
        CommerDatabaseHelper databaseHelper = CommerDatabaseHelper.getInstance(getContext());
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        String selection = Payment.COL_CUSTOMER_BACKEND_ID + " = ? ";
        String[] args = {String.valueOf(customerID)};
        Cursor cursor = db.query(getTableName(), getProjection(), selection, args, null, null, null);

        List<Payment> payments = new ArrayList<>();
        while (cursor.moveToNext())
        {
            payments.add(createEntityFromCursor(cursor));
        }
        cursor.close();
        return payments;
    }

    @Override
    public List<PaymentListModel> searchForPayments(PaymentSO paymentSO)
    {
        CommerDatabaseHelper databaseHelper = CommerDatabaseHelper.getInstance(getContext());
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        String[] projection = {
                "p." + Payment.COL_ID,
                "p." + Payment.COL_AMOUNT,
                "p." + Payment.COL_CREATE_DATE_TIME,
                "p." + Payment.COL_PAYMENT_TYPE_ID,
                "p." + Payment.COL_CUSTOMER_BACKEND_ID,
                "cu." + Customer.COL_FULL_NAME};

        String selection = " " + "p." + Payment.COL_CUSTOMER_BACKEND_ID + " = ? and " + "p." + Payment.COL_STATUS + " = ? ";
        String[] args = {String.valueOf(paymentSO.getCustomerBackendId()), String.valueOf(paymentSO.getSent())};
        //If it's a global search
        if (paymentSO.getCustomerBackendId() == null || paymentSO.getCustomerBackendId() == -1)
        {
            Log.d("Payment.SearchAll", "Done");
            selection = " p." + Payment.COL_STATUS + " = ? ";
            args = new String[1];
            args[0] = String.valueOf(paymentSO.getSent());
        }

        String table = getTableName() + " p " +
                " INNER JOIN " + Customer.TABLE_NAME + " cu on p." + Payment.COL_CUSTOMER_BACKEND_ID +
                " = cu." + Customer.COL_BACKEND_ID;

        String groupBy = " p." + Payment.COL_ID;

        Cursor cursor = db.query(table, projection, selection, args, groupBy, null, null);

        List<PaymentListModel> entities = new ArrayList<>();
        while (cursor.moveToNext())
        {
            entities.add(createListModelFromCursor(cursor, false));
        }
        cursor.close();
        return entities;
    }

    @Override
    public void deleteAllSentPayment()
    {
        CommerDatabaseHelper databaseHelper = CommerDatabaseHelper.getInstance(getContext());
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        db.beginTransaction();
        db.execSQL(String.format("DELETE FROM %s WHERE %s = %s"
                , getTableName(), Payment.COL_STATUS, SendStatus.SENT.getId()));
        db.setTransactionSuccessful();
        db.endTransaction();
    }
}
