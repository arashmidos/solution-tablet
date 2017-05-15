package com.parsroyal.solutiontablet.service.impl;

import android.content.Context;
import com.parsroyal.solutiontablet.biz.impl.KPIDataTransferBizImpl;
import com.parsroyal.solutiontablet.data.dao.KeyValueDao;
import com.parsroyal.solutiontablet.data.dao.impl.KeyValueDaoImpl;
import com.parsroyal.solutiontablet.data.entity.KeyValue;
import com.parsroyal.solutiontablet.data.model.KPIDto;
import com.parsroyal.solutiontablet.exception.InvalidServerAddressException;
import com.parsroyal.solutiontablet.exception.PasswordNotProvidedForConnectingToServerException;
import com.parsroyal.solutiontablet.exception.UsernameNotProvidedForConnectingToServerException;
import com.parsroyal.solutiontablet.service.KPIService;
import com.parsroyal.solutiontablet.ui.observer.ResultObserver;
import com.parsroyal.solutiontablet.util.Empty;
import com.parsroyal.solutiontablet.util.constants.ApplicationKeys;

/**
 * Created by Mahyar on 6/4/2015.
 */
public class KPIServiceImpl implements KPIService {

  private Context context;
  private KeyValueDao keyValueDao;

  private KeyValue serverAddress1;
  private KeyValue serverAddress2;
  private KeyValue username;
  private KeyValue password;

  public KPIServiceImpl(Context context) {
    this.context = context;
    this.keyValueDao = new KeyValueDaoImpl(context);
  }

  @Override
  public KPIDto getCustomerKPI(long customerBackendId, ResultObserver observer) {
    serverAddress1 = keyValueDao.retrieveByKey(ApplicationKeys.SETTING_SERVER_ADDRESS_1);
    serverAddress2 = keyValueDao.retrieveByKey(ApplicationKeys.SETTING_SERVER_ADDRESS_2);
    username = keyValueDao.retrieveByKey(ApplicationKeys.SETTING_USERNAME);
    password = keyValueDao.retrieveByKey(ApplicationKeys.SETTING_PASSWORD);
    if (Empty.isEmpty(serverAddress1) || Empty.isEmpty(serverAddress2)) {
      throw new InvalidServerAddressException();
    }

    if (Empty.isEmpty(username)) {
      throw new UsernameNotProvidedForConnectingToServerException();
    }

    if (Empty.isEmpty(password)) {
      throw new PasswordNotProvidedForConnectingToServerException();
    }

    KPIDataTransferBizImpl kpiDataTransferBiz = new KPIDataTransferBizImpl(context, observer);
    return kpiDataTransferBiz.exchangeData(customerBackendId);

  }

  @Override
  public KPIDto getSalesmanKPI(ResultObserver observer) {
    return getCustomerKPI(-1, observer);
  }
}
