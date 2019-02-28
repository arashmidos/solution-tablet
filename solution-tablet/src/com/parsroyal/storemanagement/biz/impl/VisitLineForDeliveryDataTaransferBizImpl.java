package com.parsroyal.storemanagement.biz.impl;

import android.content.Context;
import android.util.Log;
import com.parsroyal.storemanagement.R;
import com.parsroyal.storemanagement.constants.StatusCodes;
import com.parsroyal.storemanagement.data.dao.CustomerDao;
import com.parsroyal.storemanagement.data.dao.VisitLineDao;
import com.parsroyal.storemanagement.data.dao.impl.CustomerDaoImpl;
import com.parsroyal.storemanagement.data.dao.impl.VisitLineDaoImpl;
import com.parsroyal.storemanagement.data.entity.Customer;
import com.parsroyal.storemanagement.data.entity.VisitLine;
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
import java.util.ArrayList;
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
    this.settingService = new SettingServiceImpl();
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
          visitLineDao
              .create(createVisitLineEntity(context.getString(R.string.manual_visit_line), 0L));
//          customerDao.bulkInsert(getSampleCustomerList());
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

  private List<Customer> getSampleCustomerList() {
    Customer customer = new Customer();
    customer.setBackendId(0L);
    customer.setVisitLineBackendId(0L);
    List<Customer> customers = new ArrayList<>();
    customers.add(customer);
    return customers;
  }

  private VisitLine createVisitLineEntity(String title, long id) {
    VisitLine visitLine = new VisitLine();
    visitLine.setBackendId(id);
    visitLine.setCode((int) id);
    visitLine.setTitle(title);
    return visitLine;
  }
}
