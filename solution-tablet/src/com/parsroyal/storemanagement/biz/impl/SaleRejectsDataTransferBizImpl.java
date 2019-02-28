package com.parsroyal.storemanagement.biz.impl;

import android.content.Context;
import com.parsroyal.storemanagement.R;
import com.parsroyal.storemanagement.constants.SaleOrderStatus;
import com.parsroyal.storemanagement.constants.VisitInformationDetailType;
import com.parsroyal.storemanagement.data.entity.SaleOrder;
import com.parsroyal.storemanagement.util.DateUtil;

/**
 * Created by Arash on 7/8/2016.
 */
public class SaleRejectsDataTransferBizImpl extends InvoicedOrdersDataTransfer {

  public SaleRejectsDataTransferBizImpl(Context context) {
    super(context);
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
