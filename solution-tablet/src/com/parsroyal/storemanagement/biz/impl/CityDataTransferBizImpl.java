package com.parsroyal.storemanagement.biz.impl;

import android.content.Context;
import android.util.Log;
import com.parsroyal.storemanagement.constants.StatusCodes;
import com.parsroyal.storemanagement.data.dao.CityDao;
import com.parsroyal.storemanagement.data.dao.impl.CityDaoImpl;
import com.parsroyal.storemanagement.data.entity.City;
import com.parsroyal.storemanagement.data.event.DataTransferErrorEvent;
import com.parsroyal.storemanagement.data.event.DataTransferSuccessEvent;
import com.parsroyal.storemanagement.data.model.CityList;
import com.parsroyal.storemanagement.service.GetDataRestService;
import com.parsroyal.storemanagement.service.ServiceGenerator;
import com.parsroyal.storemanagement.util.CharacterFixUtil;
import com.parsroyal.storemanagement.util.Empty;
import com.parsroyal.storemanagement.util.NetworkUtil;
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
      public void onFailure(Call<CityList> call, Throwable t) {
        EventBus.getDefault().post(new DataTransferErrorEvent(StatusCodes.NETWORK_ERROR));
      }
    });
  }
}
