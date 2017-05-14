package com.parsroyal.solutiontablet.biz.impl;

import android.content.Context;

import com.parsroyal.solutiontablet.R;
import com.parsroyal.solutiontablet.biz.AbstractDataTransferBizImpl;
import com.parsroyal.solutiontablet.data.dao.ProvinceDao;
import com.parsroyal.solutiontablet.data.dao.impl.ProvinceDaoImpl;
import com.parsroyal.solutiontablet.data.entity.Province;
import com.parsroyal.solutiontablet.data.model.ProvinceList;
import com.parsroyal.solutiontablet.ui.observer.ResultObserver;
import com.parsroyal.solutiontablet.util.CharacterFixUtil;
import com.parsroyal.solutiontablet.util.Empty;

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
                province.setTitle(CharacterFixUtil.fixString(province.getTitle()));
                provinceDao.create(province);
            }
        }
        getObserver()
                .publishResult(context.getString(R.string.provinces_data_transferred_successfully));
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
