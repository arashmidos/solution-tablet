package com.parsroyal.solutiontablet.data.dao.impl;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.parsroyal.solutiontablet.constants.BaseInfoTypes;
import com.parsroyal.solutiontablet.data.dao.SaleOrderDao;
import com.parsroyal.solutiontablet.data.entity.BaseInfo;
import com.parsroyal.solutiontablet.data.entity.Customer;
import com.parsroyal.solutiontablet.data.entity.SaleOrder;
import com.parsroyal.solutiontablet.data.helper.CommerDatabaseHelper;
import com.parsroyal.solutiontablet.data.listmodel.SaleOrderListModel;
import com.parsroyal.solutiontablet.data.model.SaleOrderDto;
import com.parsroyal.solutiontablet.data.searchobject.SaleOrderSO;
import com.parsroyal.solutiontablet.util.Empty;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Mahyar on 8/21/2015.
 */
public class SaleOrderDaoImpl extends AbstractDao<SaleOrder, Long> implements SaleOrderDao
{

    private Context context;

    public SaleOrderDaoImpl(Context context)
    {
        this.context = context;
    }

    @Override
    protected Context getContext()
    {
        return context;
    }

    @Override
    protected ContentValues getContentValues(SaleOrder entity)
    {
        ContentValues contentValues = new ContentValues();
        contentValues.put(SaleOrder.COL_ID, entity.getId());
        contentValues.put(SaleOrder.COL_NUMBER, entity.getNumber());
        contentValues.put(SaleOrder.COL_AMOUNT, entity.getAmount());
        contentValues.put(SaleOrder.COL_DATE, entity.getDate());
        contentValues.put(SaleOrder.COL_PAYMENT_TYPE_BACKEND_ID, entity.getPaymentTypeBackendId());
        contentValues.put(SaleOrder.COL_SALESMAN_ID, entity.getSalesmanId());
        contentValues.put(SaleOrder.COL_CUSTOMER_BACKEND_ID, entity.getCustomerBackendId());
        contentValues.put(SaleOrder.COL_DESCRIPTION, entity.getDescription());
        contentValues.put(SaleOrder.COL_STATUS, entity.getStatus());
        contentValues.put(SaleOrder.COL_BACKEND_ID, entity.getBackendId());
        contentValues.put(SaleOrder.COL_INVOICE_BACKEND_ID, entity.getInvoiceBackendId());
        contentValues.put(SaleOrder.COL_CREATE_DATE_TIME, entity.getCreateDateTime());
        contentValues.put(SaleOrder.COL_UPDATE_DATE_TIME, entity.getUpdateDateTime());
        return contentValues;
    }

    @Override
    protected String getTableName()
    {
        return SaleOrder.TABLE_NAME;
    }

    @Override
    protected String getPrimaryKeyColumnName()
    {
        return SaleOrder.COL_ID;
    }

    @Override
    protected String[] getProjection()
    {
        String[] projection = {
                SaleOrder.COL_ID,
                SaleOrder.COL_NUMBER,
                SaleOrder.COL_AMOUNT,
                SaleOrder.COL_DATE,
                SaleOrder.COL_PAYMENT_TYPE_BACKEND_ID,
                SaleOrder.COL_SALESMAN_ID,
                SaleOrder.COL_CUSTOMER_BACKEND_ID,
                SaleOrder.COL_DESCRIPTION,
                SaleOrder.COL_STATUS,
                SaleOrder.COL_BACKEND_ID,
                SaleOrder.COL_INVOICE_BACKEND_ID,
                SaleOrder.COL_CREATE_DATE_TIME,
                SaleOrder.COL_UPDATE_DATE_TIME,
        };
        return projection;
    }

