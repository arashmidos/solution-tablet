package com.parsroyal.solutiontablet.data.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class SaleInvoiceDocument extends BaseSaleDocument<SaleInvoiceItem> {

  private Long saleOrderId;
  @JsonIgnore
  private Long rejectType;
  private Long visitlineBackendId;

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
