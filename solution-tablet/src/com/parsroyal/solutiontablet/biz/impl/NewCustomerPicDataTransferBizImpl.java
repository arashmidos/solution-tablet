package com.parsroyal.solutiontablet.biz.impl;

import android.content.Context;
import android.util.Log;
import com.parsroyal.solutiontablet.R;
import com.parsroyal.solutiontablet.biz.AbstractDataTransferBizImpl;
import com.parsroyal.solutiontablet.data.dao.CustomerPicDao;
import com.parsroyal.solutiontablet.data.dao.impl.CustomerPicDaoImpl;
import com.parsroyal.solutiontablet.exception.BusinessException;
import com.parsroyal.solutiontablet.exception.InternalServerError;
import com.parsroyal.solutiontablet.exception.TimeOutException;
import com.parsroyal.solutiontablet.exception.URLNotFoundException;
import com.parsroyal.solutiontablet.exception.UnknownSystemException;
import com.parsroyal.solutiontablet.ui.observer.ResultObserver;
import com.parsroyal.solutiontablet.util.Empty;
import com.parsroyal.solutiontablet.util.Logger;
import java.io.File;
import java.nio.charset.Charset;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpBasicAuthentication;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

/**
 * Created by Arash on 6/13/2016.
 */
public class NewCustomerPicDataTransferBizImpl extends AbstractDataTransferBizImpl<String> {

  public static final String TAG = NewCustomerPicDataTransferBizImpl.class.getSimpleName();
  private final File pics;

  private Context context;
  private CustomerPicDao customerPicDao;
  private ResultObserver observer;

  public NewCustomerPicDataTransferBizImpl(Context context, ResultObserver resultObserver,
      File pics) {
    super(context);
    this.context = context;
    this.customerPicDao = new CustomerPicDaoImpl(context);
    this.observer = resultObserver;
    this.pics = pics;
  }

  @Override
  public boolean exchangeData() {
    boolean result = false;
    try {
      prepare();

      beforeTransfer();

      HttpHeaders httpHeaders = new HttpHeaders();
      HttpBasicAuthentication authentication = new HttpBasicAuthentication(username.getValue(),
          password.getValue());
      httpHeaders.setAuthorization(authentication);

      if (Empty.isNotEmpty(salesmanId)) {
        httpHeaders.add("salesmanId", salesmanId.getValue());
        httpHeaders.add("salesmanCode", salesmanId.getValue());
      }

      RestTemplate restTemplate = new RestTemplate();
      restTemplate.getMessageConverters().add(new StringHttpMessageConverter());
      restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
      FormHttpMessageConverter formConverter = new FormHttpMessageConverter();
      formConverter.setCharset(Charset.forName("UTF8"));
      restTemplate.getMessageConverters().add(formConverter);
      restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
      restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory());

      MultiValueMap<String, Object> parts = new LinkedMultiValueMap<>();
      parts.add("zipfile.zip", new FileSystemResource(pics));
      HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<MultiValueMap<String, Object>>(
          parts, httpHeaders);

      String url = makeUrl(serverAddress1.getValue(), getMethod());

      ResponseEntity<String> response = restTemplate
          .exchange(url, getHttpMethod(), requestEntity, getType());

      receiveData(response.getBody());

      result = true;

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
      Logger.sendError("Data transfer", "Error in exchanging NewCustomerPicData " + e.getMessage());
      Log.e(TAG, e.getMessage(), e);
      getObserver().publishResult(new UnknownSystemException(e));
    } finally {
      getObserver().finished(result);
    }
    return result;
  }

  @Override
  public void receiveData(String data) {
    if (data.equals("1")) {
      customerPicDao.deleteAll();
      getObserver()
          .publishResult(context.getString(R.string.new_customers_pic_transferred_successfully));
    } else {
      getObserver().publishResult(context.getString(R.string.error_new_customers_pic_transfer));
    }
  }

  @Override
  public void beforeTransfer() {
    getObserver().publishResult(context.getString(R.string.sending_new_customers_pic_data));
  }

  @Override
  public ResultObserver getObserver() {
    return observer;
  }

  @Override
  public String getMethod() {
    return "customer/images";
  }

  @Override
  public Class getType() {
    return String.class;
  }

  @Override
  public HttpMethod getHttpMethod() {
    return HttpMethod.POST;
  }

  @Override
  protected MediaType getContentType() {
    MediaType contentType = new MediaType("TEXT", "PLAIN", Charset.forName("UTF-8"));
    return contentType;
  }

  @Override
  protected HttpEntity getHttpEntity(HttpHeaders headers) {
    return null;
  }

}
