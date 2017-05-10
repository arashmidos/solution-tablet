package com.parsroyal.solutiontablet.data.dao.impl;

import android.content.Context;

import com.parsroyal.solutiontablet.data.dao.KeyValueDao;
import com.parsroyal.solutiontablet.data.entity.KeyValue;
import com.parsroyal.solutiontablet.util.PreferenceHelper;

import java.util.List;

/**
 * Created by Arash
 */
public class KeyValueDaoImpl implements KeyValueDao
{
    private final Context context;

    public KeyValueDaoImpl(Context context)
    {
        this.context = context;
    }

    @Override
    public KeyValue retrieveByKey(String settingKey)
    {
        return PreferenceHelper.retrieveByKey(settingKey);
    }

    @Override
    public Long create(KeyValue entity)
    {
        PreferenceHelper.saveKey(entity);
        return 0L;
    }

    @Override
    public void update(KeyValue entity)
    {
        create(entity);
    }

    @Override
    public void delete(Long aLong)
    {
        throw new RuntimeException("Not implemented yet");
    }

    @Override
    public KeyValue retrieve(Long aLong)
    {
        throw new RuntimeException("Not implemented yet");
    }

    @Override
    public List<KeyValue> retrieveAll()
    {
        throw new RuntimeException("Not implemented yet");
    }

    @Override
    public void deleteAll()
    {
        throw new RuntimeException("Not implemented yet");
    }

    @Override
    public void deleteAll(String column, String condition)
    {
        throw new RuntimeException("Not implemented yet");
    }
}