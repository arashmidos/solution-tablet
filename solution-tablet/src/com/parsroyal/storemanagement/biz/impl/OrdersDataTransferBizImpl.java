package com.parsroyal.storemanagement.biz.impl;

import android.content.Context;
import com.parsroyal.storemanagement.R;
import com.parsroyal.storemanagement.constants.SaleOrderStatus;
import com.parsroyal.storemanagement.constants.StatusCodes;
import com.parsroyal.storemanagement.constants.VisitInformationDetailType;
import com.parsroyal.storemanagement.data.entity.SaleOrder;
import com.parsroyal.storemanagement.data.event.ErrorEvent;
import com.parsroyal.storemanagement.data.event.SendOrderEvent;
import com.parsroyal.storemanagement.data.model.BaseSaleDocument;
import com.parsroyal.storemanagement.data.model.SaleOrderDocument;
import com.parsroyal.storemanagement.service.RestService;
import com.parsroyal.storemanagement.service.ServiceGenerator;
import com.parsroyal.storemanagement.util.DateUtil;
import com.parsroyal.storemanagement.util.NetworkUtil;
import java.io.IOException;
import org.greenrobot.eventbus.EventBus;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

/**
 * Created by Arash on 29/12/2017
 */
public class OrdersDataTransferBizImpl extends InvoicedOrdersDataTransfer {

  private final boolean isComplimentary;

  public OrdersDataTransferBizImpl(Context context, boolean isComplimentary) {
    super(context);
    this.isComplimentary = isComplimentary;
  }

  @Override
  protected void updateOrderStatus(Long backendId, SaleOrder saleOrder) {
    saleOrder.setBackendId(backendId);
    saleOrder.setStatus(
        isComplimentary ? SaleOrderStatus.FREE_ORDER_SENT.getId() : SaleOrderStatus.SENT.getId());
    saleOrder.setUpdateDateTime(DateUtil.getCurrentGregorianFullWithTimeDate());
    saleOrderDao.update(saleOrder);
    visitService.updateVisitDetailId(getVisitDetailType(), saleOrder.getId(), backendId);
  }

  @Override
  protected String getExceptionMessage() {
    return context.getString(R.string.message_exception_in_sending_orders);
  }

  @Override
  public void beforeTransfer() {
  }

  @Override
  public String getMethod() {
    return isComplimentary ? "saleorders/free" : "saleorders";
  }

  @Override
  protected VisitInformationDetailType getVisitDetailType() {
    return isComplimentary ? VisitInformationDetailType.DELIVER_FREE_ORDER
        : VisitInformationDetailType.CREATE_ORDER;
  }

  public void sendSingleOrder(BaseSaleDocument baseSaleDocument) {
    if (!NetworkUtil.isNetworkAvailable(context)) {
      EventBus.getDefault().post(new ErrorEvent(StatusCodes.NO_NETWORK));
    }

    this.order = baseSaleDocument;

    RestService restService = ServiceGenerator.createService(RestService.class);

    Call<String> call;
    if (isComplimentary) {
      call = restService.sendFreeOrder((SaleOrderDocument) baseSaleDocument);
    } else {
      call = restService.sendOrder((SaleOrderDocument) baseSaleDocument);
    }

    call.enqueue(new Callback<String>() {
      @Override
      public void onResponse(Call<String> call, Response<String> response) {
        if (response.isSuccessful()) {
          String OrderBackendId = response.body();
          if (OrderBackendId != null) {
            receiveData(OrderBackendId);
            if (getSuccess() == 1) {
              EventBus.getDefault().post(
                  new SendOrderEvent(StatusCodes.SUCCESS, Long.valueOf(OrderBackendId),
                      getSuccessfulMessage()));
            } else {
              EventBus.getDefault().post(
                  new SendOrderEvent(StatusCodes.SERVER_ERROR, order.getId(),
                      getExceptionMessage()));
            }
          } else {
            EventBus.getDefault().post(new ErrorEvent(StatusCodes.INVALID_DATA));
          }
        } else {
          try {
            Timber.d(response.errorBody().string());
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
}
