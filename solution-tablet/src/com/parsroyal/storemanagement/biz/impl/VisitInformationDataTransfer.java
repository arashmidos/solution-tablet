package com.parsroyal.storemanagement.biz.impl;

import android.content.Context;
import com.parsroyal.storemanagement.R;
import com.parsroyal.storemanagement.constants.StatusCodes;
import com.parsroyal.storemanagement.data.dao.VisitInformationDao;
import com.parsroyal.storemanagement.data.dao.impl.VisitInformationDaoImpl;
import com.parsroyal.storemanagement.data.entity.VisitInformation;
import com.parsroyal.storemanagement.data.event.DataTransferErrorEvent;
import com.parsroyal.storemanagement.data.event.DataTransferSuccessEvent;
import com.parsroyal.storemanagement.data.model.VisitInformationDto;
import com.parsroyal.storemanagement.service.RestService;
import com.parsroyal.storemanagement.service.ServiceGenerator;
import com.parsroyal.storemanagement.util.DateUtil;
import com.parsroyal.storemanagement.util.Empty;
import com.parsroyal.storemanagement.util.NetworkUtil;
import java.io.IOException;
import java.util.Locale;
import org.greenrobot.eventbus.EventBus;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by Arash on 29/12/2017
 */
public class VisitInformationDataTransfer {

  private final Context context;
  private int success = 0;
  private VisitInformationDao visitInformationDao;
  private VisitInformationDto data;
  private int total = 0;

  public VisitInformationDataTransfer(Context context) {
    this.context = context;
    this.visitInformationDao = new VisitInformationDaoImpl(context);
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

    RestService restService = ServiceGenerator.createService(RestService.class);
    Call<VisitInformationDto> call = restService.sendVisits(data);

    try {

      Response<VisitInformationDto> response = call.execute();
      if (response.isSuccessful()) {
        VisitInformationDto visitResponse = response.body();

        if (Empty.isNotEmpty(visitResponse)) {

          VisitInformation visitInformation = visitInformationDao.retrieve(data.getId());

          if (Empty.isNotEmpty(visitInformation)) {
            visitInformation.setVisitBackendId(visitResponse.getId());
            visitInformation.setUpdateDateTime(DateUtil.getCurrentGregorianFullWithTimeDate());
            visitInformationDao.update(visitInformation);
            success++;
            sendUpdate();
          }
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

  public void setData(VisitInformationDto data) {
    this.data = data;
    total++;
  }

  public int getSuccess() {
    return success;
  }
}
