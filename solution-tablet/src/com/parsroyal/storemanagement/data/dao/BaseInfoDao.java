package com.parsroyal.storemanagement.data.dao;

import com.parsroyal.storemanagement.data.entity.BaseInfo;
import com.parsroyal.storemanagement.data.model.LabelValue;
import java.util.List;

/**
 * Created by Mahyar on 6/20/2015.
 */
public interface BaseInfoDao extends BaseDao<BaseInfo, Long> {

  List<LabelValue> getAllBaseInfosLabelValuesByTypeId(Long typeId, Long backendId);

  List<LabelValue> search(Long type, String constraint);
}
