package com.conta.comer.data.dao;

import com.conta.comer.data.entity.KeyValue;

/**
 * Created by Mahyar on 6/4/2015.
 */
public interface KeyValueDao extends BaseDao<KeyValue, Long>
{
    KeyValue retrieveByKey(String settingKey);
}
