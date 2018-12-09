package com.parsroyal.solutiontablet.biz.impl;

import android.content.Context;
import android.util.Log;
import com.parsroyal.solutiontablet.constants.SaleOrderStatus;
import com.parsroyal.solutiontablet.constants.StatusCodes;
import com.parsroyal.solutiontablet.data.dao.SaleOrderDao;
import com.parsroyal.solutiontablet.data.dao.SaleOrderItemDao;
import com.parsroyal.solutiontablet.data.dao.impl.SaleOrderDaoImpl;
import com.parsroyal.solutiontablet.data.dao.impl.SaleOrderItemDaoImpl;
import com.parsroyal.solutiontablet.data.entity.SaleOrder;
import com.parsroyal.solutiontablet.data.entity.SaleOrderItem;
import com.parsroyal.solutiontablet.data.event.DataTransferErrorEvent;
import com.parsroyal.solutiontablet.data.event.DataTransferSuccessEvent;
import com.parsroyal.solutiontablet.data.model.SaleOrderList;
import com.parsroyal.solutiontablet.service.GetDataRestService;
import com.parsroyal.solutiontablet.service.ServiceGenerator;
import com.parsroyal.solutiontablet.service.SettingService;
import com.parsroyal.solutiontablet.service.impl.SettingServiceImpl;
import com.parsroyal.solutiontablet.util.DateUtil;
import com.parsroyal.solutiontablet.util.Empty;
import com.parsroyal.solutiontablet.util.NetworkUtil;
import com.parsroyal.solutiontablet.util.constants.ApplicationKeys;
import java.io.IOException;
import java.util.List;
import org.greenrobot.eventbus.EventBus;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
          if (Empty.isNotEmpty(saleOrderList)&& Empty.isNotEmpty(saleOrderList.getOrderList())) {
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
            Log.d("TAG", response.errorBody().string());
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
