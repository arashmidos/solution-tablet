package com.parsroyal.solutiontablet.biz.impl;

import com.parsroyal.solutiontablet.biz.KeyValueBiz;
import com.parsroyal.solutiontablet.data.dao.KeyValueDao;
import com.parsroyal.solutiontablet.data.dao.impl.KeyValueDaoImpl;
import com.parsroyal.solutiontablet.data.entity.KeyValue;
import com.parsroyal.solutiontablet.util.Empty;

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
