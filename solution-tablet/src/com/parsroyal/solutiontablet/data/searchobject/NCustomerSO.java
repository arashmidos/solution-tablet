package com.parsroyal.solutiontablet.data.searchobject;

/**
 * Created by Mahyar on 7/13/2015.
 */
public class NCustomerSO extends BaseSO {

  private Long statusId;
  private Integer sent;

  public Long getStatusId() {
    return statusId;
  }

  public void setStatusId(Long statusId) {
    this.statusId = statusId;
  }

  public Integer getSent() {
    return sent;
  }

  public void setSent(Integer sent) {
    this.sent = sent;
  }
}
