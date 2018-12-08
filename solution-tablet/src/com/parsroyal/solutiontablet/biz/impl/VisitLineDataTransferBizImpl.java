package com.parsroyal.solutiontablet.biz.impl;

import android.content.Context;
import android.text.format.DateUtils;
import android.util.Log;
import com.parsroyal.solutiontablet.R;
import com.parsroyal.solutiontablet.constants.StatusCodes;
import com.parsroyal.solutiontablet.data.dao.CustomerDao;
import com.parsroyal.solutiontablet.data.dao.VisitLineDao;
import com.parsroyal.solutiontablet.data.dao.VisitLineDateDao;
import com.parsroyal.solutiontablet.data.dao.impl.CustomerDaoImpl;
import com.parsroyal.solutiontablet.data.dao.impl.VisitLineDaoImpl;
import com.parsroyal.solutiontablet.data.dao.impl.VisitLineDateDaoImpl;
import com.parsroyal.solutiontablet.data.entity.Customer;
import com.parsroyal.solutiontablet.data.entity.VisitLine;
import com.parsroyal.solutiontablet.data.entity.VisitLineDate;
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
import java.util.ArrayList;
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
    this.settingService = new SettingServiceImpl(context);
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
          visitLineDao
              .create(createVisitLineEntity(context.getString(R.string.manual_visit_line), 0L));
          customerDao.bulkInsert(getSampleCustomerList());
          if (Empty.isNotEmpty(list)) {

            //add manual visit line

            for (VisitLineDto visitLineDto : list) {
              visitLineDto.setTitle(CharacterFixUtil.fixString(visitLineDto.getTitle()));
              VisitLine visitLine = createVisitLineEntity(visitLineDto);
              visitLineDao.create(visitLine);
              customerDao.bulkInsert(visitLineDto.getCustomerList());

              List<VisitLineDate> dates = visitLineDto.getDates();
              addGregorianDate(dates);
              visitLineDateDao.bulkInsert(dates);
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
      date.setBackendDateGregorian(DateUtil.convertShamsiToGregorianDate(date.getBackendDate(),DateUtil.GLOBAL_FORMATTER2));
    }
  }

  private List<Customer> getSampleCustomerList() {
    Customer customer = new Customer();
    customer.setBackendId(0L);
    customer.setVisitLineBackendId(0L);
    List<Customer> customers = new ArrayList<>();
    customers.add(customer);
    return customers;
  }

  private VisitLine createVisitLineEntity(VisitLineDto visitLineDto) {
    VisitLine visitLine = new VisitLine();
    visitLine.setBackendId(visitLineDto.getBackendId());
    visitLine.setCode(visitLineDto.getCode());
    visitLine.setTitle(visitLineDto.getTitle());
    return visitLine;
  }

  private VisitLine createVisitLineEntity(String title, long id) {
    VisitLine visitLine = new VisitLine();
    visitLine.setBackendId(id);
    visitLine.setCode((int) id);
    visitLine.setTitle(title);
    return visitLine;
  }
}
