package com.parsroyal.solutiontablet.biz.impl;

import android.content.Context;
import com.parsroyal.solutiontablet.R;
import com.parsroyal.solutiontablet.biz.AbstractDataTransferBizImpl;
import com.parsroyal.solutiontablet.biz.KeyValueBiz;
import com.parsroyal.solutiontablet.constants.SaleOrderStatus;
import com.parsroyal.solutiontablet.constants.VisitInformationDetailType;
import com.parsroyal.solutiontablet.data.dao.SaleOrderDao;
import com.parsroyal.solutiontablet.data.dao.SaleOrderItemDao;
import com.parsroyal.solutiontablet.data.dao.impl.SaleOrderDaoImpl;
import com.parsroyal.solutiontablet.data.dao.impl.SaleOrderItemDaoImpl;
import com.parsroyal.solutiontablet.data.entity.SaleOrder;
import com.parsroyal.solutiontablet.data.model.BaseSaleDocument;
import com.parsroyal.solutiontablet.service.VisitService;
import com.parsroyal.solutiontablet.service.impl.VisitServiceImpl;
import com.parsroyal.solutiontablet.ui.observer.ResultObserver;
import com.parsroyal.solutiontablet.util.DateUtil;
import com.parsroyal.solutiontablet.util.Empty;
import com.parsroyal.solutiontablet.util.constants.ApplicationKeys;
import java.util.Locale;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;

/**
 * Created by Mahyar on 8/29/2015.
 */
public class InvoicedOrdersDataTransferBizImpl extends AbstractDataTransferBizImpl<String> {

  protected ResultObserver resultObserver;
  protected SaleOrderDao saleOrderDao;
  protected SaleOrderItemDao saleOrderItemDao;
  protected BaseSaleDocument order;
  protected VisitService visitService;
  private KeyValueBiz keyValueBiz;
  protected int success = 0;
  protected int total = 0;

  public int getSuccess() {
    return success;
  }

  public InvoicedOrdersDataTransferBizImpl(Context context, ResultObserver resultObserver) {
    super(context);
    this.resultObserver = resultObserver;
    this.saleOrderDao = new SaleOrderDaoImpl(context);
    this.saleOrderItemDao = new SaleOrderItemDaoImpl(context);
    this.keyValueBiz = new KeyValueBizImpl(context);
    visitService = new VisitServiceImpl(context);
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
      if (Empty.isNotEmpty(resultObserver)) {
        resultObserver.publishResult(getExceptionMessage());
      }
    }
  }

  protected String getExceptionMessage() {
    return context.getString(R.string.message_exception_in_sending_invoices);
  }

  public String getSuccessfulMessage() {
    return String
        .format(Locale.US, context.getString(R.string.data_transfered_result),
            String.valueOf(success),
            String.valueOf(total - success));
  }

  protected void updateOrderStatus(Long invoiceBackendId, SaleOrder saleOrder) {
    saleOrder.setInvoiceBackendId(invoiceBackendId);
    saleOrder.setStatus(SaleOrderStatus.SENT_INVOICE.getId());
    saleOrder.setUpdateDateTime(DateUtil.getCurrentGregorianFullWithTimeDate());
    saleOrderDao.update(saleOrder);
    visitService.updateVisitDetailId(getVisitDetailType(), saleOrder.getId(), invoiceBackendId);
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
    HttpEntity<BaseSaleDocument> httpEntity = new HttpEntity<>(order, headers);
    return httpEntity;
  }

  protected VisitInformationDetailType getVisitDetailType() {
    String saleType = keyValueBiz.findByKey(ApplicationKeys.SETTING_SALE_TYPE).getValue();
    if (saleType.equals(ApplicationKeys.SALE_DISTRIBUTER)) {
      return VisitInformationDetailType.DELIVER_ORDER;
    }
    return VisitInformationDetailType.CREATE_INVOICE;
  }
}
