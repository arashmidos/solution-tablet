package com.conta.comer.data.dao;

import com.conta.comer.data.entity.City;
import com.conta.comer.data.model.LabelValue;

import java.util.List;

/**
 * Created by Mahyar on 6/19/2015.
 */
public interface CityDao extends BaseDao<City, Long>
{

    List<LabelValue> getAllCitiesLabelValuesForProvinceId(Long provinceId);
}
