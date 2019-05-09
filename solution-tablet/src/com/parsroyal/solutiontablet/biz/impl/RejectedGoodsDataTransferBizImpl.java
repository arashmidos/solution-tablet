package com.parsroyal.solutiontablet.biz.impl;

import android.content.Context;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;
import com.parsroyal.solutiontablet.constants.StatusCodes;
import com.parsroyal.solutiontablet.data.entity.Goods;
import com.parsroyal.solutiontablet.data.event.ErrorEvent;
import com.parsroyal.solutiontablet.data.model.GoodsDtoList;
import com.parsroyal.solutiontablet.service.RestService;
import com.parsroyal.solutiontablet.service.ServiceGenerator;
import com.parsroyal.solutiontablet.util.Empty;
import com.parsroyal.solutiontablet.util.NetworkUtil;
import java.util.List;
import org.greenrobot.eventbus.EventBus;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Arash on 1/14/2018.
 */
public class RejectedGoodsDataTransferBizImpl {

  public static final String TAG = RejectedGoodsDataTransferBizImpl.class.getSimpleName();

  private Context context;

  public RejectedGoodsDataTransferBizImpl(Context context) {
    this.context = context;
  }

  public void getAllRejectedData(Long customerBackendId) {
    if (!NetworkUtil.isNetworkAvailable(context)) {
      EventBus.getDefault().post(new ErrorEvent("reject", StatusCodes.NO_NETWORK));
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
          EventBus.getDefault().post(new ErrorEvent("reject", StatusCodes.SERVER_ERROR));
        }
      }

      @Override
      public void onFailure(Call<JsonArray> call, Throwable t) {
        EventBus.getDefault().post(new ErrorEvent("reject", StatusCodes.NETWORK_ERROR));
      }
    });
  }
}
