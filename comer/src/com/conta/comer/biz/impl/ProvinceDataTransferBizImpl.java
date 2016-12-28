package com.conta.comer.biz.impl;

import android.content.Context;

import com.conta.comer.R;
import com.conta.comer.biz.AbstractDataTransferBizImpl;
import com.conta.comer.data.dao.ProvinceDao;
import com.conta.comer.data.dao.impl.ProvinceDaoImpl;
import com.conta.comer.data.entity.Province;
import com.conta.comer.data.model.ProvinceList;
import com.conta.comer.ui.observer.ResultObserver;
import com.conta.comer.util.Empty;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;

/**
 * Created by Mahyar on 6/18/2015.
 */
public class ProvinceDataTransferBizImpl extends AbstractDataTransferBizImpl<ProvinceList>
{

    private Context context;
    private ProvinceDao provinceDao;
    private ResultObserver observer;

    public ProvinceDataTransferBizImpl(Context context, ResultObserver observer)
    {
        super(context);
        this.context = context;
        this.provinceDao = new ProvinceDaoImpl(context);
        this.observer = observer;
    }

    @Override
    public void receiveData(ProvinceList data)
    {

        if (Empty.isNotEmpty(data))
        {
            provinceDao.deleteAll();
            for (Province province : data.getProvinceList())
            {
                provinceDao.create(province);
            }
        }
        getObserver().publishResult(context.getString(R.string.provinces_data_transferred_successfully));
    }

    @Override
    public void beforeTransfer()
    {
        getObserver().publishResult(context.getString(R.string.message_transferring_provinces_data));
    }

    @Override
    public ResultObserver getObserver()
    {
        return observer;
    }

    @Override
    public String getMethod()
    {
        return "baseinfo/provinces";
    }

    @Override
    public Class getType()
    {
        return ProvinceList.class;
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
