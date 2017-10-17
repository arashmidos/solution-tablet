package com.parsroyal.solutiontablet.biz.impl;

import android.content.Context;
import android.util.Log;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;
import com.parsroyal.solutiontablet.R;
import com.parsroyal.solutiontablet.biz.AbstractDataTransferBizImpl;
import com.parsroyal.solutiontablet.constants.StatusCodes;
import com.parsroyal.solutiontablet.data.entity.Goods;
import com.parsroyal.solutiontablet.data.entity.KeyValue;
import com.parsroyal.solutiontablet.data.event.ErrorEvent;
import com.parsroyal.solutiontablet.data.model.GoodsDtoList;
import com.parsroyal.solutiontablet.exception.BusinessException;
import com.parsroyal.solutiontablet.exception.InternalServerError;
import com.parsroyal.solutiontablet.exception.TimeOutException;
import com.parsroyal.solutiontablet.exception.URLNotFoundException;
import com.parsroyal.solutiontablet.exception.UnknownSystemException;
import com.parsroyal.solutiontablet.service.RestService;
import com.parsroyal.solutiontablet.service.ServiceGenerator;
import com.parsroyal.solutiontablet.ui.observer.ResultObserver;
import com.parsroyal.solutiontablet.util.Empty;
import com.parsroyal.solutiontablet.util.Logger;
import com.parsroyal.solutiontablet.util.LoggingRequestInterceptor;
import com.parsroyal.solutiontablet.util.NetworkUtil;
import com.parsroyal.solutiontablet.util.constants.ApplicationKeys;
import java.util.ArrayList;
import java.util.List;
import org.greenrobot.eventbus.EventBus;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Mahyar on 7/24/2015.
 */
public class RejectedGoodsDataTransferBizImpl extends AbstractDataTransferBizImpl<String> {

  public static final String TAG = RejectedGoodsDataTransferBizImpl.class.getSimpleName();

  private Context context;
  private Long customerId;


  public RejectedGoodsDataTransferBizImpl(Context context) {
    super(context);
    this.context = context;
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
//      beforeTransfer();
      KeyValue token = keyValueDao.retrieveByKey(ApplicationKeys.TOKEN);

      HttpHeaders httpHeaders = new HttpHeaders();
      httpHeaders.setContentType(getContentType());
/*      HttpBasicAuthentication authentication = new HttpBasicAuthentication(username.getValue(),
          password.getValue());
      httpHeaders.setAuthorization(authentication);*/
      httpHeaders.add("saleType",
          Empty.isEmpty(saleType) ? ApplicationKeys.SALE_COLD : saleType.getValue());

      if (Empty.isNotEmpty(salesmanId)) {
        httpHeaders.add("salesmanId", salesmanId.getValue());
        httpHeaders.add("salesmanCode", salesmanCode.getValue());
      }
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

      restTemplate.getMessageConverters().add(new StringHttpMessageConverter());
      restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
      restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory());
      String url = makeUrl(serverAddress1.getValue(), getMethod());

      HttpEntity httpEntity = getHttpEntity(httpHeaders);
      ResponseEntity<String> response = restTemplate
          .exchange(url, getHttpMethod(), httpEntity, getType());

      result = true;
      String responseBody = response.getBody();
      List<Goods> list = new Gson().fromJson(responseBody, new TypeToken<List<Goods>>() {
      }.getType());

      if (Empty.isNotEmpty(list)) {
        goodsDtoList = new GoodsDtoList();
        goodsDtoList.setGoodsDtoList(list);
      }
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
      Logger.sendError("Data transfer", "Error in exchanging RejectedGoodsData " + e.getMessage());
      Log.e(TAG, e.getMessage(), e);
      getObserver().publishResult(new UnknownSystemException(e));
    } finally {
      getObserver().finished(result);
    }
    return goodsDtoList;
  }

  @Override
  public void receiveData(String data) {

  }

  @Override
  public void beforeTransfer() {
    getObserver()
        .publishResult(context.getString(R.string.message_transferring_rejected_goods_data));
  }

  @Override
  public ResultObserver getObserver() {
    return null;
  }

  @Override
  public String getMethod() {
    return "goods/reject?custId=" + customerId;
  }

  @Override
  public Class getType() {
    return String.class;
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

  public void getAllRejectedData(Long customerBackendId) {
    if (!NetworkUtil.isNetworkAvailable(context)) {
      EventBus.getDefault().post(new ErrorEvent(StatusCodes.NO_NETWORK));
      return;
    }

    RestService restService = ServiceGenerator.createService(RestService.class);

    Call<JsonArray> call = restService.getAllRejectedData(customerBackendId);
    call.enqueue(new Callback<JsonArray>() {
      @Override
      public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
        if (response.isSuccessful()) {
          JsonArray rejectedData = response.body();

          GoodsDtoList goodsDtoList = new GoodsDtoList();
          List<Goods> list = new Gson().fromJson(rejectedData, new TypeToken<List<Goods>>() {
          }.getType());

          if (Empty.isNotEmpty(list)) {
            goodsDtoList.setGoodsDtoList(list);
          }
          EventBus.getDefault().post(goodsDtoList);

        } else {
          EventBus.getDefault().post(new ErrorEvent(StatusCodes.SERVER_ERROR));
        }
      }

      @Override
      public void onFailure(Call<JsonArray> call, Throwable t) {
        EventBus.getDefault().post(new ErrorEvent(StatusCodes.NETWORK_ERROR));
      }
    });
  }
}
