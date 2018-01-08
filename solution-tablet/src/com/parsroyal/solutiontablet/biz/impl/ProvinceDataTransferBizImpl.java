package com.parsroyal.solutiontablet.biz.impl;

import android.content.Context;
import android.util.Log;
import com.parsroyal.solutiontablet.R;
import com.parsroyal.solutiontablet.constants.StatusCodes;
import com.parsroyal.solutiontablet.data.dao.ProvinceDao;
import com.parsroyal.solutiontablet.data.dao.impl.ProvinceDaoImpl;
import com.parsroyal.solutiontablet.data.entity.Province;
import com.parsroyal.solutiontablet.data.event.DataTransferErrorEvent;
import com.parsroyal.solutiontablet.data.event.DataTransferSuccessEvent;
import com.parsroyal.solutiontablet.data.event.ErrorEvent;
import com.parsroyal.solutiontablet.data.model.ProvinceList;
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
 * Created by Arash on 12/23/2017.
 */
public class ProvinceDataTransferBizImpl {

  private Context context;
  private ProvinceDao provinceDao;

  public ProvinceDataTransferBizImpl(Context context) {
    this.context = context;
    this.provinceDao = new ProvinceDaoImpl(context);
  }

  public void getAllProvinces() {
    if (!NetworkUtil.isNetworkAvailable(context)) {
      EventBus.getDefault().post(new DataTransferErrorEvent(StatusCodes.NO_NETWORK));
    }

    GetDataRestService restService = ServiceGenerator.createService(GetDataRestService.class);

    Call<ProvinceList> call = restService.getAllProvinces();

    call.enqueue(new Callback<ProvinceList>() {
      @Override
      public void onResponse(Call<ProvinceList> call, Response<ProvinceList> response) {
        if (response.isSuccessful()) {
          ProvinceList provinceList = response.body();
          if (Empty.isNotEmpty(provinceList)) {
            provinceDao.deleteAll();
            for (Province province : provinceList.getProvinceList()) {
              province.setTitle(CharacterFixUtil.fixString(province.getTitle()));
            }
            provinceDao.bulkInsert(provinceList.getProvinceList());
            EventBus.getDefault().post(
                new DataTransferSuccessEvent(
                    context.getString(R.string.provinces_data_transferred_successfully)
                    , StatusCodes.SUCCESS));
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
      public void onFailure(Call<ProvinceList> call, Throwable t) {
        EventBus.getDefault().post(new DataTransferErrorEvent(StatusCodes.NETWORK_ERROR));
      }
    });
  }
}
