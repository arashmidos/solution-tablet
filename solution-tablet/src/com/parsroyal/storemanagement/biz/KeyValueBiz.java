package com.parsroyal.storemanagement.biz;

import com.parsroyal.storemanagement.data.entity.KeyValue;

/**
 * Created by Mahyar on 6/14/2015.
 */
public interface KeyValueBiz {

  void save(KeyValue keyValue);

  KeyValue findByKey(String key);
}
