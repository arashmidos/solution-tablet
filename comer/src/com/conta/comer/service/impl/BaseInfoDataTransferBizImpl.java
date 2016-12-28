package com.conta.comer.service.impl;

import android.content.Context;

import com.conta.comer.R;
import com.conta.comer.biz.AbstractDataTransferBizImpl;
import com.conta.comer.data.dao.BaseInfoDao;
import com.conta.comer.data.dao.impl.BaseInfoDaoImpl;
import com.conta.comer.data.entity.BaseInfo;
import com.conta.comer.data.model.BaseInfoList;
import com.conta.comer.ui.observer.ResultObserver;
import com.conta.comer.util.Empty;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;

/**
 * Created by Mahyar on 6/20/2015.
 */
public class BaseInfoDataTransferBizImpl extends AbstractDataTransferBizImpl<BaseInfoList>
{

    private BaseInfoDao baseInfoDao;
    private Context context;
    private ResultObserver observer;

    public BaseInfoDataTransferBizImpl(Context context, ResultObserver resultObserver)
    {
        super(context);
        this.context = context;
        this.observer = resultObserver;
        this.baseInfoDao = new BaseInfoDaoImpl(context);
    }

    @Override
    public void receiveData(BaseInfoList data)
    {
        if (Empty.isNotEmpty(data))
        {
            baseInfoDao.deleteAll();
            for (BaseInfo baseInfo : data.getBaseInfoList())
            {
                baseInfoDao.create(baseInfo);
            }
        }
        getObserver().publishResult(context.getString(R.string.message_base_info_transferred_successfully));
    }

    @Override
    public void beforeTransfer()
    {
        getObserver().publishResult(context.getString(R.string.message_transferring_base_info_data));
    }

    @Override
    public ResultObserver getObserver()
    {
        return observer;
    }

    @Override
    public String getMethod()
    {
        return "baseinfo/all";
    }

    @Override
    public Class getType()
    {
        return BaseInfoList.class;
    }

    @Override
    public HttpMethod getHttpMethod()
    {
        return HttpMethod.GET;
    }

    @Override
    protected MediaType getContentType()
    {
        return MediaType.APPLICATION_JSON;
    }

    @Override
    protected HttpEntity getHttpEntity(HttpHeaders headers)
    {
        HttpEntity requestEntity = new HttpEntity<String>(headers);
        return requestEntity;
    }
}
