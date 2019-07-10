package com.parsroyal.solutiontablet.data.listmodel;

import androidx.annotation.NonNull;

/**
 * Created by Arash on 08/19/2016
 */
public class PaymentListModel extends BaseListModel implements Comparable<PaymentListModel> {

  protected String date;
  protected String amount;
  protected String type;
  protected long customerBackendId;
  private Long status;
  private String customerFullName;
  private String customerCode;
  private String bank;
  private String branch;

  public PaymentListModel(Long primaryKey, String amount, String date, String type,
      long customerBackendId, String customerFullName, Long status, String bank, String branch,
      String customerCode) {
    this.primaryKey = primaryKey;
    this.date = date;
    this.amount = amount;
    this.type = type;
    this.customerBackendId = customerBackendId;
    this.customerFullName = customerFullName;
    this.status = status;
    this.bank = bank;
    this.branch = branch;
    this.customerCode = customerCode;
  }

  public PaymentListModel() {
  }

  public String getCustomerCode() {
    return customerCode;
  }

  public void setCustomerCode(String customerCode) {
    this.customerCode = customerCode;
  }

  public long getCustomerBackendId() {
    return customerBackendId;
  }

  public void setCustomerBackendId(long customerBackendId) {
    this.customerBackendId = customerBackendId;
  }

  public Long getStatus() {
    return status;
  }

  public void setStatus(Long status) {
    this.status = status;
  }

  public String getAmount() {
    return amount;
  }

  public void setAmount(String amount) {
    this.amount = amount;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getDate() {
    return date;
  }

  public void setDate(String date) {
    this.date = date;
  }

  public String getCustomerFullName() {
    return customerFullName;
  }

  public void setCustomerFullName(String customerFullName) {
    this.customerFullName = customerFullName;
  }

  public String getBank() {
    return bank;
  }

  public void setBank(String bank) {
    this.bank = bank;
  }

  public String getBranch() {
    return branch;
  }

  public void setBranch(String branch) {
    this.branch = branch;
  }

  @Override
  public int compareTo(@NonNull PaymentListModel paymentListModel) {
    return this.primaryKey.compareTo(paymentListModel.primaryKey);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    if (!super.equals(o)) {
      return false;
    }
    PaymentListModel that = (PaymentListModel) o;
    return this.primaryKey.equals(that.primaryKey);
  }
}
