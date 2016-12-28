package com.conta.comer.biz.impl;

import android.content.Context;

import com.conta.comer.biz.KeyValueBiz;
import com.conta.comer.data.dao.KeyValueDao;
import com.conta.comer.data.dao.impl.KeyValueDaoImpl;
import com.conta.comer.data.entity.KeyValue;
import com.conta.comer.util.Empty;

/**
 * Created by Mahyar on 6/14/2015.
 */
public class KeyValueBizImpl implements KeyValueBiz
{

    private Context context;
    private KeyValueDao keyValueDao;

    public KeyValueBizImpl(Context context)
    {
        this.context = context;
        this.keyValueDao = new KeyValueDaoImpl(context);
    }

    @Override
    public void save(KeyValue keyValue)
    {
        KeyValue loadedKeyValue = keyValueDao.retrieveByKey(keyValue.getKey());
        if (Empty.isNotEmpty(loadedKeyValue))
        {
            loadedKeyValue.setKey(keyValue.getKey());
            loadedKeyValue.setValue(keyValue.getValue());
            keyValueDao.update(loadedKeyValue);
        } else
        {
            keyValueDao.create(keyValue);
        }
    }

    @Override
    public KeyValue findByKey(String key)
    {
        KeyValue keyValue = keyValueDao.retrieveByKey(key);
        if (Empty.isNotEmpty(keyValue))
            return keyValue;
        return null;
    }
}
