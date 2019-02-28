package com.parsroyal.storemanagement.data.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Arash on 2017-08-31
 */

public class SettingDetailsResponse extends Response {

  @SerializedName("stock.id")
  @Expose
  private String stockId;
  @SerializedName("branch.id")
  @Expose
  private String branchId;
  @SerializedName("stock.code")
  @Expose
  private String stockCode;
  @SerializedName("branch.code")
  @Expose
  private String branchCode;
  @SerializedName("factor.type")
  @Expose
  private String factorType;
  @SerializedName("reject.type")
  @Expose
  private String rejectType;
  @SerializedName("order.type")
  @Expose
  private String orderType;
  @SerializedName("check.distance.from.customer.value")
  @Expose
  private String checkDistanceFromCustomerValue;
  @SerializedName("use.sale.rate")
  @Expose
  private boolean useSaleRate;
  @SerializedName("check.distance.from.customer")
  @Expose
  private boolean checkDistanceFromCustomer;
  @SerializedName("check.customer.credit")
  @Expose
  private boolean checkCustomerCredit;
  @SerializedName("check.sms.confirm")
  @Expose
  private boolean checkSmsConfirm;

  public boolean isCheckSmsConfirm() {
    return checkSmsConfirm;
  }

  public void setCheckSmsConfirm(boolean checkSmsConfirm) {
    this.checkSmsConfirm = checkSmsConfirm;
  }

  public String getStockCode() {
    return stockCode;
  }

  public void setStockCode(String stockCode) {
    this.stockCode = stockCode;
  }

  public String getBranchCode() {
    return branchCode;
  }

  public void setBranchCode(String branchCode) {
    this.branchCode = branchCode;
  }

  public String getBranchId() {
    return branchId;
  }

  public void setBranchId(String branchId) {
    this.branchId = branchId;
  }

  public boolean isUseSaleRate() {
    return useSaleRate;
  }

  public void setUseSaleRate(boolean useSaleRate) {
    this.useSaleRate = useSaleRate;
  }

  public boolean isCheckDistanceFromCustomer() {
    return checkDistanceFromCustomer;
  }

  public void setCheckDistanceFromCustomer(boolean checkDistanceFromCustomer) {
    this.checkDistanceFromCustomer = checkDistanceFromCustomer;
  }

  public String getStockId() {
    return stockId;
  }

  public void setStockId(String stockId) {
    this.stockId = stockId;
  }

  public String getFactorType() {
    return factorType;
  }

  public void setFactorType(String factorType) {
    this.factorType = factorType;
  }

  public String getRejectType() {
    return rejectType;
  }

  public void setRejectType(String rejectType) {
    this.rejectType = rejectType;
  }

  public String getOrderType() {
    return orderType;
  }

  public void setOrderType(String orderType) {
    this.orderType = orderType;
  }

  public boolean isCheckCustomerCredit() {
    return checkCustomerCredit;
  }

  public void setCheckCustomerCredit(boolean checkCustomerCredit) {
    this.checkCustomerCredit = checkCustomerCredit;
  }

  public String getCheckDistanceFromCustomerValue() {
    return checkDistanceFromCustomerValue;
  }

  public void setCheckDistanceFromCustomerValue(String checkDistanceFromCustomerValue) {
    this.checkDistanceFromCustomerValue = checkDistanceFromCustomerValue;
  }
}
