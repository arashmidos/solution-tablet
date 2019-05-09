package com.parsroyal.solutiontablet.data.searchobject;

/**
 * Created by Arash on 11/15/2017.
 */
public class CustomerPictureSO extends BaseSO {

  private Long visitId;
  private long status;

  public CustomerPictureSO(Long visitId, long status) {
    this.visitId = visitId;
    this.status = status;
  }

  public Long getVisitId() {
    return visitId;
  }

  public void setVisitId(Long visitId) {
    this.visitId = visitId;
  }

  public long getStatus() {
    return status;
  }

  public void setStatus(long status) {
    this.status = status;
  }
}
