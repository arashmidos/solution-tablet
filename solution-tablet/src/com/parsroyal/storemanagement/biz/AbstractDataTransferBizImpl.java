package com.parsroyal.storemanagement.biz;

import android.content.Context;
import android.util.Log;
import com.parsroyal.storemanagement.constants.StatusCodes;
import com.parsroyal.storemanagement.data.dao.KeyValueDao;
import com.parsroyal.storemanagement.data.dao.impl.KeyValueDaoImpl;
import com.parsroyal.storemanagement.data.entity.KeyValue;
import com.parsroyal.storemanagement.data.event.DataTransferErrorEvent;
import com.parsroyal.storemanagement.exception.BackendIsNotReachableException;
import com.parsroyal.storemanagement.exception.BusinessException;
import com.parsroyal.storemanagement.exception.InternalServerError;
import com.parsroyal.storemanagement.exception.TimeOutException;
import com.parsroyal.storemanagement.exception.URLNotFoundException;
import com.parsroyal.storemanagement.exception.UnknownSystemException;
import com.parsroyal.storemanagement.exception.UserNotAuthorizedException;
import com.parsroyal.storemanagement.ui.observer.ResultObserver;
import com.parsroyal.storemanagement.util.Empty;
import com.parsroyal.storemanagement.util.LoggingRequestInterceptor;
import com.parsroyal.storemanagement.util.constants.ApplicationKeys;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import org.greenrobot.eventbus.EventBus;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

/**
 * Created by Arash on 6/18/2015.
 */
public abstract class AbstractDataTransferBizImpl<T extends Serializable> {

  public static final String TAG = "AbstractDatrBizImpl";
  protected Context context;
  protected KeyValue serverAddress1;
  protected KeyValue username;
  protected KeyValue password;
  protected KeyValue salesmanId;
  protected KeyValue saleType;
  protected KeyValueDao keyValueDao;
  protected KeyValue salesmanCode;
  protected KeyValue token;
  private KeyValue goodsRequestId;

  public AbstractDataTransferBizImpl(Context context) {
    this.context = context;
    this.keyValueDao = new KeyValueDaoImpl();
  }

  public void prepare() {
    serverAddress1 = keyValueDao.retrieveByKey(ApplicationKeys.BACKEND_URI);
    username = keyValueDao.retrieveByKey(ApplicationKeys.SETTING_USERNAME);
    password = keyValueDao.retrieveByKey(ApplicationKeys.SETTING_PASSWORD);
    saleType = keyValueDao.retrieveByKey(ApplicationKeys.SETTING_SALE_TYPE);
    salesmanId = keyValueDao.retrieveByKey(ApplicationKeys.SALESMAN_ID);
    salesmanCode = keyValueDao.retrieveByKey(ApplicationKeys.SETTING_USER_CODE);
//    goodsRequestId = keyValueDao.retrieveByKey(ApplicationKeys.GOODS_REQUEST_ID);
    token = keyValueDao.retrieveByKey(ApplicationKeys.TOKEN);
  }

  public boolean exchangeData() {
    boolean result = false;
    try {
      prepare();

      beforeTransfer();

      HttpHeaders httpHeaders = new HttpHeaders();
      httpHeaders.setContentType(getContentType());

      httpHeaders.add("saleType",
          Empty.isEmpty(saleType) ? ApplicationKeys.SALE_COLD : saleType.getValue());

      if (Empty.isNotEmpty(salesmanId)) {
        httpHeaders.add("salesmanId", salesmanId.getValue());
      }
      httpHeaders.add("salesmanCode", salesmanCode.getValue());
     /* if (Empty.isNotEmpty(goodsRequestId)) {
        httpHeaders.add("goodsRequestId", goodsRequestId.getValue());
      }*/
      httpHeaders.add("Authorization", "Bearer " + token.getValue());
      //Make RestTemplate loggable
      System.setProperty("org.apache.commons.logging.Log",
          "org.apache.commons.logging.impl.SimpleLog");
      System.setProperty("org.apache.commons.logging.simplelog.showdatetime", "true");
      System.setProperty("org.apache.commons.logging.simplelog.log.httpclient.wire", "debug");
      System.setProperty("org.apache.commons.logging.simplelog.log.org.apache.commons.httpclient",
          "debug");

      RestTemplate restTemplate = new RestTemplate();
      List<ClientHttpRequestInterceptor> interceptors = new ArrayList<>();
      interceptors.add(new LoggingRequestInterceptor());
      restTemplate.setInterceptors(interceptors);
      restTemplate.setRequestFactory(
          new BufferingClientHttpRequestFactory(new SimpleClientHttpRequestFactory()));
      //
      restTemplate.getMessageConverters().add(new StringHttpMessageConverter());
      restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
      String url = makeUrl(serverAddress1.getValue(), getMethod());

      HttpEntity httpEntity = getHttpEntity(httpHeaders);
      ResponseEntity<T> response = restTemplate
          .exchange(url, getHttpMethod(), httpEntity, getType());

      receiveData(response.getBody());

      result = true;

    } catch (BackendIsNotReachableException ex) {
      if (Empty.isNotEmpty(getObserver())) {
        getObserver().publishResult(ex);
      }
    } catch (ResourceAccessException ex) {
      Log.e(TAG, ex.getMessage(), ex);
      EventBus.getDefault().post(new DataTransferErrorEvent(StatusCodes.NO_NETWORK));
      if (Empty.isNotEmpty(getObserver())) {
        getObserver().publishResult(new TimeOutException());
      }
    } catch (HttpServerErrorException ex) {
      Log.e(TAG, ex.getMessage(), ex);
      if (ex.getStatusCode().equals(HttpStatus.INTERNAL_SERVER_ERROR)) {
        if (Empty.isNotEmpty(getObserver())) {
          getObserver().publishResult(new InternalServerError());
        }
      } else {
        if (ex.getStatusCode().equals(HttpStatus.NOT_FOUND) && Empty.isNotEmpty(getObserver())) {
          getObserver().publishResult(new URLNotFoundException());
        }
      }
    } catch (HttpClientErrorException ex) {
      if (Empty.isNotEmpty(getObserver())) {
        if (ex.getStatusCode() == HttpStatus.UNAUTHORIZED) {
          getObserver().publishResult(new UserNotAuthorizedException());
        } else {
          getObserver().publishResult(new URLNotFoundException());
        }
      }
    } catch (final BusinessException ex) {
      Log.e(TAG, ex.getMessage(), ex);
      if (Empty.isNotEmpty(getObserver())) {
        getObserver().publishResult(ex);
      }
    } catch (Exception e) {
      Log.e(TAG, e.getMessage(), e);
      if (Empty.isNotEmpty(getObserver())) {
        getObserver().publishResult(new UnknownSystemException(e));
      }
    } finally {
      if (Empty.isNotEmpty(getObserver())) {
        getObserver().finished(result);
      }
    }
    return result;
  }

  protected String makeUrl(String serverAddress1, String method) {
    return serverAddress1 + "/" + method;
  }

  public abstract void receiveData(T data);

  public abstract void beforeTransfer();

  public abstract ResultObserver getObserver();

  public abstract String getMethod();

  public abstract Class getType();

  public abstract HttpMethod getHttpMethod();

  protected abstract MediaType getContentType();

  protected abstract HttpEntity getHttpEntity(HttpHeaders headers);
}
