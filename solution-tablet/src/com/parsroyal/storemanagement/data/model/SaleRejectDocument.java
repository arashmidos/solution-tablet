package com.parsroyal.storemanagement.data.model;

public class SaleRejectDocument extends BaseSaleDocument<SaleRejectItem> {

  private Long saleOrderId;
  private Long rejectType;
  private Long visitlineBackendId;

  public Long getRejectType() {
    return rejectType;
  }

  public void setRejectType(Long rejectType) {
    this.rejectType = rejectType;
  }

  public Long getVisitlineBackendId() {
    return visitlineBackendId;
  }

  public void setVisitlineBackendId(Long visitlineBackendId) {
    this.visitlineBackendId = visitlineBackendId;
  }

  public Long getSaleOrderId() {
    return saleOrderId;
  }

  public void setSaleOrderId(Long saleOrderId) {
    this.saleOrderId = saleOrderId;
  }
}
