package com.conta.comer.service.impl;

import android.content.Context;

import com.conta.comer.biz.KeyValueBiz;
import com.conta.comer.biz.impl.KPIDataTransferBizImpl;
import com.conta.comer.biz.impl.KeyValueBizImpl;
import com.conta.comer.biz.impl.UserInformationDataTransferBizImpl;
import com.conta.comer.data.dao.KeyValueDao;
import com.conta.comer.data.dao.impl.KeyValueDaoImpl;
import com.conta.comer.data.entity.KeyValue;
import com.conta.comer.data.model.KPIDto;
import com.conta.comer.exception.InvalidServerAddressException;
import com.conta.comer.exception.PasswordNotProvidedForConnectingToServerException;
import com.conta.comer.exception.UsernameNotProvidedForConnectingToServerException;
import com.conta.comer.service.KPIService;
import com.conta.comer.service.SettingService;
import com.conta.comer.ui.observer.ResultObserver;
import com.conta.comer.util.Empty;
import com.conta.comer.util.constants.ApplicationKeys;

/**
 * Created by Mahyar on 6/4/2015.
 */
public class KPIServiceImpl implements KPIService
{
    private Context context;
    private KeyValueBiz keyValueBiz;
    private KeyValueDao keyValueDao;

    private KeyValue serverAddress1;
    private KeyValue serverAddress2;
    private KeyValue username;
    private KeyValue password;

    public KPIServiceImpl(Context context)
    {
        this.context = context;
        this.keyValueBiz = new KeyValueBizImpl(context);
        this.keyValueDao = new KeyValueDaoImpl(context);
    }

    @Override
    public KPIDto getCustomerKPI(long customerBackendId, ResultObserver observer)
    {
        serverAddress1 = keyValueDao.retrieveByKey(ApplicationKeys.SETTING_SERVER_ADDRESS_1);
        serverAddress2 = keyValueDao.retrieveByKey(ApplicationKeys.SETTING_SERVER_ADDRESS_2);
        username = keyValueDao.retrieveByKey(ApplicationKeys.SETTING_USERNAME);
        password = keyValueDao.retrieveByKey(ApplicationKeys.SETTING_PASSWORD);
        if (Empty.isEmpty(serverAddress1) || Empty.isEmpty(serverAddress2))
        {
            throw new InvalidServerAddressException();
        }

        if (Empty.isEmpty(username))
        {
            throw new UsernameNotProvidedForConnectingToServerException();
        }

        if (Empty.isEmpty(password))
        {
            throw new PasswordNotProvidedForConnectingToServerException();
        }

        KPIDataTransferBizImpl kpiDataTransferBiz = new KPIDataTransferBizImpl(context, observer);
        return kpiDataTransferBiz.exchangeData(customerBackendId);

    }

    @Override
    public KPIDto getSalesmanKPI(ResultObserver observer)
    {
        return getCustomerKPI(-1, observer);
    }
}
