package com.parsroyal.solutiontablet.biz.impl;

import android.content.Context;

import com.parsroyal.solutiontablet.R;
import com.parsroyal.solutiontablet.biz.AbstractDataTransferBizImpl;
import com.parsroyal.solutiontablet.data.dao.CityDao;
import com.parsroyal.solutiontablet.data.dao.impl.CityDaoImpl;
import com.parsroyal.solutiontablet.data.entity.City;
import com.parsroyal.solutiontablet.data.model.CityList;
import com.parsroyal.solutiontablet.ui.observer.ResultObserver;
import com.parsroyal.solutiontablet.util.CharacterFixUtil;
import com.parsroyal.solutiontablet.util.Empty;

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
