package com.parsroyal.solutiontablet.data.model;

import com.parsroyal.solutiontablet.data.entity.Customer;
import com.parsroyal.solutiontablet.util.DateUtil;
import java.util.Date;
import java.util.List;

/**
 * Created by Mahyar on 8/27/2015.
 */
public class SaleOrderDto extends BaseModel {

  private Long id;
  private Long number;
  private String date;
  private Long amount;
  private Long paymentTypeBackendId;
  private Long salesmanId;
  private Long customerBackendId;
  private String description;
  private Long status;
  private Long backendId;
  private Long invoiceBackendId;
  private String createDateTime;
  private String updateDateTime;
  private List<SaleOrderItemDto> orderItems;
  private Customer customer;
  private Long rejectType;

  public SaleOrderDto(Long statusID, Customer customer) {
    this.status = statusID;
    this.customer = customer;
    this.date = DateUtil.convertDate(new Date(), DateUtil.GLOBAL_FORMATTER, "FA");
    this.amount = 0L;
    this.customerBackendId = customer.getBackendId();
    this.createDateTime = DateUtil.getCurrentGregorianFullWithTimeDate();
    this.updateDateTime = DateUtil.getCurrentGregorianFullWithTimeDate();
  }

  public SaleOrderDto() {
  }

  public Long getRejectType() {
    return rejectType;
  }

  public void setRejectType(Long rejectType) {
    this.rejectType = rejectType;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Long getNumber() {
    return number;
  }

  public void setNumber(Long number) {
    this.number = number;
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

  public Long getPaymentTypeBackendId() {
    return paymentTypeBackendId;
  }

  public void setPaymentTypeBackendId(Long paymentTypeBackendId) {
    this.paymentTypeBackendId = paymentTypeBackendId;
  }

  public Long getSalesmanId() {
    return salesmanId;
  }

  public void setSalesmanId(Long salesmanId) {
    this.salesmanId = salesmanId;
  }

  public Long getCustomerBackendId() {
    return customerBackendId;
  }

  public void setCustomerBackendId(Long customerBackendId) {
    this.customerBackendId = customerBackendId;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public Long getStatus() {
    return status;
  }

  public void setStatus(Long status) {
    this.status = status;
  }

  public Long getBackendId() {
    return backendId;
  }

  public void setBackendId(Long backendId) {
    this.backendId = backendId;
  }

  public Long getInvoiceBackendId() {
    return invoiceBackendId;
  }

  public void setInvoiceBackendId(Long invoiceBackendId) {
    this.invoiceBackendId = invoiceBackendId;
  }

  public List<SaleOrderItemDto> getOrderItems() {
    return orderItems;
  }

  public void setOrderItems(List<SaleOrderItemDto> orderItems) {
    this.orderItems = orderItems;
  }

  public Customer getCustomer() {
    return customer;
  }

  public void setCustomer(Customer customer) {
    this.customer = customer;
  }

  public String getCreateDateTime() {
    return createDateTime;
  }

  public void setCreateDateTime(String createDateTime) {
    this.createDateTime = createDateTime;
  }

  public String getUpdateDateTime() {
    return updateDateTime;
  }

  public void setUpdateDateTime(String updateDateTime) {
    this.updateDateTime = updateDateTime;
  }
}
