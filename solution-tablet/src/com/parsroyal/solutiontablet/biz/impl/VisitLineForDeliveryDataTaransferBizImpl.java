package com.parsroyal.solutiontablet.biz.impl;

import android.content.Context;
import android.util.Log;
import com.parsroyal.solutiontablet.constants.StatusCodes;
import com.parsroyal.solutiontablet.data.dao.CustomerDao;
import com.parsroyal.solutiontablet.data.dao.VisitLineDao;
import com.parsroyal.solutiontablet.data.dao.impl.CustomerDaoImpl;
import com.parsroyal.solutiontablet.data.dao.impl.VisitLineDaoImpl;
import com.parsroyal.solutiontablet.data.entity.Customer;
import com.parsroyal.solutiontablet.data.entity.VisitLine;
import com.parsroyal.solutiontablet.data.event.DataTransferErrorEvent;
import com.parsroyal.solutiontablet.data.event.DataTransferSuccessEvent;
import com.parsroyal.solutiontablet.data.model.VisitLineDto;
import com.parsroyal.solutiontablet.service.GetDataRestService;
import com.parsroyal.solutiontablet.service.ServiceGenerator;
import com.parsroyal.solutiontablet.service.SettingService;
import com.parsroyal.solutiontablet.service.impl.SettingServiceImpl;
import com.parsroyal.solutiontablet.util.CharacterFixUtil;
import com.parsroyal.solutiontablet.util.DateUtil;
import com.parsroyal.solutiontablet.util.Empty;
import com.parsroyal.solutiontablet.util.NetworkUtil;
import com.parsroyal.solutiontablet.util.constants.ApplicationKeys;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import org.greenrobot.eventbus.EventBus;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Arash on 7/5/2015.
 */
public class VisitLineForDeliveryDataTaransferBizImpl {

  private Context context;
  private VisitLineDao visitLineDao;
  private CustomerDao customerDao;
  private SettingService settingService;

  public VisitLineForDeliveryDataTaransferBizImpl(Context context) {
    this.context = context;
    this.visitLineDao = new VisitLineDaoImpl(context);
    this.customerDao = new CustomerDaoImpl(context);
    this.settingService = new SettingServiceImpl(context);
  }

  public void exchangeData() {
    if (!NetworkUtil.isNetworkAvailable(context)) {
      EventBus.getDefault().post(new DataTransferErrorEvent(StatusCodes.NO_NETWORK));
    }

    GetDataRestService restService = ServiceGenerator.createService(GetDataRestService.class);

    Call<List<VisitLineDto>> call = restService.getAllVisitLinesForDelivery(
        settingService.getSettingValue(ApplicationKeys.SALESMAN_ID));

    call.enqueue(new Callback<List<VisitLineDto>>() {
      @Override
      public void onResponse(Call<List<VisitLineDto>> call, Response<List<VisitLineDto>> response) {
        if (response.isSuccessful()) {
          List<VisitLineDto> list = response.body();
          if (Empty.isNotEmpty(list)) {
            for (VisitLineDto visitLineDto : list) {
              visitLineDto.setTitle(CharacterFixUtil.fixString(visitLineDto.getTitle()));
              VisitLine visitLine = createVisitLineEntity(visitLineDto);
              VisitLine oldVisitLine = visitLineDao
                  .getVisitLineByBackendId(visitLine.getBackendId());
              if (Empty.isEmpty(oldVisitLine)) {
                visitLineDao.create(visitLine);
                customerDao.bulkInsert(visitLineDto.getCustomerList());
              } else {
                List<Customer> customers = customerDao
                    .getCustomersVisitLineBackendId(visitLine.getBackendId());
                for (Customer customer : visitLineDto.getCustomerList()) {
                  if (!customers.contains(customer)) {
                    customer.setCreateDateTime(DateUtil.convertDate(
                        new Date(), DateUtil.FULL_FORMATTER_GREGORIAN_WITH_TIME, "EN"));
                    customer.setUpdateDateTime(DateUtil.convertDate(
                        new Date(), DateUtil.FULL_FORMATTER_GREGORIAN_WITH_TIME, "EN"));
                    customer.setApproved(true);
                    customerDao.create(customer);
                  }
                }
              }
            }
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
      public void onFailure(Call<List<VisitLineDto>> call, Throwable t) {
        EventBus.getDefault().post(new DataTransferErrorEvent(StatusCodes.NETWORK_ERROR));
      }
    });
  }

  private VisitLine createVisitLineEntity(VisitLineDto visitLineDto) {
    VisitLine visitLine = new VisitLine();
    visitLine.setBackendId(visitLineDto.getBackendId());
    visitLine.setCode(visitLineDto.getCode());
    visitLine.setTitle(visitLineDto.getTitle());
    return visitLine;
  }
}
