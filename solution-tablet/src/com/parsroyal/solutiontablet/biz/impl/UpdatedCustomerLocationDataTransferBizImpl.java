package com.parsroyal.solutiontablet.biz.impl;

import android.content.Context;
import com.parsroyal.solutiontablet.R;
import com.parsroyal.solutiontablet.biz.AbstractDataTransferBizImpl;
import com.parsroyal.solutiontablet.constants.CustomerStatus;
import com.parsroyal.solutiontablet.constants.StatusCodes;
import com.parsroyal.solutiontablet.data.dao.CustomerDao;
import com.parsroyal.solutiontablet.data.dao.impl.CustomerDaoImpl;
import com.parsroyal.solutiontablet.data.entity.Customer;
import com.parsroyal.solutiontablet.data.event.DataTransferErrorEvent;
import com.parsroyal.solutiontablet.data.event.DataTransferSuccessEvent;
import com.parsroyal.solutiontablet.data.model.CustomerLocationDto;
import com.parsroyal.solutiontablet.service.CustomerService;
import com.parsroyal.solutiontablet.service.impl.CustomerServiceImpl;
import com.parsroyal.solutiontablet.service.impl.SettingServiceImpl;
import com.parsroyal.solutiontablet.ui.observer.ResultObserver;
import com.parsroyal.solutiontablet.util.DateUtil;
import com.parsroyal.solutiontablet.util.Empty;
import com.parsroyal.solutiontablet.util.Logger;
import com.parsroyal.solutiontablet.util.constants.ApplicationKeys;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import org.greenrobot.eventbus.EventBus;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;

/**
 * Created by Arash on 2016-08-10
 */
public class UpdatedCustomerLocationDataTransferBizImpl extends
    AbstractDataTransferBizImpl<String> {

  public static final String TAG = UpdatedCustomerLocationDataTransferBizImpl.class.getSimpleName();

  private Context context;
  private CustomerDao customerDao;
  private CustomerService customerService;
  private ResultObserver observer;
  private CustomerLocationDto data;

  public UpdatedCustomerLocationDataTransferBizImpl(Context context) {
    super(context);
    this.context = context;
    this.customerDao = new CustomerDaoImpl(context);
    this.customerService = new CustomerServiceImpl(context);
  }

  @Override
  public void receiveData(String response) {
    if (Empty.isNotEmpty(response)) {
      try {
        String[] rows = response.split("[$]");
        for (int i = 0; i < rows.length; i++) {
          String row = rows[i];
          String[] columns = row.split("[&]");
          long customerBackendId = Long.parseLong(columns[0]);
          long updated = Long.parseLong(columns[1]);

          Customer customer = customerDao.retrieveByBackendId(customerBackendId);
          if (Empty.isNotEmpty(customer) && updated == 1) {
            customer.setStatus(CustomerStatus.SENT.getId());
            customer.setUpdateDateTime(DateUtil
                .convertDate(new Date(), DateUtil.FULL_FORMATTER_GREGORIAN_WITH_TIME, "EN"));
            customerDao.update(customer);
          }
        }

        EventBus.getDefault().post(new DataTransferSuccessEvent(
            context.getString(R.string.updated_customers_data_transferred_successfully),
            StatusCodes.SUCCESS));
      } catch (Exception ex) {
        Logger.sendError("Data transfer",
            "Error in receiving UpdatedCustomerLocationData " + ex.getMessage());
        EventBus.getDefault().post(new DataTransferErrorEvent(StatusCodes.DATA_STORE_ERROR));
      }
    } else {
      EventBus.getDefault().post(new DataTransferErrorEvent(
          context.getString(R.string.message_got_no_response), StatusCodes.NETWORK_ERROR));
    }
  }

  @Override
  public void beforeTransfer() {
  }

  @Override
  public ResultObserver getObserver() {
    return observer;
  }

  @Override
  public String getMethod() {
    return String.format("customers/%s/updateLocation",
        new SettingServiceImpl(context).getSettingValue(ApplicationKeys.SALESMAN_ID));
  }

  @Override
  public Class getType() {
    return String.class;
  }

  @Override
  public HttpMethod getHttpMethod() {
    return HttpMethod.POST;
  }

  @Override
  protected MediaType getContentType() {
    return MediaType.APPLICATION_JSON;
  }

  @Override
  protected HttpEntity getHttpEntity(HttpHeaders headers) {
    if (Empty.isEmpty(data)) {
      List<CustomerLocationDto> customerLocationDtoList = customerService
          .getAllUpdatedCustomerLocation();
      return new HttpEntity<>(customerLocationDtoList, headers);
    } else {
      return new HttpEntity<>(Collections.singletonList(data), headers);
    }
  }

  public void setData(CustomerLocationDto data) {
    this.data = data;
  }
}
