package com.parsroyal.solutiontablet.data.dao;

import com.parsroyal.solutiontablet.data.entity.City;
import com.parsroyal.solutiontablet.data.model.LabelValue;
import java.util.List;

/**
 * Created by Mahyar on 6/19/2015.
 */
public interface CityDao extends BaseDao<City, Long> {

  List<LabelValue> getAllCitiesLabelValuesForProvinceId(Long provinceId);
}
