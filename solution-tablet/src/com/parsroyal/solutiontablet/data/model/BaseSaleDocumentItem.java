package com.parsroyal.solutiontablet.data.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class BaseSaleDocumentItem {

  private Long goods;
  private Long count1;
  private Long count2;
  @JsonIgnore
  private Integer fiscalYear;
  private Integer companyId;

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
}