    @Override
    protected SaleOrder createEntityFromCursor(Cursor cursor)
    {
        SaleOrder saleOrder = new SaleOrder();

        saleOrder.setId(cursor.getLong(0));
        saleOrder.setNumber(cursor.getLong(1));
        saleOrder.setAmount(cursor.getLong(2));
        saleOrder.setDate(cursor.getString(3));
        saleOrder.setPaymentTypeBackendId(cursor.getLong(4));
        saleOrder.setSalesmanId(cursor.getLong(5));
        saleOrder.setCustomerBackendId(cursor.getLong(6));
        saleOrder.setDescription(cursor.getString(7));
        saleOrder.setStatus(cursor.getLong(8));
        saleOrder.setBackendId(cursor.getLong(9));
        saleOrder.setInvoiceBackendId(cursor.getLong(10));
        saleOrder.setCreateDateTime(cursor.getString(11));
        saleOrder.setUpdateDateTime(cursor.getString(12));
        return saleOrder;
    }

    protected SaleOrderDto createOrderDtoFromCursor(Cursor cursor)
    {
        SaleOrderDto saleOrder = new SaleOrderDto();

        saleOrder.setId(cursor.getLong(0));
        saleOrder.setNumber(cursor.getLong(1));
        saleOrder.setAmount(cursor.getLong(2));
        saleOrder.setDate(cursor.getString(3));
        saleOrder.setPaymentTypeBackendId(cursor.getLong(4));
        saleOrder.setSalesmanId(cursor.getLong(5));
        saleOrder.setCustomerBackendId(cursor.getLong(6));
        saleOrder.setDescription(cursor.getString(7));
        saleOrder.setStatus(cursor.getLong(8));
        saleOrder.setBackendId(cursor.getLong(9));
        saleOrder.setInvoiceBackendId(cursor.getLong(10));
        saleOrder.setCreateDateTime(cursor.getString(11));
        saleOrder.setUpdateDateTime(cursor.getString(12));
        return saleOrder;
    }

    @Override
    public List<SaleOrder> retrieveSaleOrderByStatus(Long statusId)
    {
        CommerDatabaseHelper databaseHelper = CommerDatabaseHelper.getInstance(getContext());
        SQLiteDatabase db = databaseHelper.getWritableDatabase();

        List<SaleOrder> saleOrders = new ArrayList<>();
        String sql =
                "SELECT * FROM " + SaleOrder.TABLE_NAME + " WHERE " + SaleOrder.COL_STATUS + " = ?";
        String[] args = {String.valueOf(statusId)};

        Cursor cursor = db.rawQuery(sql, args);
        while (cursor.moveToNext())
        {
            saleOrders.add(createEntityFromCursor(cursor));
        }
        cursor.close();

        return saleOrders;
    }

    @Override
    public List<SaleOrderListModel> searchForOrders(SaleOrderSO saleOrderSO)
    {
        CommerDatabaseHelper databaseHelper = CommerDatabaseHelper.getInstance(getContext());
        SQLiteDatabase db = databaseHelper.getWritableDatabase();

        //Temporary solution to get unique result
        Set<SaleOrderListModel> orders = new HashSet<>();
        List<SaleOrderListModel> returnOrders = new ArrayList<>();
        List<String> argsList = new ArrayList<>();
        String sql = "SELECT " +
                "o." + SaleOrder.COL_ID + "," +
                "o." + SaleOrder.COL_BACKEND_ID + "," +
                "o." + SaleOrder.COL_NUMBER + "," +
                "o." + SaleOrder.COL_DATE + "," +
                "o." + SaleOrder.COL_AMOUNT + "," +
                "o." + SaleOrder.COL_STATUS + "," +
                "c." + Customer.COL_FULL_NAME + "," +
                "i." + BaseInfo.COL_TITLE + "," +
                "c." + Customer.COL_BACKEND_ID +
                " FROM " + SaleOrder.TABLE_NAME + " o " +
                " INNER JOIN " + Customer.TABLE_NAME + " c on c." + Customer.COL_BACKEND_ID + " = o."
                + SaleOrder.COL_CUSTOMER_BACKEND_ID +
                " LEFT OUTER JOIN " + BaseInfo.TABLE_NAME + " i on i."
                + BaseInfo.COL_BACKEND_ID + " = o." + SaleOrder.COL_PAYMENT_TYPE_BACKEND_ID
                + " AND i." + BaseInfo.COL_TYPE + " = " + BaseInfoTypes.PAYMENT_TYPE.getId()
                + " WHERE 1 = 1 ";

        if (Empty.isNotEmpty(saleOrderSO) && Empty.isNotEmpty(saleOrderSO.getStatusId()))
        {
            sql = sql.concat(" ").concat(" AND o." + SaleOrder.COL_STATUS + " = ?");
            argsList.add(String.valueOf(saleOrderSO.getStatusId()));
        }

        if (Empty.isNotEmpty(saleOrderSO) && Empty.isNotEmpty(saleOrderSO.getCustomerBackendId()))
        {
            sql = sql.concat(" ").concat(" AND o." + SaleOrder.COL_CUSTOMER_BACKEND_ID + " = ?");
            argsList.add(String.valueOf(saleOrderSO.getCustomerBackendId()));
        }

        String[] args = {};
        Cursor cursor = db.rawQuery(sql, argsList.toArray(args));

        while (cursor.moveToNext())
        {
            SaleOrderListModel order = new SaleOrderListModel();
            order.setId(cursor.getLong(0));
            order.setBackendId(cursor.getLong(1));
            order.setSaleOrderNumber(cursor.getString(2));
            order.setDate(cursor.getString(3));
            order.setAmount(cursor.getLong(4));
            order.setStatus(cursor.getLong(5));
            order.setCustomerName(cursor.getString(6));
            order.setPaymentTypeTitle(cursor.getString(7));
            order.setCustomerBackendId(cursor.getLong(8));
            orders.add(order);
        }

        cursor.close();
        returnOrders.addAll(orders);
        return returnOrders;
    }

