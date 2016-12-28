package com.conta.comer.data.dao;

import com.conta.comer.data.entity.BaseInfo;
import com.conta.comer.data.model.LabelValue;

import java.util.List;

/**
 * Created by Mahyar on 6/20/2015.
 */
public interface BaseInfoDao extends BaseDao<BaseInfo, Long>
{
    List<LabelValue> getAllBaseInfosLabelValuesByTypeId(Long typeId);
}
