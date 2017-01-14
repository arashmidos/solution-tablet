package com.conta.comer.data.dao.impl;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;

import com.conta.comer.data.dao.VisitInformationDao;
import com.conta.comer.data.entity.VisitInformation;
import com.conta.comer.data.helper.CommerDatabaseHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mahyar on 7/17/2015.
 */
public class VisitInformationDaoImpl extends AbstractDao<VisitInformation, Long> implements VisitInformationDao
{

    private Context context;

    public VisitInformationDaoImpl(Context context)
    {
        this.context = context;
    }

    @Override
    protected Context getContext()
    {
        return context;
    }

    @Override
    protected ContentValues getContentValues(VisitInformation entity)
    {
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
        return cv;
    }

    @Override
    protected String getTableName()
    {
        return VisitInformation.TABLE_NAME;
    }

    @Override
    protected String getPrimaryKeyColumnName()
    {
        return VisitInformation.COL_ID;
    }

    @Override
    protected String[] getProjection()
    {
        return new String[]{
                VisitInformation.COL_ID,
                VisitInformation.COL_CUSTOMER_BACKEND_ID,
                VisitInformation.COL_RESULT,
                VisitInformation.COL_VISIT_DATE,
                VisitInformation.COL_VISIT_BACKEND_ID,
                VisitInformation.COL_START_TIME,
                VisitInformation.COL_END_TIME,
                VisitInformation.COL_X_LOCATION,
                VisitInformation.COL_Y_LOCATION,
                VisitInformation.COL_CREATE_DATE_TIME,
                VisitInformation.COL_UPDATE_DATE_TIME,
                VisitInformation.COL_CUSTOMER_ID
        };
    }

    @Override
    protected VisitInformation createEntityFromCursor(Cursor cursor)
    {
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
        return entity;
    }

    @Override
    public List<VisitInformation> getAllVisitInformationForSend()
    {
        String selection = " " + VisitInformation.COL_VISIT_BACKEND_ID + " is null or " + VisitInformation.COL_VISIT_BACKEND_ID + " = 0";
        return retrieveAll(selection, null, null, null, null);
    }

    @Override
    public void updateLocation(Long visitInformationId, Location location)
    {
        CommerDatabaseHelper databaseHelper = CommerDatabaseHelper.getInstance(getContext());
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        db.beginTransaction();
        String whereClause = VisitInformation.COL_ID + " = ?";
        String[] args = {String.valueOf(visitInformationId)};
        ContentValues contentValues = new ContentValues();
        contentValues.put(VisitInformation.COL_X_LOCATION, location.getLatitude());
        contentValues.put(VisitInformation.COL_Y_LOCATION, location.getLongitude());
        db.update(getTableName(), contentValues, whereClause, args);
        db.setTransactionSuccessful();
        db.endTransaction();
        db.close();
    }

    @Override
    public VisitInformation retrieveForNewCustomer(Long customerId)
    {
        CommerDatabaseHelper databaseHelper = CommerDatabaseHelper.getInstance(getContext());
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        String selection = VisitInformation.COL_CUSTOMER_ID + " = ? AND " + VisitInformation.COL_RESULT + " = -1 ";
        String[] args = {String.valueOf(customerId)};
        Cursor cursor = db.query(getTableName(), getProjection(), selection, args, null, null, null);
        VisitInformation visitInformationList=null;// = new VisitInformation();
        if (cursor.moveToNext())
        {
            visitInformationList=createEntityFromCursor(cursor);
        }
        cursor.close();
        db.close();
        return visitInformationList;

    }
}
