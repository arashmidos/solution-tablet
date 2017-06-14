package com.parsroyal.solutiontablet.biz.impl;

import android.content.Context;
import com.parsroyal.solutiontablet.R;
import com.parsroyal.solutiontablet.constants.SaleOrderStatus;
import com.parsroyal.solutiontablet.constants.VisitInformationDetailType;
import com.parsroyal.solutiontablet.data.entity.SaleOrder;
import com.parsroyal.solutiontablet.ui.observer.ResultObserver;
import com.parsroyal.solutiontablet.util.DateUtil;
import java.util.Locale;

/**
 * Created by Mahyar on 9/2/2015.
 */
public class OrdersDataTransferBizImpl extends InvoicedOrdersDataTransferBizImpl {

  public OrdersDataTransferBizImpl(Context context, ResultObserver resultObserver) {
    super(context, resultObserver);
  }

  @Override
  protected void updateOrderStatus(Long backendId, SaleOrder saleOrder) {
    saleOrder.setBackendId(backendId);
    saleOrder.setStatus(SaleOrderStatus.SENT.getId());
    saleOrder.setUpdateDateTime(DateUtil.getCurrentGregorianFullWithTimeDate());
    saleOrderDao.update(saleOrder);
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
    return "saleorders";
  }

  @Override
  protected VisitInformationDetailType getVisitDetailType() {
    return VisitInformationDetailType.CREATE_ORDER;
  }
}
