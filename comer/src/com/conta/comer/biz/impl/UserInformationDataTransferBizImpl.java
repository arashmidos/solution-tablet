package com.conta.comer.biz.impl;

import android.content.Context;

import com.conta.comer.biz.AbstractDataTransferBizImpl;
import com.conta.comer.biz.KeyValueBiz;
import com.conta.comer.data.entity.KeyValue;
import com.conta.comer.data.model.User;
import com.conta.comer.exception.GotNoResponseFromBackendException;
import com.conta.comer.exception.InvalidUserCodeException;
import com.conta.comer.ui.observer.ResultObserver;
import com.conta.comer.util.Empty;
import com.conta.comer.util.constants.ApplicationKeys;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;

import java.nio.charset.Charset;

/**
 * Created by Mahyar on 6/18/2015.
 */
public class UserInformationDataTransferBizImpl extends AbstractDataTransferBizImpl<User>
{

    private Context context;
    private ResultObserver observer;
    private KeyValueBiz keyValueBiz;
    private KeyValue userCode;

    public UserInformationDataTransferBizImpl(Context context, ResultObserver observer)
    {
        super(context);
        this.observer = observer;
        this.keyValueBiz = new KeyValueBizImpl(context);
    }

    @Override
    public void receiveData(User data)
    {
        if (Empty.isNotEmpty(data))
        {
            keyValueBiz.save(new KeyValue(ApplicationKeys.SALESMAN_ID, data.getSalesmanId()));
            keyValueBiz.save(new KeyValue(ApplicationKeys.USER_FULL_NAME, data.getFullName()));
            keyValueBiz.save(new KeyValue(ApplicationKeys.USER_COMPANY_NAME, data.getCompanyName()));
        } else
        {
            getObserver().publishResult(new GotNoResponseFromBackendException());
        }
    }

    @Override
    public void beforeTransfer()
    {
        userCode = keyValueBiz.findByKey(ApplicationKeys.SETTING_USER_CODE);
        if (Empty.isEmpty(userCode))
        {
            getObserver().publishResult(new InvalidUserCodeException());
        }
    }

    @Override
    public ResultObserver getObserver()
    {
        return observer;
    }

    @Override
    public String getMethod()
    {
        return "user/detail";
    }

    @Override
    public Class getType()
    {
        return User.class;
    }

    @Override
    public HttpMethod getHttpMethod()
    {
        return HttpMethod.POST;
    }

    @Override
    protected MediaType getContentType()
    {
        return new MediaType("TEXT", "PLAIN", Charset.forName("UTF-8"));
    }

    @Override
    protected HttpEntity getHttpEntity(HttpHeaders headers)
    {
        headers.add("branchCode", keyValueBiz.findByKey(ApplicationKeys.SETTING_BRANCH_CODE).getValue());
        headers.add("stockCode", keyValueBiz.findByKey(ApplicationKeys.SETTING_STOCK_CODE).getValue());

        HttpEntity<String> entity = new HttpEntity<String>(userCode.getValue(), headers);
        return entity;
    }
}
