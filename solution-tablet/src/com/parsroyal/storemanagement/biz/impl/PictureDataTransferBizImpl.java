package com.parsroyal.storemanagement.biz.impl;

import android.content.Context;
import com.parsroyal.storemanagement.R;
import com.parsroyal.storemanagement.constants.CustomerStatus;
import com.parsroyal.storemanagement.constants.StatusCodes;
import com.parsroyal.storemanagement.constants.VisitInformationDetailType;
import com.parsroyal.storemanagement.data.dao.CustomerPicDao;
import com.parsroyal.storemanagement.data.dao.impl.CustomerPicDaoImpl;
import com.parsroyal.storemanagement.data.entity.CustomerPic;
import com.parsroyal.storemanagement.data.event.DataTransferErrorEvent;
import com.parsroyal.storemanagement.data.event.DataTransferEvent;
import com.parsroyal.storemanagement.service.RestService;
import com.parsroyal.storemanagement.service.ServiceGenerator;
import com.parsroyal.storemanagement.service.VisitService;
import com.parsroyal.storemanagement.service.impl.CustomerServiceImpl;
import com.parsroyal.storemanagement.service.impl.VisitServiceImpl;
import com.parsroyal.storemanagement.util.Empty;
import com.parsroyal.storemanagement.util.NetworkUtil;
import com.parsroyal.storemanagement.util.NumberUtil;
import java.io.File;
import java.io.IOException;
import java.util.Locale;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.MultipartBody.Part;
import okhttp3.RequestBody;
import org.greenrobot.eventbus.EventBus;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by Arash on 6/13/2016.
 */
public class PictureDataTransferBizImpl {

  private final CustomerServiceImpl customerService;
  private final VisitService visitService;

  private Context context;
  private CustomerPicDao customerPicDao;
  private CustomerPic data;
  private int success = 0;
  private int total = 0;

  public PictureDataTransferBizImpl(Context context) {
    this.context = context;
    this.customerPicDao = new CustomerPicDaoImpl(context);
    this.customerService = new CustomerServiceImpl(context);
    this.visitService = new VisitServiceImpl(context);
  }

  public int getSuccess() {
    return success;
  }

  public int getTotal() {
    return total;
  }

  public void exchangeData() {
    if (!NetworkUtil.isNetworkAvailable(context)) {
      EventBus.getDefault().post(new DataTransferErrorEvent(StatusCodes.NO_NETWORK));
      return;
    }

    RestService restService = ServiceGenerator.createService(RestService.class);

    // create RequestBody instance from file
    RequestBody requestFile = RequestBody
        .create(MediaType.parse("image/jpeg"), new File(data.getTitle()));

    // MultipartBody.Part is used to send also the actual file name

    // add another part within the multipart request
    String customerBackend = String.valueOf(data.getCustomerBackendId());

    RequestBody backendId = RequestBody.create(MultipartBody.FORM, customerBackend);
    Part body = Part.createFormData("picture", data.getName(), requestFile);

    // finally, execute the request
    Call<String> call = restService.upload(backendId, body);
    try {
      Response<String> response = call.execute();
      if (response.isSuccessful()) {
        String picBackendId = response.body();
        if (Empty.isNotEmpty(picBackendId)) {
          data.setBackendId(Long.valueOf(picBackendId));
          data.setStatus(CustomerStatus.SENT.getId());
          customerPicDao.update(data);
          visitService.updateVisitDetailId(VisitInformationDetailType.TAKE_PICTURE, data.getId(),
              data.getBackendId());
          success++;
        }
      }
      EventBus.getDefault()
          .post(new DataTransferEvent(getSuccessfulMessage(), StatusCodes.UPDATE));
    } catch (IOException e) {
      e.printStackTrace();
      EventBus.getDefault()
          .post(new DataTransferEvent(getSuccessfulMessage(), StatusCodes.UPDATE));
    }
  }

  public CustomerPic getData() {
    return data;
  }

  public void setData(CustomerPic data) {
    this.data = data;
    total++;
  }

  public String getSuccessfulMessage() {
    return NumberUtil.digitsToPersian(String
        .format(Locale.getDefault(), context.getString(R.string.data_transfered_result),
            String.valueOf(success),
            String.valueOf(total - success)));
  }
}
