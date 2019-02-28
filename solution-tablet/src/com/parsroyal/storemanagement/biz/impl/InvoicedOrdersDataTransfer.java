package com.parsroyal.storemanagement.biz.impl;

import android.content.Context;
import android.util.Log;
import com.parsroyal.storemanagement.R;
import com.parsroyal.storemanagement.biz.AbstractDataTransferBizImpl;
import com.parsroyal.storemanagement.constants.SaleOrderStatus;
import com.parsroyal.storemanagement.constants.StatusCodes;
import com.parsroyal.storemanagement.constants.VisitInformationDetailType;
import com.parsroyal.storemanagement.data.dao.SaleOrderDao;
import com.parsroyal.storemanagement.data.dao.SaleOrderItemDao;
import com.parsroyal.storemanagement.data.dao.impl.SaleOrderDaoImpl;
import com.parsroyal.storemanagement.data.dao.impl.SaleOrderItemDaoImpl;
import com.parsroyal.storemanagement.data.entity.SaleOrder;
import com.parsroyal.storemanagement.data.event.DataTransferSuccessEvent;
import com.parsroyal.storemanagement.data.event.ErrorEvent;
import com.parsroyal.storemanagement.data.event.SendOrderEvent;
import com.parsroyal.storemanagement.data.model.BaseSaleDocument;
import com.parsroyal.storemanagement.data.model.SaleInvoiceDocument;
import com.parsroyal.storemanagement.service.RestService;
import com.parsroyal.storemanagement.service.ServiceGenerator;
import com.parsroyal.storemanagement.service.VisitService;
import com.parsroyal.storemanagement.service.impl.VisitServiceImpl;
import com.parsroyal.storemanagement.ui.observer.ResultObserver;
import com.parsroyal.storemanagement.util.DateUtil;
import com.parsroyal.storemanagement.util.Empty;
import com.parsroyal.storemanagement.util.NetworkUtil;
import com.parsroyal.storemanagement.util.NumberUtil;
import com.parsroyal.storemanagement.util.constants.ApplicationKeys;
import java.io.IOException;
import java.util.Locale;
import org.greenrobot.eventbus.EventBus;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Arash on 29/12/2017
 */
public class InvoicedOrdersDataTransfer extends AbstractDataTransferBizImpl<String> {

  protected ResultObserver resultObserver;
  protected SaleOrderDao saleOrderDao;
  protected SaleOrderItemDao saleOrderItemDao;
  protected BaseSaleDocument order;
  protected VisitService visitService;
  protected int success = 0;
  protected int total = 0;

  public InvoicedOrdersDataTransfer(Context context) {
    super(context);
    this.saleOrderDao = new SaleOrderDaoImpl(context);
    this.saleOrderItemDao = new SaleOrderItemDaoImpl(context);
    visitService = new VisitServiceImpl(context);
    saleType = keyValueDao.retrieveByKey(ApplicationKeys.SETTING_SALE_TYPE);

  }

  public int getSuccess() {
    return success;
  }

  public BaseSaleDocument getOrder() {
    return order;
  }

  public void setOrder(BaseSaleDocument order) {
    this.order = order;
    this.total++;
  }

  @Override
  public void receiveData(String response) {
    if (Empty.isNotEmpty(response)) {
      SaleOrder saleOrder = saleOrderDao.retrieve(order.getId());

      if (Empty.isNotEmpty(saleOrder)) {
        updateOrderStatus(Long.valueOf(response), saleOrder);
        success++;
      }
    } else {
      EventBus.getDefault().post(new ErrorEvent(StatusCodes.DATA_STORE_ERROR));
    }
    EventBus.getDefault()
        .post(new DataTransferSuccessEvent(getSuccessfulMessage(), StatusCodes.UPDATE));
  }

  protected String getExceptionMessage() {
    return context.getString(R.string.message_exception_in_sending_invoices);
  }

  public String getSuccessfulMessage() {
    return NumberUtil.digitsToPersian(String
        .format(Locale.getDefault(), context.getString(R.string.data_transfered_result),
            String.valueOf(success),
            String.valueOf(total - success)));
  }

  protected void updateOrderStatus(Long invoiceBackendId, SaleOrder saleOrder) {
    saleOrder.setInvoiceBackendId(invoiceBackendId);
    if (ApplicationKeys.SALE_DISTRIBUTER.equals(saleType.getValue())) {
      if (order.getStatusCode() == null || !order.getStatusCode()
          .equals(SaleOrderStatus.GIFT.getId())) {
        saleOrder.setStatus(SaleOrderStatus.DELIVERABLE_SENT.getId());
      }
    } else {
      saleOrder.setStatus(SaleOrderStatus.SENT_INVOICE.getId());
    }
    saleOrder.setUpdateDateTime(DateUtil.getCurrentGregorianFullWithTimeDate());
    saleOrderDao.update(saleOrder);
    visitService.updateVisitDetailId(getVisitDetailType(), saleOrder.getId(), invoiceBackendId);
  }

  public void sendSingleInvoice(BaseSaleDocument baseSaleDocument) {
    if (!NetworkUtil.isNetworkAvailable(context)) {
      EventBus.getDefault().post(new ErrorEvent(StatusCodes.NO_NETWORK));
    }

    this.order = baseSaleDocument;

    RestService restService = ServiceGenerator.createService(RestService.class);
//:LAter change for hot sale
    Call<String> call = restService.sendInvoice(3L, (SaleInvoiceDocument) baseSaleDocument);

    call.enqueue(new Callback<String>() {
      @Override
      public void onResponse(Call<String> call, Response<String> response) {
        if (response.isSuccessful()) {
          String OrderBackendId = response.body();
          if (OrderBackendId != null) {
            receiveData(OrderBackendId);
            if (getSuccess() == 1) {
              EventBus.getDefault().post(new SendOrderEvent(StatusCodes.SUCCESS,
                  Long.valueOf(OrderBackendId), getSuccessfulMessage()));
            } else {
              EventBus.getDefault().post(new SendOrderEvent(
                  StatusCodes.SERVER_ERROR, order.getId(), getExceptionMessage()));
            }
          } else {
            EventBus.getDefault().post(new ErrorEvent(StatusCodes.INVALID_DATA));
          }
        } else {
          try {
            Log.d("TAG", response.errorBody().string());
          } catch (IOException e) {
            e.printStackTrace();
          }
          EventBus.getDefault().post(
              new SendOrderEvent(StatusCodes.SERVER_ERROR, order.getId(), getExceptionMessage()));
        }
      }

      @Override
      public void onFailure(Call<String> call, Throwable t) {
        EventBus.getDefault().post(new ErrorEvent(StatusCodes.NETWORK_ERROR));
      }
    });
  }

  @Override
  public void beforeTransfer() {
  }

  @Override
  public ResultObserver getObserver() {
    return resultObserver;
  }

  @Override
  public String getMethod() {
    return "saleinvoices";
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
    return new HttpEntity<>(order, headers);
  }

  protected VisitInformationDetailType getVisitDetailType() {
    if (saleType.getValue().equals(ApplicationKeys.SALE_DISTRIBUTER)) {
      return VisitInformationDetailType.DELIVER_ORDER;
    }
    return VisitInformationDetailType.CREATE_INVOICE;
  }
}
