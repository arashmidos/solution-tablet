package com.parsroyal.solutiontablet.service.impl;

import android.content.Context;

import com.parsroyal.solutiontablet.R;
import com.parsroyal.solutiontablet.biz.AbstractDataTransferBizImpl;
import com.parsroyal.solutiontablet.data.dao.BaseInfoDao;
import com.parsroyal.solutiontablet.data.dao.impl.BaseInfoDaoImpl;
import com.parsroyal.solutiontablet.data.entity.BaseInfo;
import com.parsroyal.solutiontablet.data.model.BaseInfoList;
import com.parsroyal.solutiontablet.ui.observer.ResultObserver;
import com.parsroyal.solutiontablet.util.CharacterFixUtil;
import com.parsroyal.solutiontablet.util.Empty;

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
                baseInfo.setTitle(CharacterFixUtil.fixString(baseInfo.getTitle()));
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
