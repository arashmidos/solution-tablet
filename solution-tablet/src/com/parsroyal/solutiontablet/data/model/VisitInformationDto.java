package com.parsroyal.solutiontablet.data.model;

import com.google.gson.annotations.SerializedName;
import com.parsroyal.solutiontablet.constants.SaleType;
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
  @SerializedName("customerId")
  private Long customerBackendId;
  private Long salesmanId;
  private SaleType saleType;
  private List<VisitInformationDetailDto> details;
  private String networkDate;
  private String endNetworkDate;
  private Long distance;
  private boolean phoneVisit;

  public boolean isPhoneVisit() {
    return phoneVisit;
  }

  public void setPhoneVisit(boolean phoneVisit) {
    this.phoneVisit = phoneVisit;
  }

  public String getEndNetworkDate() {
    return endNetworkDate;
  }

  public void setEndNetworkDate(String endNetworkDate) {
    this.endNetworkDate = endNetworkDate;
  }

  public Long getDistance() {
    return distance;
  }

  public void setDistance(Long distance) {
    this.distance = distance;
  }

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

  public Long getCustomerBackendIdId() {
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

  public SaleType getSaleType() {
    return saleType;
  }

  public void setSaleType(SaleType saleType) {
    this.saleType = saleType;
  }

  public String getNetworkDate() {
    return networkDate;
  }

  public void setNetworkDate(String networkDate) {
    this.networkDate = networkDate;
  }
}
