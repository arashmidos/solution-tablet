package com.parsroyal.storemanagement.data.model;

import com.parsroyal.storemanagement.util.PreferenceHelper;

public class SelectOrderRequest {
  //1 new
  //3 next
  //4 delete
  //5 select with order id

  private Integer mode;
  private Long asn;
  private Long psn;
  private Long classNo;

  public SelectOrderRequest() {
    mode = 1;
    asn = PreferenceHelper.getStockKey();
  }

  public SelectOrderRequest(Integer mode) {
    this.mode = mode;
  }

  public SelectOrderRequest(Integer mode, Long classNo) {
    this.mode = mode;
    this.classNo = classNo;
  }

  public SelectOrderRequest(Integer mode, Long asn, Long classNo) {
    this.mode = mode;
    this.asn = asn;
    this.classNo = classNo;
  }

  public Integer getMode() {
    return mode;
  }

  public void setMode(Integer mode) {
    this.mode = mode;
  }

  public Long getAsn() {
    return asn;
  }

  public void setAsn(Long asn) {
    this.asn = asn;
  }

  public Long getPsn() {
    return psn;
  }

  public void setPsn(Long psn) {
    this.psn = psn;
  }

  public Long getClassNo() {
    return classNo;
  }

  public void setClassNo(Long classNo) {
    this.classNo = classNo;
  }
}
