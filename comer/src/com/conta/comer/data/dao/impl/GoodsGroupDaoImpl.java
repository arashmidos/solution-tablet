package com.conta.comer.data.dao.impl;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.conta.comer.data.dao.GoodsGroupDao;
import com.conta.comer.data.entity.GoodsGroup;

/**
 * Created by Mahyar on 7/24/2015.
 */
public class GoodsGroupDaoImpl extends AbstractDao<GoodsGroup, Long> implements GoodsGroupDao
{

    private Context context;

    public GoodsGroupDaoImpl(Context context)
    {
        this.context = context;
    }

    @Override
    protected Context getContext()
    {
        return context;
    }

    @Override
    protected ContentValues getContentValues(GoodsGroup entity)
    {
        ContentValues contentValues = new ContentValues();
        contentValues.put(GoodsGroup.COL_ID, entity.getId());
        contentValues.put(GoodsGroup.COL_BACKEND_ID, entity.getBackendId());
        contentValues.put(GoodsGroup.COL_PARENT_BACKEND_ID, entity.getParentBackendId());
        contentValues.put(GoodsGroup.COL_TITLE, entity.getTitle());
        contentValues.put(GoodsGroup.COL_CODE, entity.getCode());
        contentValues.put(GoodsGroup.COL_LEVEL, entity.getLevel());
        contentValues.put(GoodsGroup.COL_CREATE_DATE_TIME, entity.getCreateDateTime());
        contentValues.put(GoodsGroup.COL_UPDATE_DATE_TIME, entity.getUpdateDateTime());
        return contentValues;
    }

    @Override
    protected String getTableName()
    {
        return GoodsGroup.TABLE_NAME;
    }

    @Override
    protected String getPrimaryKeyColumnName()
    {
        return GoodsGroup.COL_ID;
    }

    @Override
    protected String[] getProjection()
    {
        String[] projection = {
                GoodsGroup.COL_ID,
                GoodsGroup.COL_BACKEND_ID,
                GoodsGroup.COL_PARENT_BACKEND_ID,
                GoodsGroup.COL_TITLE,
                GoodsGroup.COL_CODE,
                GoodsGroup.COL_LEVEL,
                GoodsGroup.COL_CREATE_DATE_TIME,
                GoodsGroup.COL_UPDATE_DATE_TIME
        };
        return projection;
    }

    @Override
    protected GoodsGroup createEntityFromCursor(Cursor cursor)
    {
        GoodsGroup group = new GoodsGroup();
        group.setId(cursor.getLong(0));
        group.setBackendId(cursor.getLong(1));
        group.setParentBackendId(cursor.getLong(2));
        group.setTitle(cursor.getString(3));
        group.setCode(cursor.getString(4));
        group.setLevel(cursor.getInt(5));
        group.setCreateDateTime(cursor.getString(6));
        group.setUpdateDateTime(cursor.getString(7));
        return group;
    }
}
