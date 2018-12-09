package com.parsroyal.solutiontablet.biz.impl;

import android.content.Context;
import android.util.Log;
import com.parsroyal.solutiontablet.constants.StatusCodes;
import com.parsroyal.solutiontablet.data.dao.GoodsDao;
import com.parsroyal.solutiontablet.data.dao.impl.GoodsDaoImpl;
import com.parsroyal.solutiontablet.data.entity.Goods;
import com.parsroyal.solutiontablet.data.event.DataTransferErrorEvent;
import com.parsroyal.solutiontablet.data.event.DataTransferSuccessEvent;
import com.parsroyal.solutiontablet.service.GetDataRestService;
import com.parsroyal.solutiontablet.service.ServiceGenerator;
import com.parsroyal.solutiontablet.service.SettingService;
import com.parsroyal.solutiontablet.service.impl.SettingServiceImpl;
import com.parsroyal.solutiontablet.util.Empty;
import com.parsroyal.solutiontablet.util.NetworkUtil;
import com.parsroyal.solutiontablet.util.constants.ApplicationKeys;
import java.io.IOException;
import java.util.List;
import org.greenrobot.eventbus.EventBus;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Arash on 28/12/2017
 */
public class GoodsDataTransferBizImpl {

  public static final String TAG = GoodsDataTransferBizImpl.class.getSimpleName();

  private Context context;
  private GoodsDao goodsDao;
  private SettingService settingService;

  public GoodsDataTransferBizImpl(Context context) {
    this.context = context;
    this.goodsDao = new GoodsDaoImpl(context);
    this.settingService = new SettingServiceImpl();
  }

  public void exchangeData() {
    if (!NetworkUtil.isNetworkAvailable(context)) {
      EventBus.getDefault().post(new DataTransferErrorEvent(StatusCodes.NO_NETWORK));
    }

    GetDataRestService restService = ServiceGenerator.createService(GetDataRestService.class);

    Call<List<Goods>> call = restService
        .getAllGoods(settingService.getSettingValue(ApplicationKeys.USER_COMPANY_ID),
            settingService.getSettingValue(ApplicationKeys.SETTING_STOCK_ID),
            settingService.getSettingValue(ApplicationKeys.SETTING_SALE_TYPE),
            settingService.getSettingValue(ApplicationKeys.SALESMAN_ID));

    call.enqueue(new Callback<List<Goods>>() {
      @Override
      public void onResponse(Call<List<Goods>> call, Response<List<Goods>> response) {
        if (response.isSuccessful()) {
          List<Goods> list = response.body();
          if (Empty.isNotEmpty(list)) {
            goodsDao.deleteAll();
            goodsDao.bulkInsert(list);
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
      public void onFailure(Call<List<Goods>> call, Throwable t) {
        EventBus.getDefault().post(new DataTransferErrorEvent(StatusCodes.NETWORK_ERROR));
      }
    });
  }
}
