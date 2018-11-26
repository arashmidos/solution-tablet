package com.parsroyal.solutiontablet.data.model;

public class SaleOrderDocument extends BaseSaleDocument<SaleOrderItem> {

  private String exportDate;
  private Long smsConfirm;

  public Long getSmsConfirm() {
    return smsConfirm;
  }

  public void setSmsConfirm(Long smsConfirm) {
    this.smsConfirm = smsConfirm;
  }

  public String getExportDate() {
    return exportDate;
  }

  public void setExportDate(String exportDate) {
    this.exportDate = exportDate;
  }
}
