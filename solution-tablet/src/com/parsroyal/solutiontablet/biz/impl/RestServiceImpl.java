package com.parsroyal.solutiontablet.biz.impl;

import android.content.Context;
import com.parsroyal.solutiontablet.constants.StatusCodes;
import com.parsroyal.solutiontablet.data.event.ErrorEvent;
import com.parsroyal.solutiontablet.data.event.NavigateErrorEvent;
import com.parsroyal.solutiontablet.data.model.CustomerLocationDto;
import com.parsroyal.solutiontablet.data.model.UpdatePusheRequest;
import com.parsroyal.solutiontablet.service.RestService;
import com.parsroyal.solutiontablet.service.ServiceGenerator;
import com.parsroyal.solutiontablet.util.Empty;
import com.parsroyal.solutiontablet.util.NetworkUtil;
import com.parsroyal.solutiontablet.util.PreferenceHelper;
import com.parsroyal.solutiontablet.vrp.model.OptimizedRouteResponse;
import java.util.List;
import org.greenrobot.eventbus.EventBus;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

public class RestServiceImpl {

  private static String TAG = RestServiceImpl.class.getName();

  public void updatePusheId(Context context, String pusheId, String GCMToken) {
    if (!NetworkUtil.isNetworkAvailable(context)) {
      EventBus.getDefault().post(new ErrorEvent(StatusCodes.NO_NETWORK));
      return;
    }

    RestService restService = ServiceGenerator.createService(RestService.class);

    Call<String> call = restService.updatePusheId(new UpdatePusheRequest(pusheId, GCMToken));

    call.enqueue(new Callback<String>() {
      @Override
      public void onResponse(Call<String> call, Response<String> response) {
        if (response.isSuccessful()) {
          String result = response.body();
          if ("1".equals(result)) {
            Timber.d("PusheID updated");
          } else {
            Timber.d("PusheID update failed");
          }
        }
      }

      @Override
      public void onFailure(Call<String> call, Throwable t) {
        EventBus.getDefault().post(new ErrorEvent(StatusCodes.NETWORK_ERROR));
      }
    });
  }

  public void testValOR(Context context) {
    if (!NetworkUtil.isNetworkAvailable(context)) {
      EventBus.getDefault().post(new ErrorEvent(StatusCodes.NO_NETWORK));
      return;
    }

    RestService restService = ServiceGenerator.createService(RestService.class);

    Call<OptimizedRouteResponse> call = restService.testValOR();

    call.enqueue(new Callback<OptimizedRouteResponse>() {
      @Override
      public void onResponse(Call<OptimizedRouteResponse> call,
          Response<OptimizedRouteResponse> response) {
        if (response.isSuccessful()) {
          OptimizedRouteResponse result = response.body();
          if (result != null && Empty.isEmpty(result.getError())) {
            EventBus.getDefault().post(result);
          } else {
            EventBus.getDefault().post(new ErrorEvent(StatusCodes.SERVER_ERROR));
          }
        }
      }

      @Override
      public void onFailure(Call<OptimizedRouteResponse> call, Throwable t) {
        EventBus.getDefault().post(new ErrorEvent(StatusCodes.NETWORK_ERROR));
      }
    });
  }

  public void valOptimizedRoute(Context context, long visitLineBackendId,
      List<CustomerLocationDto> list) {
    if (!NetworkUtil.isNetworkAvailable(context)) {
      EventBus.getDefault().post(new NavigateErrorEvent(StatusCodes.NO_NETWORK));
      return;
    }

    RestService restService = ServiceGenerator.createService(RestService.class);

    Call<OptimizedRouteResponse> call = restService.valOptimizedRoute(list);

    call.enqueue(new Callback<OptimizedRouteResponse>() {
      @Override
      public void onResponse(Call<OptimizedRouteResponse> call,
          Response<OptimizedRouteResponse> response) {
        if (response.isSuccessful()) {
          OptimizedRouteResponse result = response.body();
          if (result != null && Empty.isEmpty(result.getError()) && result.getTrip() !=null) {
            PreferenceHelper.setOptimizedRoute(visitLineBackendId, result);
            EventBus.getDefault().post(result);
          } else {
            EventBus.getDefault().post(new NavigateErrorEvent(StatusCodes.SERVER_ERROR));
          }
        }
      }

      @Override
      public void onFailure(Call<OptimizedRouteResponse> call, Throwable t) {
        EventBus.getDefault().post(new NavigateErrorEvent(StatusCodes.NETWORK_ERROR));
      }
    });
  }
}
