package com.parsroyal.solutiontablet.biz.impl;

import android.content.Context;
import android.util.Log;
import com.parsroyal.solutiontablet.constants.StatusCodes;
import com.parsroyal.solutiontablet.data.entity.Customer;
import com.parsroyal.solutiontablet.data.event.DataTransferSuccessEvent;
import com.parsroyal.solutiontablet.data.event.ErrorEvent;
import com.parsroyal.solutiontablet.data.event.SearchCustomerSuccessEvent;
import com.parsroyal.solutiontablet.service.SearchGoodService;
import com.parsroyal.solutiontablet.service.ServiceGenerator;
import com.parsroyal.solutiontablet.util.Empty;
import com.parsroyal.solutiontablet.util.NetworkUtil;
import java.io.IOException;
import java.util.List;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import org.greenrobot.eventbus.EventBus;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by shkbhbb on 10/22/18.
 */

public class SearchGoodServiceImpl {

  public static final String TAG = SearchGoodServiceImpl.class.getSimpleName();

  private Context context;

  public SearchGoodServiceImpl(Context context) {
    this.context = context;
  }

  public void search(String keyWord) {
    if (!NetworkUtil.isNetworkAvailable(context)) {
      EventBus.getDefault().post(new ErrorEvent(StatusCodes.NO_NETWORK));
    }

    SearchGoodService restService = ServiceGenerator.createService(SearchGoodService.class);
    RequestBody body =
        RequestBody.create(MediaType.parse("text/plain"), keyWord);
    Call<List<Customer>> call = restService.search(body);

    call.enqueue(new Callback<List<Customer>>() {
      @Override
      public void onResponse(Call<List<Customer>> call, Response<List<Customer>> response) {
        if (response.isSuccessful()) {
          List<Customer> data = response.body();
          if (Empty.isNotEmpty(data)) {
            EventBus.getDefault()
                .post(new SearchCustomerSuccessEvent("", StatusCodes.SUCCESS, data));
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
      public void onFailure(Call<List<Customer>> call, Throwable t) {
//        EventBus.getDefault().post(new ErrorEvent(StatusCodes.NETWORK_ERROR));
        EventBus.getDefault().post(new DataTransferSuccessEvent("", StatusCodes.NO_DATA_ERROR));
      }
    });
  }
}