    @Override
    public SaleOrderDto getOrderDtoById(Long orderId)
    {
        CommerDatabaseHelper databaseHelper = CommerDatabaseHelper.getInstance(getContext());
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        String selection = getPrimaryKeyColumnName() + " = ?";
        String[] args = {String.valueOf(orderId)};
        Cursor cursor = db.query(getTableName(), getProjection(), selection, args, null, null, null);
        SaleOrderDto orderDto = null;
        if (cursor.moveToFirst())
        {
            orderDto = createOrderDtoFromCursor(cursor);
        }
        cursor.close();
        return orderDto;
    }

    @Override
    public SaleOrderDto getOrderDtoByCustomerBackendIdAndStatus(Long customerBackendId,
                                                                Long statusId)
    {
        CommerDatabaseHelper databaseHelper = CommerDatabaseHelper.getInstance(getContext());
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        String selection =
                SaleOrder.COL_CUSTOMER_BACKEND_ID + " = ? AND " + SaleOrder.COL_STATUS + " = ?";
        String[] args = {String.valueOf(customerBackendId), String.valueOf(statusId)};
        Cursor cursor = db.query(getTableName(), getProjection(), selection, args, null, null, null);
        SaleOrderDto orderDto = null;
        if (cursor.moveToFirst())
        {
            orderDto = createOrderDtoFromCursor(cursor);
        }
        cursor.close();
        return orderDto;
    }

    @Override
    public List<SaleOrderDto> findOrderDtoByStatusId(Long statusId)
    {
        CommerDatabaseHelper databaseHelper = CommerDatabaseHelper.getInstance(getContext());
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        String selection = SaleOrder.COL_STATUS + " = ?";
        String[] args = {String.valueOf(statusId)};
        Cursor cursor = db.query(getTableName(), getProjection(), selection, args, null, null, null);

        List<SaleOrderDto> saleOrderDtoList = new ArrayList<>();

        while (cursor.moveToNext())
        {
            saleOrderDtoList.add(createOrderDtoFromCursor(cursor));
        }

        cursor.close();
        return saleOrderDtoList;
    }

    @Override
    public void deleteByCustomerBackendIdAndStatus(Long customerBackendId, Long statusId)
    {
        CommerDatabaseHelper databaseHelper = CommerDatabaseHelper.getInstance(getContext());
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        String sql =
                " DELETE FROM " + SaleOrder.TABLE_NAME + " WHERE " + SaleOrder.COL_CUSTOMER_BACKEND_ID
                        + " = ? AND " +
                        " " + SaleOrder.COL_STATUS + " = ?";
        String[] args = {String.valueOf(customerBackendId), String.valueOf(statusId)};
        db.rawQuery(sql, args);
    }
}
