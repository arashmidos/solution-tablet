package com.parsroyal.solutiontablet.biz.impl;

import android.content.Context;
import android.util.Log;
import com.parsroyal.solutiontablet.R;
import com.parsroyal.solutiontablet.constants.StatusCodes;
import com.parsroyal.solutiontablet.data.dao.CustomerPicDao;
import com.parsroyal.solutiontablet.data.dao.impl.CustomerPicDaoImpl;
import com.parsroyal.solutiontablet.data.event.DataTransferErrorEvent;
import com.parsroyal.solutiontablet.data.event.DataTransferSuccessEvent;
import com.parsroyal.solutiontablet.data.event.ErrorEvent;
import com.parsroyal.solutiontablet.data.event.SuccessEvent;
import com.parsroyal.solutiontablet.data.model.CustomerDto;
import com.parsroyal.solutiontablet.service.RestService;
import com.parsroyal.solutiontablet.service.ServiceGenerator;
import com.parsroyal.solutiontablet.service.impl.CustomerServiceImpl;
import com.parsroyal.solutiontablet.util.Empty;
import com.parsroyal.solutiontablet.util.NetworkUtil;
import java.io.File;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import org.greenrobot.eventbus.EventBus;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Arash on 6/13/2016.
 */
public class PictureDataTransferBizImpl {

  public static final String TAG = PictureDataTransferBizImpl.class.getSimpleName();
  private final File pics;
  private final Long visitId;
  private final CustomerServiceImpl customerService;
  private final CustomerDto customer;

  private Context context;
  private CustomerPicDao customerPicDao;

  public PictureDataTransferBizImpl(Context context, File pics, Long visitId, Long customerId) {
    this.context = context;
    this.customerPicDao = new CustomerPicDaoImpl(context);
    this.customerService = new CustomerServiceImpl(context);
    this.pics = pics;
    this.visitId = visitId;
    this.customer = customerService.getCustomerDtoById(customerId);
  }
/*

  public boolean exchangeData() {
    boolean result = false;
    try {

      if (Empty.isNotEmpty(customer)) {
        httpHeaders.add("backendId", String.valueOf(customer.getBackendId()));
      }

      //
      RestTemplate restTemplate = new RestTemplate();
      restTemplate.getMessageConverters().add(new StringHttpMessageConverter());
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
*/

  public boolean exchangeData() {
    if (!NetworkUtil.isNetworkAvailable(context)) {
      EventBus.getDefault().post(new DataTransferErrorEvent(StatusCodes.NO_NETWORK));
    }

    RestService restService = ServiceGenerator.createService(RestService.class);

    // create RequestBody instance from file
    RequestBody requestFile = RequestBody.create(MediaType.parse("application/zip"), pics);

    // MultipartBody.Part is used to send also the actual file name
    MultipartBody.Part body =
        MultipartBody.Part.createFormData("picture", pics.getName(), requestFile);

    // add another part within the multipart request
    String descriptionString = "hello, this is description speaking";
    RequestBody description = RequestBody.create(okhttp3.MultipartBody.FORM, descriptionString);

    // finally, execute the request
    Call<ResponseBody> call = restService.upload(description, body);
    call.enqueue(new Callback<ResponseBody>() {
      @Override
      public void onResponse(Call<ResponseBody> call,
          Response<ResponseBody> response) {
        Log.v("Upload", "success");
      }

      @Override
      public void onFailure(Call<ResponseBody> call, Throwable t) {
        Log.e("Upload error:", t.getMessage());
      }
    });
    return true;
  }

  public void receiveData(String data) {
    if (data.equals("1")) {
      if (Empty.isNotEmpty(visitId)) {
        //Sent single visit images
        customerPicDao.updatePicturesByVisitId(visitId);
        EventBus.getDefault().post(new SuccessEvent("", StatusCodes.SUCCESS));
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

  public String getMethod() {
    return Empty.isNotEmpty(customer) ? "customers/saveCustomerPics" : "customers/images";
  }
}
