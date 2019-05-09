package com.parsroyal.solutiontablet.data.model;

import com.parsroyal.solutiontablet.data.entity.Customer;
import com.parsroyal.solutiontablet.data.entity.VisitLineDate;
import java.util.List;

/**
 * Created by Mahyar on 7/5/2015.
 */
public class VisitLineDto extends BaseModel {

  private Long backendId;
  private Integer code;
  private String title;
  private List<Customer> customerList;
  private List<VisitLineDate> dates;

  public List<VisitLineDate> getDates() {
    return dates;
  }

  public void setDates(List<VisitLineDate> dates) {
    this.dates = dates;
  }

  public Long getBackendId() {
    return backendId;
  }

  public void setBackendId(Long backendId) {
    this.backendId = backendId;
  }

  public Integer getCode() {
    return code;
  }

  public void setCode(Integer code) {
    this.code = code;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public List<Customer> getCustomerList() {
    return customerList;
  }

  public void setCustomerList(List<Customer> customerList) {
    this.customerList = customerList;
  }
}
