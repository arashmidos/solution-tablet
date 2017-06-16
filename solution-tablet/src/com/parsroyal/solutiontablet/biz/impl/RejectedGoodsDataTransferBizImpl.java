package com.parsroyal.solutiontablet.biz.impl;

import android.content.Context;
import android.util.Log;
import com.crashlytics.android.Crashlytics;
import com.parsroyal.solutiontablet.R;
import com.parsroyal.solutiontablet.biz.AbstractDataTransferBizImpl;
import com.parsroyal.solutiontablet.data.dao.GoodsDao;
import com.parsroyal.solutiontablet.data.dao.impl.GoodsDaoImpl;
import com.parsroyal.solutiontablet.data.entity.KeyValue;
import com.parsroyal.solutiontablet.data.model.GoodsDtoList;
import com.parsroyal.solutiontablet.exception.BusinessException;
import com.parsroyal.solutiontablet.exception.InternalServerError;
import com.parsroyal.solutiontablet.exception.TimeOutException;
import com.parsroyal.solutiontablet.exception.URLNotFoundException;
import com.parsroyal.solutiontablet.exception.UnknownSystemException;
import com.parsroyal.solutiontablet.ui.observer.ResultObserver;
import com.parsroyal.solutiontablet.util.Empty;
import com.parsroyal.solutiontablet.util.constants.ApplicationKeys;
import org.springframework.http.HttpBasicAuthentication;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

/**
 * Created by Mahyar on 7/24/2015.
 */
public class RejectedGoodsDataTransferBizImpl extends AbstractDataTransferBizImpl<GoodsDtoList> {

  public static final String TAG = RejectedGoodsDataTransferBizImpl.class.getSimpleName();

  private Context context;
  private ResultObserver resultObserver;
  private GoodsDao goodsDao;
  private Long customerId;

  public RejectedGoodsDataTransferBizImpl(Context context, ResultObserver resultObserver) {
    super(context);
    this.context = context;
    this.resultObserver = resultObserver;
    this.goodsDao = new GoodsDaoImpl(context);
  }

  public GoodsDtoList getAllRejectedData(KeyValue serverAddress1,
      KeyValue username, KeyValue password,
      KeyValue salesmanId, Long customerId) {
    boolean result = false;
    GoodsDtoList goodsDtoList = null;
    try {
      this.serverAddress1 = serverAddress1;
      this.username = username;
      this.password = password;
      this.salesmanId = salesmanId;
      this.customerId = customerId;
      saleType = keyValueDao.retrieveByKey(ApplicationKeys.SETTING_SALE_TYPE);
      salesmanCode = keyValueDao.retrieveByKey(ApplicationKeys.SETTING_USER_CODE);
      beforeTransfer();

      HttpHeaders httpHeaders = new HttpHeaders();
      httpHeaders.setContentType(getContentType());
      HttpBasicAuthentication authentication = new HttpBasicAuthentication(username.getValue(),
          password.getValue());
      httpHeaders.setAuthorization(authentication);
      httpHeaders.add("saleType",
          Empty.isEmpty(saleType) ? ApplicationKeys.SALE_COLD : saleType.getValue());

      if (Empty.isNotEmpty(salesmanId)) {
        httpHeaders.add("salesmanId", salesmanId.getValue());
        httpHeaders.add("salesmanCode", salesmanCode.getValue());
      }

      RestTemplate restTemplate = new RestTemplate();
      restTemplate.getMessageConverters().add(new StringHttpMessageConverter());
      restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
      restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory());
      String url = makeUrl(serverAddress1.getValue(), getMethod());

      HttpEntity httpEntity = getHttpEntity(httpHeaders);
      ResponseEntity<GoodsDtoList> response = restTemplate
          .exchange(url, getHttpMethod(), httpEntity, getType());

      result = true;
      goodsDtoList = response.getBody();

    } catch (ResourceAccessException ex) {
      Log.e(TAG, ex.getMessage(), ex);
      getObserver().publishResult(new TimeOutException());
    } catch (HttpServerErrorException ex) {
      Log.e(TAG, ex.getMessage(), ex);
      if (ex.getStatusCode().equals(HttpStatus.INTERNAL_SERVER_ERROR)) {
        getObserver().publishResult(new InternalServerError());
      } else {
        if (ex.getStatusCode().equals(HttpStatus.NOT_FOUND)) {
          getObserver().publishResult(new URLNotFoundException());
        }
      }
    } catch (HttpClientErrorException ex) {
      getObserver().publishResult(new URLNotFoundException());
    } catch (final BusinessException ex) {
      Log.e(TAG, ex.getMessage(), ex);
      getObserver().publishResult(ex);
    } catch (Exception e) {
      Crashlytics.log(Log.ERROR, "Data transfer", "Error in exchanging RejectedGoodsData " + e.getMessage());
      Log.e(TAG, e.getMessage(), e);
      getObserver().publishResult(new UnknownSystemException(e));
    } finally {
      getObserver().finished(result);
    }
    return goodsDtoList;
  }

  @Override
  public void receiveData(GoodsDtoList data) {

  }

  @Override
  public void beforeTransfer() {
    getObserver()
        .publishResult(context.getString(R.string.message_transferring_rejected_goods_data));
  }

  @Override
  public ResultObserver getObserver() {
    return resultObserver;
  }

  @Override
  public String getMethod() {
    return "goods/reject?custId=" + customerId;
  }

  @Override
  public Class getType() {
    return GoodsDtoList.class;
  }

  @Override
  public HttpMethod getHttpMethod() {
    return HttpMethod.GET;
  }

  @Override
  protected MediaType getContentType() {
    return MediaType.TEXT_PLAIN;
  }

  @Override
  protected HttpEntity getHttpEntity(HttpHeaders headers) {
    HttpEntity<String> entity = new HttpEntity<>("No Params", headers);
    return entity;
  }
}
