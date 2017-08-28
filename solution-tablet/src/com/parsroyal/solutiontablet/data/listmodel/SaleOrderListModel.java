package com.parsroyal.solutiontablet.data.listmodel;

/**
 * Created by Mahyar on 8/25/2015.
 */
public class SaleOrderListModel extends BaseListModel {

  private Long id;
  private Long backendId;
  private String date;
  private Long amount;
  private String paymentTypeTitle;
  private String customerName;
  private Long status;
  private Long customerBackendId;

  public SaleOrderListModel(Long id, Long backendId, String date, Long amount, String paymentTypeTitle, String customerName, Long status, Long customerBackendId) {
    this.id = id;
    this.backendId = backendId;
    this.date = date;
    this.amount = amount;
    this.paymentTypeTitle = paymentTypeTitle;
    this.customerName = customerName;
    this.status = status;
    this.customerBackendId = customerBackendId;
  }

  public SaleOrderListModel() {
  }

  public Long getCustomerBackendId() {
    return customerBackendId;
  }

  public void setCustomerBackendId(Long customerBackendId) {
    this.customerBackendId = customerBackendId;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Long getBackendId() {
    return backendId;
  }

  public void setBackendId(Long backendId) {
    this.backendId = backendId;
  }

  public String getDate() {
    return date;
  }

  public void setDate(String date) {
    this.date = date;
  }

  public Long getAmount() {
    return amount;
  }

  public void setAmount(Long amount) {
    this.amount = amount;
  }

  public String getPaymentTypeTitle() {
    return paymentTypeTitle;
  }

  public void setPaymentTypeTitle(String paymentTypeTitle) {
    this.paymentTypeTitle = paymentTypeTitle;
  }

  public Long getStatus() {
    return status;
  }

  public void setStatus(Long status) {
    this.status = status;
  }

  public String getCustomerName() {
    return customerName;
  }

  public void setCustomerName(String customerName) {
    this.customerName = customerName;
  }

  @Override
  public Long getPrimaryKey() {
    return getId();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    SaleOrderListModel that = (SaleOrderListModel) o;

    return id != null ? id.equals(that.id) : that.id == null;
  }

  @Override
  public int hashCode() {
    return id.hashCode();
  }
}
