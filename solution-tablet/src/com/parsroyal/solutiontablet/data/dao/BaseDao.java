package com.parsroyal.solutiontablet.data.dao;

import com.parsroyal.solutiontablet.data.entity.BaseEntity;
import java.io.Serializable;
import java.util.List;

/**
 * Created by Mahyar on 6/4/2015.
 */
public interface BaseDao<ENTITY extends BaseEntity, PK extends Serializable> {

  PK create(ENTITY entity);

  void update(ENTITY entity);

  void delete(PK pk);

  ENTITY retrieve(PK pk);

  List<ENTITY> retrieveAll();

  void deleteAll();

  void deleteAll(String column, String condition);

}
