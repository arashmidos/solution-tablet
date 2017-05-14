package com.parsroyal.solutiontablet.data.dao.impl;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.parsroyal.solutiontablet.data.dao.GoodsDao;
import com.parsroyal.solutiontablet.data.entity.Goods;
import com.parsroyal.solutiontablet.data.helper.CommerDatabaseHelper;
import com.parsroyal.solutiontablet.data.listmodel.GoodsListModel;
import com.parsroyal.solutiontablet.data.searchobject.GoodsSo;
import com.parsroyal.solutiontablet.util.Empty;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mahyar on 7/24/2015.
 */
public class GoodsDaoImpl extends AbstractDao<Goods, Long> implements GoodsDao
{

    private Context context;

    public GoodsDaoImpl(Context context)
    {
        this.context = context;
    }

    @Override
    protected Context getContext()
    {
        return context;
    }

    @Override
    protected ContentValues getContentValues(Goods entity)
    {
        ContentValues contentValues = new ContentValues();

        contentValues.put(Goods.COL_ID, entity.getId());
        contentValues.put(Goods.COL_BACKEND_ID, entity.getBackendId());
        contentValues.put(Goods.COL_TITLE, entity.getTitle());
        contentValues.put(Goods.COL_CODE, entity.getCode());
        contentValues.put(Goods.COL_PRICE, entity.getPrice());
        contentValues.put(Goods.COL_CUSTOMER_PRICE, entity.getCustomerPrice());
        contentValues.put(Goods.COL_EXISTING, entity.getExisting());
        contentValues.put(Goods.COL_UNIT1_TITLE, entity.getUnit1Title());
        contentValues.put(Goods.COL_UNIT2_TITLE, entity.getUnit2Title());
        contentValues.put(Goods.COL_REFERENCE_CODE, entity.getReferenceCode());
        contentValues.put(Goods.COL_UNIT1_COUNT, entity.getUnit1Count());
        contentValues.put(Goods.COL_GROUP_BACKEND_ID, entity.getGroupBackendId());
        contentValues.put(Goods.COL_BAR_CODE, entity.getBarCode());
        contentValues.put(Goods.COL_DEFAULT_UNIT, entity.getDefaultUnit());
        contentValues.put(Goods.COL_RECOVERY_DATE, entity.getDefaultUnit());
        contentValues.put(Goods.COL_CREATE_DATE_TIME, entity.getCreateDateTime());
        contentValues.put(Goods.COL_UPDATE_DATE_TIME, entity.getUpdateDateTime());

        return contentValues;
    }

    @Override
    protected String getTableName()
    {
        return Goods.TABLE_NAME;
    }

    @Override
    protected String getPrimaryKeyColumnName()
    {
        return Goods.COL_ID;
    }

    @Override
    protected String[] getProjection()
    {
        String[] projection = {
                Goods.COL_ID,
                Goods.COL_BACKEND_ID,
                Goods.COL_TITLE,
                Goods.COL_CODE,
                Goods.COL_PRICE,
                Goods.COL_CUSTOMER_PRICE,
                Goods.COL_EXISTING,
                Goods.COL_UNIT1_TITLE,
                Goods.COL_UNIT2_TITLE,
                Goods.COL_REFERENCE_CODE,
                Goods.COL_UNIT1_COUNT,
                Goods.COL_GROUP_BACKEND_ID,
                Goods.COL_BAR_CODE,
                Goods.COL_DEFAULT_UNIT,
                Goods.COL_RECOVERY_DATE,
                Goods.COL_CREATE_DATE_TIME,
                Goods.COL_UPDATE_DATE_TIME
        };
        return projection;
    }

    @Override
    protected Goods createEntityFromCursor(Cursor cursor)
    {
        Goods goods = new Goods();
        goods.setId(cursor.getLong(0));
        goods.setBackendId(cursor.getLong(1));
        goods.setTitle(cursor.getString(2));
        goods.setCode(cursor.getString(3));
        goods.setPrice(cursor.getLong(4));
        goods.setCustomerPrice(cursor.getLong(5));
        goods.setExisting(cursor.getLong(6));
        goods.setUnit1Title(cursor.getString(7));
        goods.setUnit2Title(cursor.getString(8));
        goods.setReferenceCode(cursor.getString(9));
        goods.setUnit1Count(cursor.getLong(10));
        goods.setGroupBackendId(cursor.getLong(11));
        goods.setBarCode(cursor.getString(12));
        goods.setDefaultUnit(cursor.getInt(13));
        goods.setRecoveryDate(cursor.getString(14));
        goods.setCreateDateTime(cursor.getString(15));
        goods.setUpdateDateTime(cursor.getString(16));
        return goods;
    }

    @Override
    public List<GoodsListModel> findGoods(GoodsSo goodsSo)
    {
        CommerDatabaseHelper commerDatabaseHelper = CommerDatabaseHelper.getInstance(context);
        SQLiteDatabase db = commerDatabaseHelper.getWritableDatabase();
        List<String> argsList = new ArrayList<>();
        String sql =
                "select g._id, g.BACKEND_ID, g.TITLE, g.CODE, g.PRICE, g.EXISTING, g.UNIT1_COUNT, g.UNIT1_TITLE, "
                        +
                        "g.UNIT2_TITLE, g.RECOVERY_DATE, g.CUSTOMER_PRICE from COMMER_GOODS g WHERE 1=1";
        if (Empty.isNotEmpty(goodsSo.getGoodsGroupBackendId()))
        {
            sql = sql.concat(" AND g.GROUP_BACKEND_ID = ? ");
            argsList.add(String.valueOf(goodsSo.getGoodsGroupBackendId()));
        }

        if (Empty.isNotEmpty(goodsSo.getConstraint()))
        {
            sql = sql.concat(" AND g.TITLE like ? ");
            argsList.add(goodsSo.getConstraint());
        }

        String[] args = new String[argsList.size()];
        Cursor cursor = db.rawQuery(sql, argsList.toArray(args));
        List<GoodsListModel> goods = new ArrayList<>();

        while (cursor.moveToNext())
        {
            goods.add(createGoodsListModelFromCursor(cursor));
        }

        cursor.close();

        return goods;
    }

    @Override
    public Goods retrieveByBackendId(Long backendId)
    {
        CommerDatabaseHelper databaseHelper = CommerDatabaseHelper.getInstance(getContext());
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        String selection = Goods.COL_BACKEND_ID + " = ?";
        String[] args = {String.valueOf(backendId)};
        Cursor cursor = db.query(getTableName(), getProjection(), selection, args, null, null, null);
        Goods goods = null;
        if (cursor.moveToFirst())
        {
            goods = createEntityFromCursor(cursor);
        }
        cursor.close();
        return goods;
    }

    private GoodsListModel createGoodsListModelFromCursor(Cursor cursor)
    {
        GoodsListModel goodsListModel = new GoodsListModel();
        goodsListModel.setPrimaryKey(cursor.getLong(0));
        goodsListModel.setGoodsBackendId(cursor.getLong(1));
        goodsListModel.setTitle(cursor.getString(2));
        goodsListModel.setCode(cursor.getString(3));
        goodsListModel.setGoodsAmount(cursor.getLong(4));
        goodsListModel.setExisting(cursor.getLong(5));
        goodsListModel.setUnit1Count(cursor.getLong(6));
        goodsListModel.setUnit1Title(cursor.getString(7));
        goodsListModel.setUnit2Title(cursor.getString(8));
        goodsListModel.setRecoveryDate(cursor.getLong(9));
        goodsListModel.setCustomerPrice(cursor.getLong(10));
        return goodsListModel;
    }
}
