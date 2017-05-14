package com.parsroyal.solutiontablet.data.dao;

import com.parsroyal.solutiontablet.data.entity.Province;
import com.parsroyal.solutiontablet.data.model.LabelValue;

import java.util.List;

/**
 * Created by Mahyar on 6/18/2015.
 */
public interface ProvinceDao extends BaseDao<Province, Long>
{

    List<LabelValue> getAllProvincesLabelValues();
}
