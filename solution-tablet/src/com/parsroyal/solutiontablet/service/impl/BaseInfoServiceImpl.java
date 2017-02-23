package com.parsroyal.solutiontablet.service.impl;

import android.content.Context;

import com.parsroyal.solutiontablet.constants.BaseInfoTypes;
import com.parsroyal.solutiontablet.constants.PaymentType;
import com.parsroyal.solutiontablet.data.dao.BaseInfoDao;
import com.parsroyal.solutiontablet.data.dao.CityDao;
import com.parsroyal.solutiontablet.data.dao.KeyValueDao;
import com.parsroyal.solutiontablet.data.dao.ProvinceDao;
import com.parsroyal.solutiontablet.data.dao.impl.BaseInfoDaoImpl;
import com.parsroyal.solutiontablet.data.dao.impl.CityDaoImpl;
import com.parsroyal.solutiontablet.data.dao.impl.KeyValueDaoImpl;
import com.parsroyal.solutiontablet.data.dao.impl.ProvinceDaoImpl;
import com.parsroyal.solutiontablet.data.entity.City;
import com.parsroyal.solutiontablet.data.entity.KeyValue;
import com.parsroyal.solutiontablet.data.entity.Province;
import com.parsroyal.solutiontablet.data.model.LabelValue;
import com.parsroyal.solutiontablet.service.BaseInfoService;

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
