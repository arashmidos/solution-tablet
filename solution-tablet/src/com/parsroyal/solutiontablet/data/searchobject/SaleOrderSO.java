package com.parsroyal.solutiontablet.data.searchobject;

/**
 * Created by Mahyar on 8/25/2015.
 */
public class SaleOrderSO extends BaseSO {

  private Long statusId;
  private Long customerBackendId;

  public Long getStatusId() {
    return statusId;
  }

  public void setStatusId(Long statusId) {
    this.statusId = statusId;
  }

  public Long getCustomerBackendId() {
    return customerBackendId;
  }

  public void setCustomerBackendId(Long customerBackendId) {
    this.customerBackendId = customerBackendId;
  }
}
