package com.parsroyal.storemanagement.data.model;

public class SaleInvoiceDocument extends BaseSaleDocument<SaleInvoiceItem> {

  private Long saleOrderId;
  private Long rejectType;
  private Long visitlineBackendId;
  private Long invoiceBackendId;

  public Long getInvoiceBackendId() {
    return invoiceBackendId;
  }

  public void setInvoiceBackendId(Long invoiceBackendId) {
    this.invoiceBackendId = invoiceBackendId;
  }

  public Long getVisitlineBackendId() {
    return visitlineBackendId;
  }

  public void setVisitlineBackendId(Long visitlineBackendId) {
    this.visitlineBackendId = visitlineBackendId;
  }

  public Long getRejectType() {
    return rejectType;
  }

  public void setRejectType(Long rejectType) {
    this.rejectType = rejectType;
  }

  public Long getSaleOrderId() {
    return saleOrderId;
  }

  public void setSaleOrderId(Long saleOrderId) {
    this.saleOrderId = saleOrderId;
  }
}
