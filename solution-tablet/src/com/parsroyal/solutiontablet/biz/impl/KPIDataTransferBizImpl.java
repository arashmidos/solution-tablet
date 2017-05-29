package com.parsroyal.solutiontablet.biz.impl;

import android.content.Context;
import android.util.Log;
import com.crashlytics.android.Crashlytics;
import com.parsroyal.solutiontablet.R;
import com.parsroyal.solutiontablet.biz.AbstractDataTransferBizImpl;
import com.parsroyal.solutiontablet.data.model.KPIDto;
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
 * Created by Arash on 2016-09-21.
 */
public class KPIDataTransferBizImpl extends AbstractDataTransferBizImpl<KPIDto> {

  public static final String TAG = KPIDataTransferBizImpl.class.getSimpleName();

  private Context context;
  private ResultObserver resultObserver;
  private Long customerId;
  private long customerBackendId;

  public KPIDataTransferBizImpl(Context context, ResultObserver resultObserver) {
    super(context);
    this.context = context;
    this.resultObserver = resultObserver;
  }

  public KPIDto exchangeData(long customerBackendId) {
    boolean result = false;
    KPIDto kpiDto = null;
    this.customerBackendId = customerBackendId;
    try {
      prepare();
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
      String url = makeUrl(serverAddress1.getValue(), serverAddress2.getValue(), getMethod());

      HttpEntity httpEntity = getHttpEntity(httpHeaders);
      ResponseEntity<KPIDto> response = restTemplate
          .exchange(url, getHttpMethod(), httpEntity, getType());

      result = true;

      kpiDto = response.getBody();

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
      Crashlytics.log(Log.ERROR, "Data transfer", "Error in exchanging KPIData" + e.getMessage());
      Log.e(TAG, e.getMessage(), e);
      getObserver().publishResult(new UnknownSystemException(e));
    } finally {
      getObserver().finished(result);
    }
    return kpiDto;
  }

  @Override
  public void receiveData(KPIDto data) {

  }

  @Override
  public void beforeTransfer() {
    resultObserver.publishResult(context.getString(R.string.message_transferring_kpi_data));
  }

  @Override
  public ResultObserver getObserver() {
    return resultObserver;
  }

  @Override
  public String getMethod() {
    return customerBackendId == -1 ? "kpi/salesman" : "kpi/customer";
  }

  @Override
  public Class getType() {
    return KPIDto.class;
  }

  @Override
  public HttpMethod getHttpMethod() {
    return HttpMethod.POST;
  }

  @Override
  protected MediaType getContentType() {
    return MediaType.TEXT_PLAIN;
  }

  @Override
  protected HttpEntity getHttpEntity(HttpHeaders headers) {
    HttpEntity<String> entity = new HttpEntity<>(String.valueOf(customerBackendId), headers);
    return entity;
  }
}
