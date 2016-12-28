package com.conta.comer.data.dao.impl;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.conta.comer.data.dao.KeyValueDao;
import com.conta.comer.data.entity.KeyValue;
import com.conta.comer.data.helper.CommerDatabaseHelper;

/**
 * Created by Mahyar on 6/4/2015.
 */
public class KeyValueDaoImpl extends AbstractDao<KeyValue, Long> implements KeyValueDao
{

    private Context context;

    public KeyValueDaoImpl(Context context)
    {
        this.context = context;
    }

    @Override
    protected Context getContext()
    {
        return context;
    }

    @Override
    public KeyValue retrieveByKey(String settingKey)
    {
        CommerDatabaseHelper databaseHelper = CommerDatabaseHelper.getInstance(getContext());
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        String selection = KeyValue.COL_KEY + " = ?";
        String[] args = {String.valueOf(settingKey)};
        Cursor cursor = db.query(getTableName(), getProjection(), selection, args, null, null, null);
        if (cursor.moveToFirst())
        {
            KeyValue keyValue = createEntityFromCursor(cursor);
            cursor.close();
            db.close();
            return keyValue;
        }
        cursor.close();
        db.close();
        return null;
    }

    @Override
    protected ContentValues getContentValues(KeyValue entity)
    {
        ContentValues contentValues = new ContentValues();
        contentValues.put(KeyValue.COL_KEY, entity.getKey());
        contentValues.put(KeyValue.COL_VALUE, entity.getValue());
        return contentValues;
    }

    @Override
    protected String getTableName()
    {
        return KeyValue.TABLE_NAME;
    }

    @Override
    protected String getPrimaryKeyColumnName()
    {
        return KeyValue.COL_ID;
    }

    @Override
    protected String[] getProjection()
    {
        String[] projection = {KeyValue.COL_ID, KeyValue.COL_KEY, KeyValue.COL_VALUE};
        return projection;
    }

    @Override
    protected KeyValue createEntityFromCursor(Cursor cursor)
    {
        return new KeyValue(cursor.getLong(0), cursor.getString(1), cursor.getString(2));
    }

}
