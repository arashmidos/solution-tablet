package com.parsroyal.solutiontablet.data.dao;

import com.parsroyal.solutiontablet.data.entity.BaseInfo;
import com.parsroyal.solutiontablet.data.model.LabelValue;
import java.util.List;

/**
 * Created by Mahyar on 6/20/2015.
 */
public interface BaseInfoDao extends BaseDao<BaseInfo, Long> {

  List<LabelValue> getAllBaseInfosLabelValuesByTypeId(Long typeId);
}
