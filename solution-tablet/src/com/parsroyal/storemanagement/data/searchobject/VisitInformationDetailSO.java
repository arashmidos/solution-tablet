package com.parsroyal.storemanagement.data.searchobject;

import com.parsroyal.storemanagement.constants.VisitInformationDetailType;

/**
 * Created by Arash on 11/15/2017.
 */
public class VisitInformationDetailSO extends BaseSO {

  private String groupBy;
  private Long visitId;
  private VisitInformationDetailType type;
  private Long typeId;

  public VisitInformationDetailSO(long visitId, String groupBy) {
    this.visitId = visitId;
    this.groupBy = groupBy;
  }

  public VisitInformationDetailSO(long visitId) {
    this.visitId = visitId;
  }

  public String getGroupBy() {
    return groupBy;
  }

  public void setGroupBy(String groupBy) {
    this.groupBy = groupBy;
  }

  public Long getVisitId() {
    return visitId;
  }

  public void setVisitId(Long visitId) {
    this.visitId = visitId;
  }

  public VisitInformationDetailType getType() {
    return type;
  }

  public void setType(VisitInformationDetailType type) {
    this.type = type;
  }

  public Long getTypeId() {
    return typeId;
  }

  public void setTypeId(Long typeId) {
    this.typeId = typeId;
  }
}
