package com.parsroyal.solutiontablet.data.model;

public class SaleOrderDocument extends BaseSaleDocument<SaleOrderItem> {

  private String exportDate;

  public String getExportDate() {
    return exportDate;
  }

  public void setExportDate(String exportDate) {
    this.exportDate = exportDate;
  }
}
