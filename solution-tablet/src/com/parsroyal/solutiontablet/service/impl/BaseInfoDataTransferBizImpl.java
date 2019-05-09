package com.parsroyal.solutiontablet.service.impl;

import android.content.Context;
import android.util.Log;
import com.parsroyal.solutiontablet.constants.StatusCodes;
import com.parsroyal.solutiontablet.data.dao.BaseInfoDao;
import com.parsroyal.solutiontablet.data.dao.impl.BaseInfoDaoImpl;
import com.parsroyal.solutiontablet.data.entity.BaseInfo;
import com.parsroyal.solutiontablet.data.event.DataTransferErrorEvent;
import com.parsroyal.solutiontablet.data.event.DataTransferSuccessEvent;
import com.parsroyal.solutiontablet.data.model.BaseInfoList;
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
public class BaseInfoDataTransferBizImpl {

  private BaseInfoDao baseInfoDao;
  private Context context;

  public BaseInfoDataTransferBizImpl(Context context) {
    this.context = context;
    this.baseInfoDao = new BaseInfoDaoImpl(context);
  }

  public void exchangeData() {
    if (!NetworkUtil.isNetworkAvailable(context)) {
      EventBus.getDefault().post(new DataTransferErrorEvent(StatusCodes.NO_NETWORK));
    }

    GetDataRestService restService = ServiceGenerator.createService(GetDataRestService.class);

    Call<BaseInfoList> call = restService.getAllBaseInfo();

    call.enqueue(new Callback<BaseInfoList>() {
      @Override
      public void onResponse(Call<BaseInfoList> call, Response<BaseInfoList> response) {
        if (response.isSuccessful()) {
          BaseInfoList baseInfoList = response.body();
          if (Empty.isNotEmpty(baseInfoList)) {
            baseInfoDao.deleteAll();
            for (BaseInfo baseInfo : baseInfoList.getBaseInfoList()) {
              baseInfo.setTitle(CharacterFixUtil.fixString(baseInfo.getTitle()));
            }
            baseInfoDao.bulkInsert(baseInfoList.getBaseInfoList());

            EventBus.getDefault().post(new DataTransferSuccessEvent("", StatusCodes.SUCCESS));
          } else {
            EventBus.getDefault().post(new DataTransferSuccessEvent(StatusCodes.NO_DATA_ERROR));
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
      public void onFailure(Call<BaseInfoList> call, Throwable t) {
        EventBus.getDefault().post(new DataTransferErrorEvent(StatusCodes.NETWORK_ERROR));
      }
    });
  }
}
