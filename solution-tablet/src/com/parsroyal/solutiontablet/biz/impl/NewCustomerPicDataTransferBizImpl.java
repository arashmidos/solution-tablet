package com.parsroyal.solutiontablet.biz.impl;

import android.content.Context;
import android.util.Log;
import com.parsroyal.solutiontablet.R;
import com.parsroyal.solutiontablet.biz.AbstractDataTransferBizImpl;
import com.parsroyal.solutiontablet.constants.StatusCodes;
import com.parsroyal.solutiontablet.data.dao.CustomerPicDao;
import com.parsroyal.solutiontablet.data.dao.impl.CustomerPicDaoImpl;
import com.parsroyal.solutiontablet.data.event.DataTransferErrorEvent;
import com.parsroyal.solutiontablet.data.event.DataTransferSuccessEvent;
import com.parsroyal.solutiontablet.data.event.ErrorEvent;
import com.parsroyal.solutiontablet.data.event.SuccessEvent;
import com.parsroyal.solutiontablet.data.model.CustomerDto;
import com.parsroyal.solutiontablet.exception.BusinessException;
import com.parsroyal.solutiontablet.exception.InternalServerError;
import com.parsroyal.solutiontablet.exception.TimeOutException;
import com.parsroyal.solutiontablet.exception.URLNotFoundException;
import com.parsroyal.solutiontablet.exception.UnknownSystemException;
import com.parsroyal.solutiontablet.service.impl.CustomerServiceImpl;
import com.parsroyal.solutiontablet.ui.observer.ResultObserver;
import com.parsroyal.solutiontablet.util.Empty;
import com.parsroyal.solutiontablet.util.Logger;
import java.io.File;
import java.nio.charset.Charset;
import org.greenrobot.eventbus.EventBus;
import org.springframework.core.io.FileSystemResource;
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
  private final Long visitId;
  private final CustomerServiceImpl customerService;
  private final CustomerDto customer;

  private Context context;
  private CustomerPicDao customerPicDao;

  public NewCustomerPicDataTransferBizImpl(Context context, File pics, Long visitId,
      Long customerId) {
    super(context);
    this.context = context;
    this.customerPicDao = new CustomerPicDaoImpl(context);
    this.customerService = new CustomerServiceImpl(context);
    this.pics = pics;
    this.visitId = visitId;
    this.customer = customerService.getCustomerDtoById(customerId);

  }

  @Override
  public boolean exchangeData() {
    boolean result = false;
    try {
      prepare();

      beforeTransfer();

      HttpHeaders httpHeaders = new HttpHeaders();

      httpHeaders.add("Authorization", "Bearer " + token.getValue());

      if (Empty.isNotEmpty(customer)) {
        httpHeaders.add("backendId", String.valueOf(customer.getBackendId()));
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
      if (Empty.isNotEmpty(customer)) {
        parts.add("customerPics.zip", new FileSystemResource(pics));
      } else {
        parts.add("zipfile.zip", new FileSystemResource(pics));
      }
      HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(parts,
          httpHeaders);

      String url = makeUrl(serverAddress1.getValue(), getMethod());

      ResponseEntity<String> response = restTemplate
          .exchange(url, getHttpMethod(), requestEntity, getType());

      receiveData(response.getBody());

      result = true;

    } catch (ResourceAccessException ex) {
      Log.e(TAG, ex.getMessage(), ex);
      if (Empty.isNotEmpty(getObserver())) {
        getObserver().publishResult(new TimeOutException());
      }
    } catch (HttpServerErrorException ex) {
      Log.e(TAG, ex.getMessage(), ex);
      if (ex.getStatusCode().equals(HttpStatus.INTERNAL_SERVER_ERROR) && Empty
          .isNotEmpty(getObserver())) {
        getObserver().publishResult(new InternalServerError());
      } else {
        if (ex.getStatusCode().equals(HttpStatus.NOT_FOUND) && Empty.isNotEmpty(getObserver())) {
          getObserver().publishResult(new URLNotFoundException());
        }
      }
    } catch (HttpClientErrorException ex) {
      if (Empty.isNotEmpty(getObserver())) {
        getObserver().publishResult(new URLNotFoundException());
      }
    } catch (final BusinessException ex) {
      Log.e(TAG, ex.getMessage(), ex);
      if (Empty.isNotEmpty(getObserver())) {
        getObserver().publishResult(ex);
      }
    } catch (Exception e) {
      Logger.sendError("Data transfer", "Error in exchanging NewCustomerPicData " + e.getMessage());
      Log.e(TAG, e.getMessage(), e);
      if (Empty.isNotEmpty(getObserver())) {
        getObserver().publishResult(new UnknownSystemException(e));
      }
    }

    return result;
  }

  @Override
  public void receiveData(String data) {
    if (data.equals("1")) {
      if (Empty.isNotEmpty(visitId)) {
        //Sent single visit images
        customerPicDao.updatePicturesByVisitId(visitId);
        EventBus.getDefault().post(new SuccessEvent(context.getString(
            R.string.new_customers_pic_transferred_successfully), StatusCodes.SUCCESS));
      } else if (Empty.isNotEmpty(customer)) {
        customerPicDao.updateAllPicturesByCustomerId(customer.getId());
      } else {
        //Sent all images
        customerPicDao.updateAllPictures();
        EventBus.getDefault().post(new DataTransferSuccessEvent(context.getString(
            R.string.new_customers_pic_transferred_successfully), StatusCodes.SUCCESS));
      }
    } else {
      if (Empty.isNotEmpty(visitId)) {
        EventBus.getDefault().post(new ErrorEvent(context.getString(
            R.string.error_new_customers_pic_transfer), StatusCodes.SERVER_ERROR));
      } else if (Empty.isNotEmpty(customer)) {
      } else {
        EventBus.getDefault().post(new DataTransferErrorEvent(context.getString(
            R.string.error_new_customers_pic_transfer), StatusCodes.SERVER_ERROR));
      }
    }
  }

  @Override
  public void beforeTransfer() {
  }

  @Override
  public ResultObserver getObserver() {
    return null;
  }

  @Override
  public String getMethod() {
    return Empty.isNotEmpty(customer) ? "customers/saveCustomerPics" : "customers/images";
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
    return new MediaType("TEXT", "PLAIN", Charset.forName("UTF-8"));
  }

  @Override
  protected HttpEntity getHttpEntity(HttpHeaders headers) {
    return null;
  }

}
