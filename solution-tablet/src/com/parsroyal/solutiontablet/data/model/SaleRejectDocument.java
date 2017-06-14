package com.parsroyal.solutiontablet.data.model;

public class SaleRejectDocument extends BaseSaleDocument<SaleRejectItem>{

  private Long saleOrderId;

  public Long getSaleOrderId() {
    return saleOrderId;
  }

  public void setSaleOrderId(Long saleOrderId) {
    this.saleOrderId = saleOrderId;
  }
}
