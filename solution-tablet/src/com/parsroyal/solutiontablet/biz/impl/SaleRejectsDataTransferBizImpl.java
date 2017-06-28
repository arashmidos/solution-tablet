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
public class SaleRejectsDataTransferBizImpl extends InvoicedOrdersDataTransferBizImpl {

  public SaleRejectsDataTransferBizImpl(Context context, ResultObserver resultObserver) {
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
  protected String getExceptionMessage() {
    return context.getString(R.string.message_exception_in_sending_sale_rejects);
  }

  @Override
  public void beforeTransfer() {
  }

  @Override
  public String getMethod() {
    return "salerejects";
  }

  @Override
  protected VisitInformationDetailType getVisitDetailType() {
    return VisitInformationDetailType.CREATE_REJECT;
  }
}
