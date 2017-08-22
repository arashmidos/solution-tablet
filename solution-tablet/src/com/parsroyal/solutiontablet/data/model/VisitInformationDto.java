package com.parsroyal.solutiontablet.data.model;

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
  private Long customerId;//Its actually customerBackendId
  private Long salesmanId;
  private SaleType saleType;
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

  /**
   * @return Customer BackendId
   */
  public Long getCustomerId() {
    return customerId;
  }

  /**
   * @param customerId Set CustomerBackendId
   */
  public void setCustomerId(Long customerId) {
    this.customerId = customerId;
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
}
