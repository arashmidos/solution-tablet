package com.conta.comer.service;

import com.conta.comer.data.entity.City;
import com.conta.comer.data.entity.KeyValue;
import com.conta.comer.data.entity.Province;
import com.conta.comer.data.model.LabelValue;

import java.util.List;

/**
 * Created by Mahyar on 6/14/2015.
 */
public interface BaseInfoService
{
    KeyValue getKeyValue(String key);

    List<Province> getAllProvinces();

    List<City> getAllCities();

    List<LabelValue> getAllBaseInfosLabelValuesByTypeId(Long id);

    List<LabelValue> getAllProvincesLabelValues();

    List<LabelValue> getAllCitiesLabelsValues(Long provinceId);

    List<LabelValue> getAllPaymentType();

}
