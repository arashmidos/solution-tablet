package com.parsroyal.storemanagement.service.impl;

import android.content.Context;
import com.parsroyal.storemanagement.constants.BaseInfoTypes;
import com.parsroyal.storemanagement.constants.PaymentType;
import com.parsroyal.storemanagement.data.dao.BaseInfoDao;
import com.parsroyal.storemanagement.data.dao.CityDao;
import com.parsroyal.storemanagement.data.dao.KeyValueDao;
import com.parsroyal.storemanagement.data.dao.ProvinceDao;
import com.parsroyal.storemanagement.data.dao.impl.BaseInfoDaoImpl;
import com.parsroyal.storemanagement.data.dao.impl.CityDaoImpl;
import com.parsroyal.storemanagement.data.dao.impl.KeyValueDaoImpl;
import com.parsroyal.storemanagement.data.dao.impl.ProvinceDaoImpl;
import com.parsroyal.storemanagement.data.entity.BaseInfo;
import com.parsroyal.storemanagement.data.entity.City;
import com.parsroyal.storemanagement.data.entity.KeyValue;
import com.parsroyal.storemanagement.data.entity.Province;
import com.parsroyal.storemanagement.data.model.LabelValue;
import com.parsroyal.storemanagement.service.BaseInfoService;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mahyar on 6/14/2015.
 */
public class BaseInfoServiceImpl implements BaseInfoService {

  private Context context;
  private KeyValueDao keyValueDao;
  private ProvinceDao provinceDao;
  private CityDao cityDao;
  private BaseInfoDao baseInfoDao;

  public BaseInfoServiceImpl(Context context) {
    this.context = context;
    this.keyValueDao = new KeyValueDaoImpl();
    this.provinceDao = new ProvinceDaoImpl(context);
    this.cityDao = new CityDaoImpl(context);
    this.baseInfoDao = new BaseInfoDaoImpl(context);
  }

  @Override
  public KeyValue getKeyValue(String key) {
    return keyValueDao.retrieveByKey(key);
  }

  @Override
  public List<Province> getAllProvinces() {
    return provinceDao.retrieveAll();
  }

  @Override
  public List<City> getAllCities() {
    return cityDao.retrieveAll();
  }

  @Override
  public List<LabelValue> getAllBaseInfosLabelValuesByTypeId(Long typeId) {
    if (typeId.equals(BaseInfoTypes.REJECT_TYPE.getId())) {
      return baseInfoDao
          .getAllBaseInfosLabelValuesByTypeId(BaseInfoTypes.DELIVERY_RETURN_TYPE.getId(), null);
    }
    return baseInfoDao.getAllBaseInfosLabelValuesByTypeId(typeId, null);
  }

  @Override
  public List<LabelValue> getAllSupplier() {
    return getAllBaseInfosLabelValuesByTypeId(BaseInfoTypes.SUPPLIER.getId());
  }

  @Override
  public List<LabelValue> getAllAssortment() {
    return getAllBaseInfosLabelValuesByTypeId(BaseInfoTypes.ASSORTMENT.getId());
  }

  @Override
  public LabelValue getBaseInfoByBackendId(Long typeId, Long backendId) {
    List<LabelValue> baseInfoList = baseInfoDao
        .getAllBaseInfosLabelValuesByTypeId(typeId, backendId);
    if (baseInfoList.size() > 0) {
      return baseInfoList.get(0);
    }
    return null;
  }

  @Override
  public List<LabelValue> getAllProvincesLabelValues() {
    return provinceDao.getAllProvincesLabelValues();
  }

  @Override
  public List<LabelValue> getAllCitiesLabelsValues(Long provinceId) {
    return cityDao.getAllCitiesLabelValuesForProvinceId(provinceId);
  }

  @Override
  public List<LabelValue> getAllPaymentType() {
    PaymentType[] payments = PaymentType.values();
    List<LabelValue> entities = new ArrayList<>();
    for (int i = 0; i < 3; i++) {
      entities.add(new LabelValue(payments[i].getId(), payments[i].getTitle()));
    }
    return entities;
  }

  @Override
  public void deleteAllCities() {
    cityDao.deleteAll();
  }

  @Override
  public void deleteAllProvinces() {
    provinceDao.deleteAll();
  }

  @Override
  public List<LabelValue> search(Long type, String constraint) {
    return baseInfoDao.search(type, constraint);
  }

  @Override
  public List<BaseInfo> retrieveByTypeAndCode(Long type, String code) {
    String selection = String.format("%s = ? AND %s = ?", BaseInfo.COL_TYPE, BaseInfo.COL_CODE);
    String[] args = {String.valueOf(type), String.valueOf(code)};
    return baseInfoDao.retrieveAll(selection, args, null, null, null, "1");
  }

  @Override
  public void deleteAll() {
    baseInfoDao.deleteAll();
  }
}
