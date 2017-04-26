package com.parsroyal.solutiontablet.biz.impl;

import android.content.Context;
import android.util.Log;
import android.widget.WrapperListAdapter;

import com.parsroyal.solutiontablet.biz.AbstractDataTransferBizImpl;
import com.parsroyal.solutiontablet.biz.KeyValueBiz;
import com.parsroyal.solutiontablet.data.entity.KeyValue;
import com.parsroyal.solutiontablet.exception.DataNotAvailableException;
import com.parsroyal.solutiontablet.ui.observer.ResultObserver;
import com.parsroyal.solutiontablet.util.Empty;
import com.parsroyal.solutiontablet.util.constants.ApplicationKeys;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;

import java.nio.charset.Charset;
import java.util.Arrays;

/**
 * Created by Arash on 2017-04-24
 */
public class GoodsRequestDataTransferBizImpl extends AbstractDataTransferBizImpl<String>
{

    public static final String TAG = GoodsRequestDataTransferBizImpl.class.getSimpleName();
    private final String userCode;

    private Context context;
    private ResultObserver observer;
    private KeyValueBiz keyValueBiz;

    public GoodsRequestDataTransferBizImpl(Context context, ResultObserver resultObserver)
    {
        super(context);
        this.context = context;
        this.observer = resultObserver;
        this.keyValueBiz = new KeyValueBizImpl(context);
        userCode = keyValueBiz.findByKey(ApplicationKeys.SETTING_USER_CODE).getValue();
    }

    @Override
    public void receiveData(String response)
    {
        if (Empty.isNotEmpty(response))
        {
            try
            {
                keyValueBiz.save(new KeyValue(ApplicationKeys.GOODS_REQUEST_ID,response));
            } catch (Exception ex)
            {
                Log.e(TAG, ex.getMessage(), ex);
            }
        } else
        {
            throw new DataNotAvailableException();
        }
    }

    @Override
    public void beforeTransfer()
    {
    }

    @Override
    public ResultObserver getObserver()
    {
        return observer;
    }

    @Override
    public String getMethod()
    {
        return "user/goodsrequest/" + userCode;
    }

    @Override
    public Class getType()
    {
        return String.class;
    }

    @Override
    public HttpMethod getHttpMethod()
    {
        return HttpMethod.GET;
    }

    @Override
    protected MediaType getContentType()
    {
        MediaType contentType = new MediaType("TEXT", "PLAIN", Charset.forName("UTF-8"));
        return contentType;
    }

    @Override
    protected HttpEntity getHttpEntity(HttpHeaders headers)
    {
        HttpEntity requestEntity = new HttpEntity<String>(headers);

        return requestEntity;
    }
}
