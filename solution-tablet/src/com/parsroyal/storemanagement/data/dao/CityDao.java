package com.parsroyal.storemanagement.data.dao;

import com.parsroyal.storemanagement.data.entity.City;
import com.parsroyal.storemanagement.data.model.LabelValue;
import java.util.List;

/**
 * Created by Mahyar on 6/19/2015.
 */
public interface CityDao extends BaseDao<City, Long> {

  List<LabelValue> getAllCitiesLabelValuesForProvinceId(Long provinceId);
  List<LabelValue> searchCitiesLabelValuesForProvinceId(Long provinceId,String constraint);
}
