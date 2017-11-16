package com.parsroyal.solutiontablet.data.listmodel;

/**
 * Created by Arash on 08/19/2016
 */
public class PaymentListModel extends BaseListModel {

  protected String date;
  protected String amount;
  protected String type;
  protected long customerBackendId;
  private Long status;
  private String customerFullName;

  public PaymentListModel(Long primaryKey, String amount, String date, String type,
      long customerBackendId, String customerFullName, Long status) {
    this.primaryKey = primaryKey;
    this.date = date;
    this.amount = amount;
    this.type = type;
    this.customerBackendId = customerBackendId;
    this.customerFullName = customerFullName;
    this.status = status;
  }

  public PaymentListModel() {
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
}
