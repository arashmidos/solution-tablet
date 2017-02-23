package com.parsroyal.solutiontablet.biz.impl;

import android.content.Context;

import com.parsroyal.solutiontablet.biz.KeyValueBiz;
import com.parsroyal.solutiontablet.data.dao.KeyValueDao;
import com.parsroyal.solutiontablet.data.dao.impl.KeyValueDaoImpl;
import com.parsroyal.solutiontablet.data.entity.KeyValue;
import com.parsroyal.solutiontablet.util.Empty;

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
