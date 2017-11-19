package com.parsroyal.solutiontablet.biz.impl;

import android.content.Context;
import android.util.Log;
import com.parsroyal.solutiontablet.R;
import com.parsroyal.solutiontablet.biz.AbstractDataTransferBizImpl;
import com.parsroyal.solutiontablet.data.dao.CustomerDao;
import com.parsroyal.solutiontablet.data.dao.QAnswerDao;
import com.parsroyal.solutiontablet.data.dao.VisitInformationDao;
import com.parsroyal.solutiontablet.data.dao.impl.CustomerDaoImpl;
import com.parsroyal.solutiontablet.data.dao.impl.QAnswerDaoImpl;
import com.parsroyal.solutiontablet.data.dao.impl.VisitInformationDaoImpl;
import com.parsroyal.solutiontablet.data.entity.Customer;
import com.parsroyal.solutiontablet.data.entity.VisitInformation;
import com.parsroyal.solutiontablet.data.model.CustomerDto;
import com.parsroyal.solutiontablet.data.response.CustomerResponse;
import com.parsroyal.solutiontablet.service.CustomerService;
import com.parsroyal.solutiontablet.service.impl.CustomerServiceImpl;
import com.parsroyal.solutiontablet.ui.observer.ResultObserver;
import com.parsroyal.solutiontablet.util.DateUtil;
import com.parsroyal.solutiontablet.util.Empty;
import com.parsroyal.solutiontablet.util.Logger;
import java.util.Date;
import java.util.Locale;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;

/**
 * Created by Arash on 1119/2017.
 */
public class NewCustomerDataTransferBizImpl extends AbstractDataTransferBizImpl<CustomerResponse> {

  public static final String TAG = NewCustomerDataTransferBizImpl.class.getSimpleName();

  private Context context;
  private CustomerDao customerDao;
  private CustomerService customerService;
  private VisitInformationDao visitInformationDao;
  private QAnswerDao qAnswerDao;
  private ResultObserver observer;
  private CustomerDto customer;
  private int success = 0;
  private int total = 0;

  public NewCustomerDataTransferBizImpl(Context context, ResultObserver resultObserver) {
    super(context);
    this.context = context;
    this.customerDao = new CustomerDaoImpl(context);
    this.visitInformationDao = new VisitInformationDaoImpl(context);
    this.qAnswerDao = new QAnswerDaoImpl(context);
    this.customerService = new CustomerServiceImpl(context);
    this.observer = resultObserver;
  }

  @Override
  public void receiveData(CustomerResponse response) {
    if (Empty.isNotEmpty(response)) {
      try {
        CustomerDto persistedCustomer = response.getCustomer();
        Long customerId = customer.getId();
        Customer tempCustomer = customerDao.retrieve(customerId);
        if (Empty.isNotEmpty(tempCustomer)) {
          Long backendId = persistedCustomer.getId();
          tempCustomer.setBackendId(backendId);
          tempCustomer.setCode(persistedCustomer.getCode());
          tempCustomer.setUpdateDateTime(DateUtil
              .convertDate(new Date(), DateUtil.FULL_FORMATTER_GREGORIAN_WITH_TIME, "EN"));
          customerDao.update(tempCustomer);

//           Update Visits
          VisitInformation visitList = visitInformationDao.retrieveForNewCustomer(customerId);

          if (visitList != null) {
            visitList.setCustomerBackendId(backendId);
            visitInformationDao.update(visitList);
          }

          //update answers
          qAnswerDao.updateCustomerBackendIdForAnswers(customerId, backendId);
        }
//        }
        success++;
        getObserver()
            .publishResult(context.getString(R.string.new_customers_data_transferred_successfully));
      } catch (Exception ex) {
        Logger.sendError("Data transfer", "Error in receiving NewCustomerData " + ex.getMessage());
        Log.e(TAG, ex.getMessage(), ex);
      }
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
    return "customers";
  }

  @Override
  public Class getType() {
    return CustomerResponse.class;
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

    return new HttpEntity<>(customer, headers);
  }

  public void setCustomer(CustomerDto customer) {
    this.customer = customer;
    total++;
  }

  public String getSuccessfulMessage() {
    return String
        .format(Locale.US, context.getString(R.string.data_transfered_result),
            String.valueOf(success),
            String.valueOf(total - success));
  }
}
