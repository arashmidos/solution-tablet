package com.parsroyal.solutiontablet.data.model;

public class SaleInvoiceDocument extends BaseSaleDocument<SaleInvoiceItem>{

  private Long saleOrderId;

  public Long getSaleOrderId() {
    return saleOrderId;
  }

  public void setSaleOrderId(Long saleOrderId) {
    this.saleOrderId = saleOrderId;
  }
}
