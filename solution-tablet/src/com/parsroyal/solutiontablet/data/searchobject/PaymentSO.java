package com.parsroyal.solutiontablet.data.searchobject;

/**
 * Created by Arash on 8/21/2016.
 */
public class PaymentSO extends BaseSO {

  private Long status;
  private Long customerBackendId;

  public PaymentSO(Long customerBackendId, Long sentStatus) {
    this.customerBackendId = customerBackendId;
    this.status = sentStatus;
  }

  public PaymentSO(Long sentStatus) {
    this.status = sentStatus;
    this.customerBackendId = -1L;
  }

  public Long getStatus() {
    return status;
  }

  public void setStatus(Long status) {
    this.status = status;
  }

  public Long getCustomerBackendId() {
    return customerBackendId;
  }

  public void setCustomerBackendId(Long customerBackendId) {
    this.customerBackendId = customerBackendId;
  }
}
