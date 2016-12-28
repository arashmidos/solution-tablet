package com.conta.comer.service.impl;

import android.content.Context;

import com.conta.comer.constants.BaseInfoTypes;
import com.conta.comer.constants.PaymentType;
import com.conta.comer.data.dao.BaseInfoDao;
import com.conta.comer.data.dao.CityDao;
import com.conta.comer.data.dao.KeyValueDao;
import com.conta.comer.data.dao.ProvinceDao;
import com.conta.comer.data.dao.impl.BaseInfoDaoImpl;
import com.conta.comer.data.dao.impl.CityDaoImpl;
import com.conta.comer.data.dao.impl.KeyValueDaoImpl;
import com.conta.comer.data.dao.impl.ProvinceDaoImpl;
import com.conta.comer.data.entity.City;
import com.conta.comer.data.entity.KeyValue;
import com.conta.comer.data.entity.Province;
import com.conta.comer.data.model.LabelValue;
import com.conta.comer.service.BaseInfoService;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mahyar on 6/14/2015.
 */
public class BaseInfoServiceImpl implements BaseInfoService
{

    private Context context;
    private KeyValueDao keyValueDao;
    private ProvinceDao provinceDao;
    private CityDao cityDao;
    private BaseInfoDao baseInfoDao;

    public BaseInfoServiceImpl(Context context)
    {
        this.context = context;
        this.keyValueDao = new KeyValueDaoImpl(context);
        this.provinceDao = new ProvinceDaoImpl(context);
        this.cityDao = new CityDaoImpl(context);
        this.baseInfoDao = new BaseInfoDaoImpl(context);
    }

    @Override
    public KeyValue getKeyValue(String key)
    {
        return keyValueDao.retrieveByKey(key);
    }

    @Override
    public List<Province> getAllProvinces()
    {
        return provinceDao.retrieveAll();
    }

    @Override
    public List<City> getAllCities()
    {
        return cityDao.retrieveAll();
    }

    @Override
    public List<LabelValue> getAllBaseInfosLabelValuesByTypeId(Long typeId)
    {
        //TODO: Static data
        if (typeId.equals(BaseInfoTypes.REJECT_TYPE.getId()))
        {
            String[] reasons = {"فاسد شده", "شکسته", "ناقص"};
            List<LabelValue> entities = new ArrayList<LabelValue>();
            for (int i = 0; i < 3; i++)
            {

                entities.add(new LabelValue((long) i,reasons[i]));
            }
            return entities;
        }
        return baseInfoDao.getAllBaseInfosLabelValuesByTypeId(typeId);
    }

    @Override
    public List<LabelValue> getAllProvincesLabelValues()
    {
        return provinceDao.getAllProvincesLabelValues();
    }

    @Override
    public List<LabelValue> getAllCitiesLabelsValues(Long provinceId)
    {
        return cityDao.getAllCitiesLabelValuesForProvinceId(provinceId);
    }

    @Override
    public List<LabelValue> getAllPaymentType()
    {
        PaymentType[] payments = PaymentType.values();
        List<LabelValue> entities = new ArrayList<LabelValue>();
        for (int i = 0; i < 3; i++)
        {
            entities.add(new LabelValue(payments[i].getId(),payments[i].getTitle()));
        }
        return entities;
    }

}
