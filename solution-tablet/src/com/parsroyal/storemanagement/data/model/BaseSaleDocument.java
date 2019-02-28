package com.parsroyal.storemanagement.data.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.List;

public class BaseSaleDocument<T extends BaseSaleDocumentItem> {

  @JsonIgnore
  private Long id;
  private Integer companyId;
  private String date;
  private Long price;
  private String description;
  private Long salesman;
  private Long customer;
  private Long paymentType;
  private Integer type;
  @JsonIgnore
  private Integer fiscalYear;
  private Integer stockCode;
  private Integer officeCode;
  private List<T> items;
  private Long statusCode;
  @JsonIgnore
  private Long status;

  public Long getStatus() {
    return status;
  }

  public void setStatus(Long status) {
    this.status = status;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Integer getCompanyId() {
    return companyId;
  }

  public void setCompanyId(Integer companyId) {
    this.companyId = companyId;
  }

  public String getDate() {
    return date;
  }

  public void setDate(String date) {
    this.date = date;
  }

  public Long getPrice() {
    return price;
  }

  public void setPrice(Long price) {
    this.price = price;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public Long getSalesman() {
    return salesman;
  }

  public void setSalesman(Long salesman) {
    this.salesman = salesman;
  }

  public Long getCustomer() {
    return customer;
  }

  public void setCustomer(Long customer) {
    this.customer = customer;
  }

  public Long getPaymentType() {
    return paymentType;
  }

  public void setPaymentType(Long paymentType) {
    this.paymentType = paymentType;
  }

  public Integer getType() {
    return type;
  }

  public void setType(Integer type) {
    this.type = type;
  }

  public Integer getFiscalYear() {
    return fiscalYear;
  }

  public void setFiscalYear(Integer fiscalYear) {
    this.fiscalYear = fiscalYear;
  }

  public Integer getStockCode() {
    return stockCode;
  }

  public void setStockCode(Integer stockCode) {
    this.stockCode = stockCode;
  }

  public Integer getOfficeCode() {
    return officeCode;
  }

  public void setOfficeCode(Integer officeCode) {
    this.officeCode = officeCode;
  }

  public List<T> getItems() {
    return items;
  }

  public void setItems(List<T> items) {
    this.items = items;
  }

  public Long getStatusCode() {
    return statusCode;
  }

  public void setStatusCode(Long status) {
    this.statusCode = status;
  }
}
