package com.parsroyal.storemanagement.service.impl;


import com.parsroyal.storemanagement.SolutionTabletApplication;
import com.parsroyal.storemanagement.constants.StatusCodes;
import com.parsroyal.storemanagement.data.event.ErrorEvent;
import com.parsroyal.storemanagement.data.event.KpiEvent;
import com.parsroyal.storemanagement.data.model.KPIDetail;
import com.parsroyal.storemanagement.service.KPIService;
import com.parsroyal.storemanagement.service.ServiceGenerator;
import com.parsroyal.storemanagement.util.NetworkUtil;
import java.util.List;
import org.greenrobot.eventbus.EventBus;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by arash on 2/19/18.
 */

public class KPIServiceImpl {


  public void getCustomerReportsList() {
    getReport(0, 0);
  }

  public void getSalesmanReportsList() {
    getReport(100, 0);
  }

  public void getReport(int reportId, long serialId) {
    if (!NetworkUtil.isNetworkAvailable(SolutionTabletApplication.getInstance())) {
      EventBus.getDefault().post(new ErrorEvent(StatusCodes.NO_NETWORK));
      return;
    }
    KPIService restService = ServiceGenerator.createService(KPIService.class);

    Call<List<KPIDetail>> call = restService.getReport(reportId, serialId);

    call.enqueue(new Callback<List<KPIDetail>>() {
      @Override
      public void onResponse(Call<List<KPIDetail>> call, Response<List<KPIDetail>> response) {
        if (response.isSuccessful()) {
          List<KPIDetail> categoryList = response.body();
          if (categoryList != null && categoryList.size() > 0) {

            EventBus.getDefault().post(new KpiEvent(categoryList));
          } else {
            EventBus.getDefault().post(new ErrorEvent(StatusCodes.NO_DATA_ERROR));
          }
        } else {
          EventBus.getDefault().post(new ErrorEvent(StatusCodes.SERVER_ERROR));
        }
      }

      @Override
      public void onFailure(Call<List<KPIDetail>> call, Throwable t) {
        EventBus.getDefault().post(new ErrorEvent(StatusCodes.NETWORK_ERROR));
      }
    });
  }
}
