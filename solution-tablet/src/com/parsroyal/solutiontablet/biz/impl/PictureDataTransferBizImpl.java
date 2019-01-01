package com.parsroyal.solutiontablet.biz.impl;

import android.content.Context;
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

  public boolean exchangeData() {
    if (!NetworkUtil.isNetworkAvailable(context)) {
      EventBus.getDefault().post(new DataTransferErrorEvent(StatusCodes.NO_NETWORK));
    }

    RestService restService = ServiceGenerator.createService(RestService.class);

    // create RequestBody instance from file
    RequestBody requestFile = RequestBody.create(MediaType.parse("application/zip"), pics);

    // MultipartBody.Part is used to send also the actual file name

    // add another part within the multipart request
    RequestBody backendId = null;
    String customerBackend;

    customerBackend = customer != null ? String.valueOf(customer.getBackendId()) : "";

    backendId = RequestBody.create(MultipartBody.FORM, customerBackend);
    MultipartBody.Part body = MultipartBody.Part
        .createFormData("picture", pics.getName(), requestFile);

    // finally, execute the request
    Call<String> call = restService.upload(backendId, body);
    call.enqueue(new Callback<String>() {
      @Override
      public void onResponse(Call<String> call, Response<String> response) {
        if (response.isSuccessful() && "1".equals(response.body())) {
          receiveData("1");
        } else {
          receiveData("0");
        }
      }

      @Override
      public void onFailure(Call<String> call, Throwable t) {
        receiveData("0");
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
}
