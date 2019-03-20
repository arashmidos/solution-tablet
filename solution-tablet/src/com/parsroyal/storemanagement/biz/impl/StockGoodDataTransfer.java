package com.parsroyal.storemanagement.biz.impl;

import android.content.Context;
import com.parsroyal.storemanagement.R;
import com.parsroyal.storemanagement.constants.SendStatus;
import com.parsroyal.storemanagement.constants.StatusCodes;
import com.parsroyal.storemanagement.data.dao.impl.StockGoodDaoImpl;
import com.parsroyal.storemanagement.data.event.DataTransferErrorEvent;
import com.parsroyal.storemanagement.data.event.DataTransferSuccessEvent;
import com.parsroyal.storemanagement.data.model.StockGood;
import com.parsroyal.storemanagement.data.model.StockGoodUpdateRequest;
import com.parsroyal.storemanagement.service.ServiceGenerator;
import com.parsroyal.storemanagement.service.StoreRestService;
import com.parsroyal.storemanagement.util.NetworkUtil;
import java.io.IOException;
import java.util.Locale;
import org.greenrobot.eventbus.EventBus;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by Arash on 29/12/2017
 */
public class StockGoodDataTransfer {

  private final Context context;
  private final StockGoodDaoImpl stockService;
  private int success = 0;
  private StockGood data;
  private int total = 0;

  public StockGoodDataTransfer(Context context) {
    this.context = context;
    this.stockService = new StockGoodDaoImpl(context);
  }

  public int getTotal() {
    return total;
  }

  public void setTotal(int total) {
    this.total = total;
  }

  public void exchangeData() {
    if (!NetworkUtil.isNetworkAvailable(context)) {
      EventBus.getDefault().post(new DataTransferErrorEvent(StatusCodes.NO_NETWORK));
      return;
    }

    StoreRestService restService = ServiceGenerator.createService(StoreRestService.class);
    Call<Long> call = restService
        .updateStockGoods(new StockGoodUpdateRequest(data.getCounted(), data.getGls()));

    try {

      Response<Long> response = call.execute();
      if (response.isSuccessful()) {
        Long serverResponse = response.body();

        if (serverResponse != null && serverResponse == 1L) {
          data.setStatus(SendStatus.SENT.getId());
          stockService.update(data);
          success++;
          sendUpdate();
        }
      } else {
        sendUpdate();
      }
    } catch (IOException e) {
      e.printStackTrace();
      sendUpdate();
    }
  }

  private void sendUpdate() {
    EventBus.getDefault().post(new DataTransferSuccessEvent(String
        .format(Locale.US, context.getString(R.string.data_transfered_result),
            String.valueOf(getSuccess()),
            String.valueOf((total - success))), StatusCodes.UPDATE));
  }

  public void setData(StockGood data) {
    this.data = data;
    total++;
  }

  public int getSuccess() {
    return success;
  }
}
