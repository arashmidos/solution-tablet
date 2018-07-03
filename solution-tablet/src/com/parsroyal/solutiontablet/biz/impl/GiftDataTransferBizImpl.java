package com.parsroyal.solutiontablet.biz.impl;

import android.content.Context;
import android.util.Log;
import com.parsroyal.solutiontablet.constants.StatusCodes;
import com.parsroyal.solutiontablet.data.event.DataTransferSuccessEvent;
import com.parsroyal.solutiontablet.data.event.ErrorEvent;
import com.parsroyal.solutiontablet.service.GetDataRestService;
import com.parsroyal.solutiontablet.service.ServiceGenerator;
import com.parsroyal.solutiontablet.util.Empty;
import com.parsroyal.solutiontablet.util.NetworkUtil;
import java.io.IOException;
import java.util.List;
import org.greenrobot.eventbus.EventBus;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Arash on 27/12/2017
 */
public class GiftDataTransferBizImpl {

  public static final String TAG = GiftDataTransferBizImpl.class.getSimpleName();

  private Context context;

  public GiftDataTransferBizImpl(Context context) {
    this.context = context;
  }

  public void exchangeData(Long orderBackendId, String saleType) {
    if (!NetworkUtil.isNetworkAvailable(context)) {
      EventBus.getDefault().post(new ErrorEvent(StatusCodes.NO_NETWORK));
    }

    GetDataRestService restService = ServiceGenerator.createService(GetDataRestService.class);

    Call<List<String>> call = restService.getGiftResult(saleType,String.valueOf(orderBackendId));

    call.enqueue(new Callback<List<String>>() {
      @Override
      public void onResponse(Call<List<String>> call, Response<List<String>> response) {
        if (response.isSuccessful()) {
          List<String> data = response.body();
          if (Empty.isNotEmpty(data)) {

            EventBus.getDefault().post(new DataTransferSuccessEvent("", StatusCodes.SUCCESS, data));

          } else {
            EventBus.getDefault().post(new DataTransferSuccessEvent("", StatusCodes.NO_DATA_ERROR));
          }
        } else {
          try {
            Log.d("TAG", response.errorBody().string());
          } catch (IOException e) {
            e.printStackTrace();
          }
          EventBus.getDefault().post(new ErrorEvent(StatusCodes.SERVER_ERROR));
        }
      }

      @Override
      public void onFailure(Call<List<String>> call, Throwable t) {
//        EventBus.getDefault().post(new ErrorEvent(StatusCodes.NETWORK_ERROR));
        EventBus.getDefault().post(new DataTransferSuccessEvent("", StatusCodes.NO_DATA_ERROR));
      }
    });
  }
}
