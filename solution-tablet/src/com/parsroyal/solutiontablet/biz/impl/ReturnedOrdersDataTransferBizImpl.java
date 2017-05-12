package com.parsroyal.solutiontablet.biz.impl;

import android.content.Context;
import com.parsroyal.solutiontablet.R;
import com.parsroyal.solutiontablet.constants.SaleOrderStatus;
import com.parsroyal.solutiontablet.constants.VisitInformationDetailType;
import com.parsroyal.solutiontablet.data.entity.SaleOrder;
import com.parsroyal.solutiontablet.ui.observer.ResultObserver;
import com.parsroyal.solutiontablet.util.DateUtil;

/**
 * Created by Arash on 7/8/2016.
 */
public class ReturnedOrdersDataTransferBizImpl extends InvoicedOrdersDataTransferBizImpl {

  public ReturnedOrdersDataTransferBizImpl(Context context, ResultObserver resultObserver) {
    super(context, resultObserver);
  }

  @Override
  protected void updateOrderStatus(Long backendId, SaleOrder saleOrder) {
    saleOrder.setBackendId(backendId);
    saleOrder.setStatus(SaleOrderStatus.REJECTED_SENT.getId());
    saleOrder.setUpdateDateTime(DateUtil.getCurrentGregorianFullWithTimeDate());
    saleOrderDao.update(saleOrder);
  }

  @Override
  protected String getSuccessfulMessage() {
    return context.getString(R.string.returned_orders_data_transferred_successfully);
  }

  @Override
  protected String getExceptionMessage() {
    return context.getString(R.string.message_exception_in_sending_returned_orders);
  }

  @Override
  public void beforeTransfer() {
    resultObserver.publishResult(context.getString(R.string.message_transferring_returned_orders));
  }

  @Override
  public String getMethod() {
    return "reject/create";
  }

  @Override
  protected VisitInformationDetailType getVisitDetailType() {
    return VisitInformationDetailType.CREATE_REJECT;
  }
}
