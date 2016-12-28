package com.conta.comer.data.dao.impl;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.conta.comer.constants.CustomerStatus;
import com.conta.comer.data.dao.CustomerPicDao;
import com.conta.comer.data.entity.CustomerPic;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Arash on 6/6/2016.
 */
public class CustomerPicDaoImpl extends AbstractDao<CustomerPic, Long> implements CustomerPicDao
{

    private Context context;

    public CustomerPicDaoImpl(Context context)
    {
        this.context = context;
    }

    @Override
    protected Context getContext()
    {
        return context;
    }

    @Override
    protected ContentValues getContentValues(CustomerPic entity)
    {
        ContentValues contentValues = new ContentValues();
        contentValues.put(CustomerPic.COL_ID, entity.getId());
        contentValues.put(CustomerPic.COL_BACKEND_ID, entity.getBackendId());
        contentValues.put(CustomerPic.COL_CUSTOMER_BACKEND_ID, entity.getCustomer_backend_id());
        contentValues.put(CustomerPic.COL_TITLE, entity.getTitle());
        contentValues.put(CustomerPic.COL_STATUS, entity.getStatus());
        return contentValues;
    }

    @Override
    protected String getTableName()
    {
        return CustomerPic.TABLE_NAME;
    }

    @Override
    protected String getPrimaryKeyColumnName()
    {
        return CustomerPic.COL_ID;
    }

    @Override
    protected String[] getProjection()
    {
        String[] projection =
                {
                        CustomerPic.COL_ID,
                        CustomerPic.COL_BACKEND_ID,
                        CustomerPic.COL_CUSTOMER_BACKEND_ID,
                        CustomerPic.COL_TITLE,
                        CustomerPic.COL_CREATE_DATE_TIME,
                        CustomerPic.COL_STATUS
                };

        return projection;
    }

    @Override
    protected CustomerPic createEntityFromCursor(Cursor cursor)
    {
        CustomerPic customerPic = new CustomerPic();
        customerPic.setId(cursor.getLong(0));
        customerPic.setBackendId(cursor.getLong(1));
        customerPic.setCustomer_backend_id(cursor.getLong(2));
        customerPic.setTitle(cursor.getString(3));
        customerPic.setCreateDateTime(cursor.getString(4));
        customerPic.setStatus(cursor.getLong(5));
        return customerPic;
    }

    @Override
    public List<String> getAllCustomerPicForSend()
    {
        String selection = " " + CustomerPic.COL_STATUS + " = ? " + " AND (" + CustomerPic.COL_BACKEND_ID
                + " is null or " + CustomerPic.COL_BACKEND_ID + " = 0)";
        String[] args = {String.valueOf(CustomerStatus.NEW.getId())};
        List<CustomerPic> result = retrieveAll(selection, args, null, null, null);
        List<String> retVal = new ArrayList<>();

        for (int i = 0; i < result.size(); i++)
        {
            CustomerPic o = result.get(i);
            retVal.add(o.getTitle());
        }

        return retVal;
    }

}
