package com.parsroyal.storemanagement.biz.impl;

import com.parsroyal.storemanagement.biz.KeyValueBiz;
import com.parsroyal.storemanagement.data.dao.KeyValueDao;
import com.parsroyal.storemanagement.data.dao.impl.KeyValueDaoImpl;
import com.parsroyal.storemanagement.data.entity.KeyValue;
import com.parsroyal.storemanagement.util.Empty;

/**
 * Created by Mahyar on 6/14/2015.
 */
public class KeyValueBizImpl implements KeyValueBiz {

  private KeyValueDao keyValueDao;

  public KeyValueBizImpl() {
    this.keyValueDao = new KeyValueDaoImpl();
  }

  @Override
  public void save(KeyValue keyValue) {
    keyValueDao.create(keyValue);
  }

  @Override
  public KeyValue findByKey(String key) {
    KeyValue keyValue = keyValueDao.retrieveByKey(key);
    if (Empty.isNotEmpty(keyValue)) {
      return keyValue;
    }
    return null;
  }
}
