package com.conta.comer.data.dao;

import com.conta.comer.data.entity.Province;
import com.conta.comer.data.model.LabelValue;

import java.util.List;

/**
 * Created by Mahyar on 6/18/2015.
 */
public interface ProvinceDao extends BaseDao<Province, Long>
{

    List<LabelValue> getAllProvincesLabelValues();
}
