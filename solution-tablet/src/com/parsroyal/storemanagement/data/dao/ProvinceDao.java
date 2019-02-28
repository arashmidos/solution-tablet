package com.parsroyal.storemanagement.data.dao;

import com.parsroyal.storemanagement.data.entity.Province;
import com.parsroyal.storemanagement.data.model.LabelValue;
import java.util.List;

/**
 * Created by Arash on 6/18/2015.
 */
public interface ProvinceDao extends BaseDao<Province, Long> {

  List<LabelValue> getAllProvincesLabelValues();
  List<LabelValue> searchProvincesLabelValues(String constraint);
}
