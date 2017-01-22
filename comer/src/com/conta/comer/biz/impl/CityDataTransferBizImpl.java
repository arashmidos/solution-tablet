package com.conta.comer.biz.impl;

import android.content.Context;

import com.conta.comer.R;
import com.conta.comer.biz.AbstractDataTransferBizImpl;
import com.conta.comer.data.dao.CityDao;
import com.conta.comer.data.dao.impl.CityDaoImpl;
import com.conta.comer.data.entity.City;
import com.conta.comer.data.model.CityList;
import com.conta.comer.ui.observer.ResultObserver;
import com.conta.comer.util.CharacterFixUtil;
import com.conta.comer.util.Empty;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;

/**
 * Created by Mahyar on 6/19/2015.
 */
public class CityDataTransferBizImpl extends AbstractDataTransferBizImpl<CityList>
{

    private Context context;
    private CityDao cityDao;
    private ResultObserver observer;

    public CityDataTransferBizImpl(Context context, ResultObserver observer)
    {
        super(context);
        this.context = context;
        this.cityDao = new CityDaoImpl(context);
        this.observer = observer;
    }

    @Override
    public void receiveData(CityList data)
    {
        if (Empty.isNotEmpty(data))
        {
            cityDao.deleteAll();
            for (City city : data.getCityList())
            {
                city.setTitle(CharacterFixUtil.fixString(city.getTitle()));
                cityDao.create(city);
            }
        }
        getObserver().publishResult(context.getString(R.string.cities_data_transferred_successfully));
    }

    @Override
    public void beforeTransfer()
    {
        getObserver().publishResult(context.getString(R.string.message_transferring_cities_data));
    }

    @Override
    public ResultObserver getObserver()
    {
        return observer;
    }

    @Override
    public String getMethod()
    {
        return "baseinfo/cities";
    }

    @Override
    public Class getType()
    {
        return CityList.class;
    }

    @Override
    public HttpMethod getHttpMethod()
    {
        return HttpMethod.GET;
    }

    @Override
    protected MediaType getContentType()
    {
        return MediaType.TEXT_PLAIN;
    }

    @Override
    protected HttpEntity getHttpEntity(HttpHeaders headers)
    {
        HttpEntity requestEntity = new HttpEntity<String>(headers);
        return requestEntity;
    }

}
