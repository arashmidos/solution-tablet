package com.parsroyal.solutiontablet.data.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class BaseSaleDocumentItem {

  private Long goods;
  private Long count1;
  private Long count2;
  @JsonIgnore
  private Integer fiscalYear;
  private Integer companyId;
  private Long discount;
  private Long price1;
  private Long price2;

  public Long getPrice1() {
    return price1;
  }

  public void setPrice1(Long price1) {
    this.price1 = price1;
  }

  public Long getPrice2() {
    return price2;
  }

  public void setPrice2(Long price2) {
    this.price2 = price2;
  }

  public Long getGoods() {
    return goods;
  }

  public void setGoods(Long goods) {
    this.goods = goods;
  }

  public Long getCount1() {
    return count1;
  }

  public void setCount1(Long count1) {
    this.count1 = count1;
  }

  public Long getCount2() {
    return count2;
  }

  public void setCount2(Long count2) {
    this.count2 = count2;
  }

  public Integer getFiscalYear() {
    return fiscalYear;
  }

  public void setFiscalYear(Integer fiscalYear) {
    this.fiscalYear = fiscalYear;
  }

  public Integer getCompanyId() {
    return companyId;
  }

  public void setCompanyId(Integer companyId) {
    this.companyId = companyId;
  }

  public Long getDiscount() {
    return discount;
  }

  public void setDiscount(Long discount) {
    this.discount = discount;
  }
}
