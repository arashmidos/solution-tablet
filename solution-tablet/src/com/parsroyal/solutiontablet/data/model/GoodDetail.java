package com.parsroyal.solutiontablet.data.model;


public class GoodDetail extends BaseModel {

  private Long goodCodeSGL;
  private Long qty;
  private Long jooiid;
  private Long remain;
  private Long gsnid;
  private Long orderId;
  private Long totalRemain;
  private Long orderBy;
  private Long isOrder;
  private String barCode;
  private String goodNameSGL;
  private String uName;

  public Long getGoodCodeSGL() {
    return goodCodeSGL;
  }

  public void setGoodCodeSGL(Long goodCodeSGL) {
    this.goodCodeSGL = goodCodeSGL;
  }

  public Long getQty() {
    return qty;
  }

  public void setQty(Long qty) {
    this.qty = qty;
  }

  public Long getJooiid() {
    return jooiid;
  }

  public void setJooiid(Long jooiid) {
    this.jooiid = jooiid;
  }

  public Long getRemain() {
    return remain;
  }

  public void setRemain(Long remain) {
    this.remain = remain;
  }

  public Long getGsnid() {
    return gsnid;
  }

  public void setGsnid(Long gsnid) {
    this.gsnid = gsnid;
  }

  public Long getOrderId() {
    return orderId;
  }

  public void setOrderId(Long orderId) {
    this.orderId = orderId;
  }

  public Long getTotalRemain() {
    return totalRemain;
  }

  public void setTotalRemain(Long totalRemain) {
    this.totalRemain = totalRemain;
  }

  public Long getOrderBy() {
    return orderBy;
  }

  public void setOrderBy(Long orderBy) {
    this.orderBy = orderBy;
  }

  public Long getIsOrder() {
    return isOrder;
  }

  public void setIsOrder(Long isOrder) {
    this.isOrder = isOrder;
  }

  public String getBarCode() {
    return barCode;
  }

  public void setBarCode(String barCode) {
    this.barCode = barCode;
  }

  public String getGoodNameSGL() {
    return goodNameSGL;
  }

  public void setGoodNameSGL(String goodNameSGL) {
    this.goodNameSGL = goodNameSGL;
  }

  public String getuName() {
    return uName;
  }

  public void setuName(String uName) {
    this.uName = uName;
  }
}
