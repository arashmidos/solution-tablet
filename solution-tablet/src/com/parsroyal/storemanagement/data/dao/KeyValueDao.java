package com.parsroyal.storemanagement.data.dao;

import com.parsroyal.storemanagement.data.entity.KeyValue;

/**
 * Created by Mahyar on 6/4/2015.
 */
public interface KeyValueDao extends BaseDao<KeyValue, Long> {

  KeyValue retrieveByKey(String settingKey);

  void clearAllKeys();
}
