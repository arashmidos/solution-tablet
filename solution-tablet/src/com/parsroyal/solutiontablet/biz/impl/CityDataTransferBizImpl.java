package com.parsroyal.solutiontablet.biz.impl;

import android.content.Context;
import android.util.Log;
import com.parsroyal.solutiontablet.constants.StatusCodes;
import com.parsroyal.solutiontablet.data.dao.CityDao;
import com.parsroyal.solutiontablet.data.dao.impl.CityDaoImpl;
import com.parsroyal.solutiontablet.data.entity.City;
import com.parsroyal.solutiontablet.data.event.DataTransferErrorEvent;
import com.parsroyal.solutiontablet.data.event.DataTransferSuccessEvent;
import com.parsroyal.solutiontablet.data.model.CityList;
import com.parsroyal.solutiontablet.service.GetDataRestService;
import com.parsroyal.solutiontablet.service.ServiceGenerator;
import com.parsroyal.solutiontablet.util.CharacterFixUtil;
import com.parsroyal.solutiontablet.util.Empty;
import com.parsroyal.solutiontablet.util.NetworkUtil;
import java.io.IOException;
import org.greenrobot.eventbus.EventBus;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Arash on 27/12/2017
 */
public class CityDataTransferBizImpl {

  private Context context;
  private CityDao cityDao;

  public CityDataTransferBizImpl(Context context) {
    this.context = context;
    this.cityDao = new CityDaoImpl(context);
  }

  public void getAllCities() {
    if (!NetworkUtil.isNetworkAvailable(context)) {
      EventBus.getDefault().post(new DataTransferErrorEvent(StatusCodes.NO_NETWORK));
    }

    GetDataRestService restService = ServiceGenerator.createService(GetDataRestService.class);

    Call<CityList> call = restService.getAllCities();

    call.enqueue(new Callback<CityList>() {
      @Override
      public void onResponse(Call<CityList> call, Response<CityList> response) {
        if (response.isSuccessful()) {
          CityList cityList = response.body();
          if (Empty.isNotEmpty(cityList)) {
            cityDao.deleteAll();
            for (City city : cityList.getCityList()) {
              city.setTitle(CharacterFixUtil.fixString(city.getTitle()));
            }
            cityDao.bulkInsert(cityList.getCityList());

            EventBus.getDefault().post(new DataTransferSuccessEvent("", StatusCodes.SUCCESS));
          } else {
            EventBus.getDefault().post(new DataTransferErrorEvent(StatusCodes.INVALID_DATA));
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
      public void onFailure(Call<CityList> call, Throwable t) {
        EventBus.getDefault().post(new DataTransferErrorEvent(StatusCodes.NETWORK_ERROR));
      }
    });
  }
}
