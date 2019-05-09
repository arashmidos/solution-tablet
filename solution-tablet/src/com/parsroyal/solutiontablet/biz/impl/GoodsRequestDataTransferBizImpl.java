package com.parsroyal.solutiontablet.biz.impl;

import android.content.Context;
import android.util.Log;
import com.parsroyal.solutiontablet.biz.KeyValueBiz;
import com.parsroyal.solutiontablet.constants.StatusCodes;
import com.parsroyal.solutiontablet.data.entity.KeyValue;
import com.parsroyal.solutiontablet.data.event.DataTransferErrorEvent;
import com.parsroyal.solutiontablet.data.event.DataTransferSuccessEvent;
import com.parsroyal.solutiontablet.service.GetDataRestService;
import com.parsroyal.solutiontablet.service.ServiceGenerator;
import com.parsroyal.solutiontablet.util.Empty;
import com.parsroyal.solutiontablet.util.NetworkUtil;
import com.parsroyal.solutiontablet.util.constants.ApplicationKeys;
import java.io.IOException;
import org.greenrobot.eventbus.EventBus;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Arash on 2017-04-24
 */
public class GoodsRequestDataTransferBizImpl {

  public static final String TAG = GoodsRequestDataTransferBizImpl.class.getSimpleName();
  private final String userCode;

  private Context context;
  private KeyValueBiz keyValueBiz;

  public GoodsRequestDataTransferBizImpl(Context context) {
    this.context = context;
    this.keyValueBiz = new KeyValueBizImpl();
    userCode = keyValueBiz.findByKey(ApplicationKeys.SETTING_USER_CODE).getValue();
  }

  public void exchangeData() {
    if (!NetworkUtil.isNetworkAvailable(context)) {
      EventBus.getDefault().post(new DataTransferErrorEvent(StatusCodes.NO_NETWORK));
    }

    GetDataRestService restService = ServiceGenerator.createService(GetDataRestService.class);

    Call<String> call = restService.getGoodsRequest(userCode);

    call.enqueue(new Callback<String>() {
      @Override
      public void onResponse(Call<String> call, Response<String> response) {
        if (response.isSuccessful()) {
          String requestCode = response.body();
          if (Empty.isNotEmpty(requestCode)) {
            keyValueBiz.save(new KeyValue(ApplicationKeys.GOODS_REQUEST_ID, requestCode));
            EventBus.getDefault().post(new DataTransferSuccessEvent("", StatusCodes.SUCCESS));
          } else {
            EventBus.getDefault().post(new DataTransferSuccessEvent("", StatusCodes.NO_DATA_ERROR));
          }
        } else {
          try {
            Log.d("TAG", response.errorBody().string());
          } catch (IOException e) {
            e.printStackTrace();
          }
          EventBus.getDefault().post(new DataTransferErrorEvent(StatusCodes.SERVER_ERROR));
        }
      }

      @Override
      public void onFailure(Call<String> call, Throwable t) {
        EventBus.getDefault().post(new DataTransferErrorEvent(StatusCodes.NETWORK_ERROR));
      }
    });
  }
}
