package com.parsroyal.solutiontablet.data.model;

import java.util.List;

/**
 * Created by Mahyar on 7/31/2015.
 */
public class VisitInformationDto extends BaseModel {

  private Long id;
  private String visitDate;
  private String startTime;
  private String endTime;
  private Double xLocation;
  private Double yLocation;
  private Long customerBackendId;
  private Long salesmanId;
  private List<VisitInformationDetailDto> details;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getVisitDate() {
    return visitDate;
  }

  public void setVisitDate(String visitDate) {
    this.visitDate = visitDate;
  }

  public String getStartTime() {
    return startTime;
  }

  public void setStartTime(String startTime) {
    this.startTime = startTime;
  }

  public String getEndTime() {
    return endTime;
  }

  public void setEndTime(String endTime) {
    this.endTime = endTime;
  }

  public Double getxLocation() {
    return xLocation;
  }

  public void setxLocation(Double xLocation) {
    this.xLocation = xLocation;
  }

  public Double getyLocation() {
    return yLocation;
  }

  public void setyLocation(Double yLocation) {
    this.yLocation = yLocation;
  }

  public Long getCustomerBackendId() {
    return customerBackendId;
  }

  public void setCustomerBackendId(Long customerBackendId) {
    this.customerBackendId = customerBackendId;
  }

  public Long getSalesmanId() {
    return salesmanId;
  }

  public void setSalesmanId(Long salesmanId) {
    this.salesmanId = salesmanId;
  }

  public List<VisitInformationDetailDto> getDetails() {
    return details;
  }

  public void setDetails(List<VisitInformationDetailDto> details) {
    this.details = details;
  }

}
