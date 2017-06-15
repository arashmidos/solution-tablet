package com.parsroyal.solutiontablet.biz.impl;

import android.content.Context;
import android.util.Log;
import com.crashlytics.android.Crashlytics;
import com.parsroyal.solutiontablet.R;
import com.parsroyal.solutiontablet.biz.AbstractDataTransferBizImpl;
import com.parsroyal.solutiontablet.biz.KeyValueBiz;
import com.parsroyal.solutiontablet.constants.SaleOrderStatus;
import com.parsroyal.solutiontablet.data.dao.SaleOrderDao;
import com.parsroyal.solutiontablet.data.dao.SaleOrderItemDao;
import com.parsroyal.solutiontablet.data.dao.impl.SaleOrderDaoImpl;
import com.parsroyal.solutiontablet.data.dao.impl.SaleOrderItemDaoImpl;
import com.parsroyal.solutiontablet.data.entity.KeyValue;
import com.parsroyal.solutiontablet.data.entity.SaleOrder;
import com.parsroyal.solutiontablet.data.entity.SaleOrderItem;
import com.parsroyal.solutiontablet.data.model.SaleOrderList;
import com.parsroyal.solutiontablet.ui.observer.ResultObserver;
import com.parsroyal.solutiontablet.util.DateUtil;
import com.parsroyal.solutiontablet.util.Empty;
import com.parsroyal.solutiontablet.util.constants.ApplicationKeys;
import java.util.List;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;

/**
 * Created by Mahyar on 8/21/2015.
 */
public class SaleOrderForDeliveryDataTaransferBizImpl extends
    AbstractDataTransferBizImpl<SaleOrderList> {

  private ResultObserver resultObserver;
  private SaleOrderDao saleOrderDao;
  private SaleOrderItemDao saleOrderItemDao;
  private KeyValueBiz keyValueBiz;

  public SaleOrderForDeliveryDataTaransferBizImpl(Context context, ResultObserver resultObserver) {
    super(context);
    this.resultObserver = resultObserver;
    this.saleOrderDao = new SaleOrderDaoImpl(context);
    this.saleOrderItemDao = new SaleOrderItemDaoImpl(context);
    this.keyValueBiz = new KeyValueBizImpl(context);
  }

  @Override
  public void receiveData(SaleOrderList data) {
    try {
      if (Empty.isNotEmpty(data)) {
        List<SaleOrder> deliverableOrders = saleOrderDao
            .retrieveSaleOrderByStatus(SaleOrderStatus.DELIVERABLE.getId());
        for (SaleOrder deliverableOrder : deliverableOrders) {
          saleOrderItemDao.deleteAllItemsBySaleOrderId(deliverableOrder.getId());
          saleOrderDao.delete(deliverableOrder.getId());
        }

        for (SaleOrder saleOrder : data.getOrderList()) {
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

        resultObserver.publishResult(
            context.getString(R.string.message_deliverable_orders_transferred_successfully));

      } else {
        resultObserver
            .publishResult(context.getString(R.string.message_found_no_deliverable_orders));
      }

    } catch (Exception ex) {
      Crashlytics.log(Log.ERROR, "Data transfer", "Error in receiving SaleOrderForDeliveryData " + ex.getMessage());
      resultObserver.publishResult(
          context.getString(R.string.message_exception_in_transfering_sale_order_for_delivery));
    }
  }

  @Override
  public void beforeTransfer() {
    resultObserver
        .publishResult(context.getString(R.string.message_transferring_deliverable_orders));
  }

  @Override
  public ResultObserver getObserver() {
    return resultObserver;
  }

  @Override
  public String getMethod() {
    return "saleorders/deliverable";
  }

  @Override
  public Class getType() {
    return SaleOrderList.class;
  }

  @Override
  public HttpMethod getHttpMethod() {
    return HttpMethod.POST;
  }

  @Override
  protected MediaType getContentType() {
    return MediaType.TEXT_PLAIN;
  }

  @Override
  protected HttpEntity getHttpEntity(HttpHeaders headers) {
    KeyValue userCodeKey = keyValueBiz.findByKey(ApplicationKeys.SETTING_USER_CODE);
    return new HttpEntity<>(userCodeKey.getValue(), headers);
  }
}
