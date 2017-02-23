package com.parsroyal.solutiontablet.biz;

import com.parsroyal.solutiontablet.data.entity.KeyValue;

/**
 * Created by Mahyar on 6/14/2015.
 */
public interface KeyValueBiz
{
    void save(KeyValue keyValue);

    KeyValue findByKey(String key);
}
