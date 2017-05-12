package com.parsroyal.solutiontablet.data.model;

import com.parsroyal.solutiontablet.constants.VisitInformationDetailType;

/**
 * Created by mahyar on 3/5/17.
 */
public class VisitInformationDetailDto {

  private long id;
  private VisitInformationDetailType type;
  private long typeId;
  private String data;
  private long customerVisitId;

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public VisitInformationDetailType getType() {
    return type;
  }

  public void setType(VisitInformationDetailType type) {
    this.type = type;
  }

  public String getData() {
    return data;
  }

  public void setData(String data) {
    this.data = data;
  }

  public long getCustomerVisitId() {
    return customerVisitId;
  }

  public void setCustomerVisitId(long customerVisitId) {
    this.customerVisitId = customerVisitId;
  }

  public long getTypeId() {
    return typeId;
  }

  public void setTypeId(long typeId) {
    this.typeId = typeId;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    VisitInformationDetailDto that = (VisitInformationDetailDto) o;

    return type == that.type;

  }
}
