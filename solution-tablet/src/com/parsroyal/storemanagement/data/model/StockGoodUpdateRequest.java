package com.parsroyal.storemanagement.data.model;

import com.parsroyal.storemanagement.util.PreferenceHelper;

public class StockGoodUpdateRequest extends BaseModel {

  private Long stockNo;
  private Long exist;
  private int fiscalYear;
  private Long glsSerial;
  private Long batchNo;
  private Long btc;

  public StockGoodUpdateRequest(Long exist, Long glsSerial) {
    this.exist = exist;
    this.glsSerial = glsSerial;
    batchNo = 0L;
    stockNo = PreferenceHelper.getSelectedStockAsn();
  }

  public Long getStockNo() {
    return stockNo;
  }

  public void setStockNo(Long stockNo) {
    this.stockNo = stockNo;
  }

  public Long getExist() {
    return exist;
  }

  public void setExist(Long exist) {
    this.exist = exist;
  }

  public int getFiscalYear() {
    return fiscalYear;
  }

  public void setFiscalYear(int fiscalYear) {
    this.fiscalYear = fiscalYear;
  }

  public Long getGlsSerial() {
    return glsSerial;
  }

  public void setGlsSerial(Long glsSerial) {
    this.glsSerial = glsSerial;
  }

  public Long getBatchNo() {
    return batchNo;
  }

  public void setBatchNo(Long batchNo) {
    this.batchNo = batchNo;
  }

  public Long getBtc() {
    return btc;
  }

  public void setBtc(Long btc) {
    this.btc = btc;
  }
}
