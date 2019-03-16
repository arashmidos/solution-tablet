package com.parsroyal.storemanagement.data.entity;

public class Stock {

  private Long asn;
  private Long codeSTK;
  private String nameSTK;
  private Long stockCntStatusSTK;

  public Long getAsn() {
    return asn;
  }

  public void setAsn(Long asn) {
    this.asn = asn;
  }

  public Long getCodeSTK() {
    return codeSTK;
  }

  public void setCodeSTK(Long codeSTK) {
    this.codeSTK = codeSTK;
  }

  public String getNameSTK() {
    return nameSTK;
  }

  public void setNameSTK(String nameSTK) {
    this.nameSTK = nameSTK;
  }

  public Long getStockCntStatusSTK() {
    return stockCntStatusSTK;
  }

  public void setStockCntStatusSTK(Long stockCntStatusSTK) {
    this.stockCntStatusSTK = stockCntStatusSTK;
  }
}
