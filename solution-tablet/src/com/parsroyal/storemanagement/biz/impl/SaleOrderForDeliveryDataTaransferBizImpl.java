package com.parsroyal.storemanagement.biz.impl;

import android.content.Context;
import com.parsroyal.storemanagement.constants.SaleOrderStatus;
import com.parsroyal.storemanagement.constants.StatusCodes;
import com.parsroyal.storemanagement.data.dao.SaleOrderDao;
import com.parsroyal.storemanagement.data.dao.SaleOrderItemDao;
import com.parsroyal.storemanagement.data.dao.impl.SaleOrderDaoImpl;
import com.parsroyal.storemanagement.data.dao.impl.SaleOrderItemDaoImpl;
import com.parsroyal.storemanagement.data.entity.SaleOrder;
import com.parsroyal.storemanagement.data.entity.SaleOrderItem;
import com.parsroyal.storemanagement.data.event.DataTransferErrorEvent;
import com.parsroyal.storemanagement.data.event.DataTransferSuccessEvent;
import com.parsroyal.storemanagement.data.model.SaleOrderList;
import com.parsroyal.storemanagement.service.GetDataRestService;
import com.parsroyal.storemanagement.service.ServiceGenerator;
import com.parsroyal.storemanagement.service.SettingService;
import com.parsroyal.storemanagement.service.impl.SettingServiceImpl;
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
import timber.log.Timber;

/**
 * Created by Arash on 8/21/2015.
 */
public class SaleOrderForDeliveryDataTaransferBizImpl {

  private final SettingService settingService;
  private final Context context;
  private SaleOrderDao saleOrderDao;
  private SaleOrderItemDao saleOrderItemDao;

  public SaleOrderForDeliveryDataTaransferBizImpl(Context context) {
    this.context = context;
    this.saleOrderDao = new SaleOrderDaoImpl(context);
    this.saleOrderItemDao = new SaleOrderItemDaoImpl(context);
    this.settingService = new SettingServiceImpl();
  }

  public void exchangeData() {
    if (!NetworkUtil.isNetworkAvailable(context)) {
      EventBus.getDefault().post(new DataTransferErrorEvent(StatusCodes.NO_NETWORK));
    }

    GetDataRestService restService = ServiceGenerator.createService(GetDataRestService.class);

    Call<SaleOrderList> call = restService.getAllSaleOrderForDelivery(
        settingService.getSettingValue(ApplicationKeys.SALESMAN_ID));

    call.enqueue(new Callback<SaleOrderList>() {
      @Override
      public void onResponse(Call<SaleOrderList> call, Response<SaleOrderList> response) {
        if (response.isSuccessful()) {
          SaleOrderList saleOrderList = response.body();
          if (Empty.isNotEmpty(saleOrderList) && Empty.isNotEmpty(saleOrderList.getOrderList())) {
            List<SaleOrder> deliverableOrders = saleOrderDao
                .retrieveSaleOrderByStatus(SaleOrderStatus.DELIVERABLE.getId());
            for (SaleOrder deliverableOrder : deliverableOrders) {
              saleOrderItemDao.deleteAllItemsBySaleOrderId(deliverableOrder.getId());
              saleOrderDao.delete(deliverableOrder.getId());
            }

            for (SaleOrder saleOrder : saleOrderList.getOrderList()) {
              saleOrder.setCreateDateTime(DateUtil.getCurrentGregorianFullWithTimeDate());
              saleOrder.setUpdateDateTime(DateUtil.getCurrentGregorianFullWithTimeDate());
              saleOrder.setStatus(SaleOrderStatus.DELIVERABLE.getId());
              Long saleOrderId = saleOrderDao.create(saleOrder);

              for (SaleOrderItem orderItem : saleOrder.getOrderItems()) {
                orderItem.setSaleOrderId(saleOrderId);
                orderItem.setCreateDateTime(DateUtil.getCurrentGregorianFullWithTimeDate());
                orderItem.setUpdateDateTime(DateUtil.getCurrentGregorianFullWithTimeDate());
                saleOrderItemDao.create(orderItem);
              }
            }
            EventBus.getDefault().post(new DataTransferSuccessEvent("", StatusCodes.SUCCESS));
          } else {
            EventBus.getDefault().post(new DataTransferSuccessEvent("", StatusCodes.NO_DATA_ERROR));
          }
        } else {
          try {
            Timber.d(response.errorBody().string());
          } catch (IOException e) {
            e.printStackTrace();
          }
          EventBus.getDefault().post(new DataTransferErrorEvent(StatusCodes.SERVER_ERROR));
        }
      }

      @Override
      public void onFailure(Call<SaleOrderList> call, Throwable t) {
        EventBus.getDefault().post(new DataTransferErrorEvent(StatusCodes.NETWORK_ERROR));
      }
    });
  }
}
