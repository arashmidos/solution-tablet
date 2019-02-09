package com.parsroyal.solutiontablet.biz.impl;

import android.content.Context;
import com.parsroyal.solutiontablet.constants.StatusCodes;
import com.parsroyal.solutiontablet.data.event.ErrorEvent;
import com.parsroyal.solutiontablet.data.model.DetectGoodRequest;
import com.parsroyal.solutiontablet.data.model.GoodDetectDetail;
import com.parsroyal.solutiontablet.service.ServiceGenerator;
import com.parsroyal.solutiontablet.service.StoreRestService;
import com.parsroyal.solutiontablet.util.NetworkUtil;
import com.parsroyal.solutiontablet.util.PreferenceHelper;
import java.util.List;
import org.greenrobot.eventbus.EventBus;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StoreRestServiceImpl {

  public void detectGood(Context context, String barcode) {
    if (!NetworkUtil.isNetworkAvailable(context)) {
      EventBus.getDefault().post(new ErrorEvent(StatusCodes.NO_NETWORK));
      return;
    }

    StoreRestService restService = ServiceGenerator.createService(StoreRestService.class);

    Call<List<GoodDetectDetail>> call = restService
        .detectGood(new DetectGoodRequest(barcode, PreferenceHelper.getStockKey()));

    call.enqueue(new Callback<List<GoodDetectDetail>>() {
      @Override
      public void onResponse(Call<List<GoodDetectDetail>> call,
          Response<List<GoodDetectDetail>> response) {
        if (response.isSuccessful()) {
          List<GoodDetectDetail> result = response.body();

        } else {

        }
      }

      @Override
      public void onFailure(Call<List<GoodDetectDetail>> call, Throwable t) {
        EventBus.getDefault().post(new ErrorEvent(StatusCodes.NETWORK_ERROR));
      }
    });
  }

}
