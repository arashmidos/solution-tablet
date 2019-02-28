package com.parsroyal.storemanagement.biz.impl;

import android.content.Context;
import android.util.Log;
import com.parsroyal.storemanagement.R;
import com.parsroyal.storemanagement.constants.CustomerStatus;
import com.parsroyal.storemanagement.constants.StatusCodes;
import com.parsroyal.storemanagement.data.dao.CustomerDao;
import com.parsroyal.storemanagement.data.dao.VisitLineDao;
import com.parsroyal.storemanagement.data.dao.VisitLineDateDao;
import com.parsroyal.storemanagement.data.dao.impl.CustomerDaoImpl;
import com.parsroyal.storemanagement.data.dao.impl.VisitLineDaoImpl;
import com.parsroyal.storemanagement.data.dao.impl.VisitLineDateDaoImpl;
import com.parsroyal.storemanagement.data.entity.Customer;
import com.parsroyal.storemanagement.data.entity.VisitLine;
import com.parsroyal.storemanagement.data.entity.VisitLineDate;
import com.parsroyal.storemanagement.data.event.DataTransferErrorEvent;
import com.parsroyal.storemanagement.data.event.DataTransferSuccessEvent;
import com.parsroyal.storemanagement.data.model.VisitLineDto;
import com.parsroyal.storemanagement.service.GetDataRestService;
import com.parsroyal.storemanagement.service.ServiceGenerator;
import com.parsroyal.storemanagement.service.SettingService;
import com.parsroyal.storemanagement.service.impl.SettingServiceImpl;
import com.parsroyal.storemanagement.util.CharacterFixUtil;
import com.parsroyal.storemanagement.util.DateUtil;
import com.parsroyal.storemanagement.util.Empty;
import com.parsroyal.storemanagement.util.NetworkUtil;
import com.parsroyal.storemanagement.util.constants.ApplicationKeys;
import java.io.IOException;
import java.util.List;
import org.greenrobot.eventbus.EventBus;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Arash on 28/12/2017
 */
public class VisitLineDataTransferBizImpl {

  private final VisitLineDateDao visitLineDateDao;
  private Context context;
  private VisitLineDao visitLineDao;
  private CustomerDao customerDao;
  private SettingService settingService;

  public VisitLineDataTransferBizImpl(Context context) {
    this.context = context;
    this.visitLineDao = new VisitLineDaoImpl(context);
    this.visitLineDateDao = new VisitLineDateDaoImpl(context);
    this.customerDao = new CustomerDaoImpl(context);
    this.settingService = new SettingServiceImpl();
  }

  public void exchangeData() {
    if (!NetworkUtil.isNetworkAvailable(context)) {
      EventBus.getDefault().post(new DataTransferErrorEvent(StatusCodes.NO_NETWORK));
      return;
    }

    GetDataRestService restService = ServiceGenerator.createService(GetDataRestService.class);
    String check = settingService.getSettingValue(ApplicationKeys.SETTING_CHECK_CREDIT_ENABLE);
    Call<List<VisitLineDto>> call = restService
        .getAllVisitLines(settingService.getSettingValue(ApplicationKeys.SETTING_SALE_TYPE),
            settingService.getSettingValue(ApplicationKeys.SALESMAN_ID),
            "true".equals(check) ? "1" : "0");

    call.enqueue(new Callback<List<VisitLineDto>>() {
      @Override
      public void onResponse(Call<List<VisitLineDto>> call, Response<List<VisitLineDto>> response) {
        if (response.isSuccessful()) {
          List<VisitLineDto> list = response.body();
          visitLineDao.deleteAll();
          visitLineDateDao.deleteAll();
          customerDao.deleteAllCustomersRelatedToVisitLines();
          visitLineDao.create(createVisitLineEntity(context.getString(R.string.manual_visit_line)));
//          customerDao.create(getSampleCustomer());
          if (Empty.isNotEmpty(list)) {

            //add manual visit line

            for (VisitLineDto visitLineDto : list) {
              visitLineDto.setTitle(CharacterFixUtil.fixString(visitLineDto.getTitle()));
              VisitLine visitLine = createVisitLineEntity(visitLineDto);
              visitLineDao.create(visitLine);
              customerDao.bulkInsert(visitLineDto.getCustomerList());

              List<VisitLineDate> dates = visitLineDto.getDates();
              if (Empty.isNotEmpty(dates)) {
                addGregorianDate(dates);
                visitLineDateDao.bulkInsert(dates);
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

  private void addGregorianDate(List<VisitLineDate> dates) {
    for (VisitLineDate date : dates) {
      date.setBackendDateGregorian(
          DateUtil.convertShamsiToGregorianDate(date.getBackendDate(), DateUtil.GLOBAL_FORMATTER2));
    }
  }

  private Customer getSampleCustomer() {
    Customer customer = new Customer();
    customer.setBackendId(0L);
    customer.setVisitLineBackendId(0L);
    customer.setStatus(CustomerStatus.SYSTEM.getId());
    customer.setFullName("");
    customer.setAddress("");

    return customer;
  }

  private VisitLine createVisitLineEntity(VisitLineDto visitLineDto) {
    VisitLine visitLine = new VisitLine();
    visitLine.setBackendId(visitLineDto.getBackendId());
    visitLine.setCode(visitLineDto.getCode());
    visitLine.setTitle(visitLineDto.getTitle());
    return visitLine;
  }

  private VisitLine createVisitLineEntity(String title) {
    VisitLine visitLine = new VisitLine();
    visitLine.setBackendId(0L);
    visitLine.setCode(0);
    visitLine.setTitle(title);
    return visitLine;
  }
}
