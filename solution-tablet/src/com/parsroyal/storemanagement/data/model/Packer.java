package com.parsroyal.storemanagement.data.model;


import java.util.List;

public class Packer extends BaseModel {

  private String title;
  private Long ordr;
  private Long classNo;
  private String nameCST;
  private Long customerCodeCST;
  private String orderDate;
  private Long radif;
  private Long ghalam;
  private String inventoryDesc;
  private String insuranceDesc;
  private String ordPayDesc;
  private String otherDesc;
  private String salemanDesc;
  private String tcportDesc;
  private String crDescOSN;
  private List<GoodDetail> goodDetails;

  public List<GoodDetail> getGoodDetails() {
    return goodDetails;
  }

  public void setGoodDetails(List<GoodDetail> goodDetails) {
    this.goodDetails = goodDetails;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public Long getOrdr() {
    return ordr;
  }

  public void setOrdr(Long ordr) {
    this.ordr = ordr;
  }

  public Long getClassNo() {
    return classNo;
  }

  public void setClassNo(Long classNo) {
    this.classNo = classNo;
  }

  public String getNameCST() {
    return nameCST;
  }

  public void setNameCST(String nameCST) {
    this.nameCST = nameCST;
  }

  public Long getCustomerCodeCST() {
    return customerCodeCST;
  }

  public void setCustomerCodeCST(Long customerCodeCST) {
    this.customerCodeCST = customerCodeCST;
  }

  public String getOrderDate() {
    return orderDate;
  }

  public void setOrderDate(String orderDate) {
    this.orderDate = orderDate;
  }

  public Long getRadif() {
    return radif;
  }

  public void setRadif(Long radif) {
    this.radif = radif;
  }

  public Long getGhalam() {
    return ghalam;
  }

  public void setGhalam(Long ghalam) {
    this.ghalam = ghalam;
  }

  public String getInventoryDesc() {
    return inventoryDesc;
  }

  public void setInventoryDesc(String inventoryDesc) {
    this.inventoryDesc = inventoryDesc;
  }

  public String getInsuranceDesc() {
    return insuranceDesc;
  }

  public void setInsuranceDesc(String insuranceDesc) {
    this.insuranceDesc = insuranceDesc;
  }

  public String getOrdPayDesc() {
    return ordPayDesc;
  }

  public void setOrdPayDesc(String ordPayDesc) {
    this.ordPayDesc = ordPayDesc;
  }

  public String getOtherDesc() {
    return otherDesc;
  }

  public void setOtherDesc(String otherDesc) {
    this.otherDesc = otherDesc;
  }

  public String getSalemanDesc() {
    return salemanDesc;
  }

  public void setSalemanDesc(String salemanDesc) {
    this.salemanDesc = salemanDesc;
  }

  public String getTcportDesc() {
    return tcportDesc;
  }

  public void setTcportDesc(String tcportDesc) {
    this.tcportDesc = tcportDesc;
  }

  public String getCrDescOSN() {
    return crDescOSN;
  }

  public void setCrDescOSN(String crDescOSN) {
    this.crDescOSN = crDescOSN;
  }
}
