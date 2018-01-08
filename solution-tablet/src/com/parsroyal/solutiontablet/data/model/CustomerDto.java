package com.parsroyal.solutiontablet.data.model;

/**
 * Created by Mahyar on 7/11/2015.
 */
public class CustomerDto extends BaseModel {

  private Long id;
  private Long backendId;
  private String fullName;
  private String phoneNumber;
  private String cellPhone;
  private Long provinceBackendId;
  private Long cityBackendId;
  private String address;
  private Long activityBackendId;
  private String activityTitle;
  private int storeSurface;
  private Long storeLocationTypeBackendId;
  private Long status;
  private String code;
  private Long visitLineBackendId;
  private String createDateTime;
  private String updateDateTime;
  private Double xLocation;
  private Double yLocation;
  private Long salesmanId;
  private Long classBackendId;
  private String shopName;
  private String nationalCode;
  private String municipalityCode;
  private String postalCode;
  private boolean isApproved;
  private String customerDescription;

  public boolean isApproved() {
    return isApproved;
  }

  public void setApproved(boolean approved) {
    isApproved = approved;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Long getBackendId() {
    return backendId;
  }

  public void setBackendId(Long backendId) {
    this.backendId = backendId;
  }

  public String getFullName() {
    return fullName;
  }

  public void setFullName(String fullName) {
    this.fullName = fullName;
  }

  public String getPhoneNumber() {
    return phoneNumber;
  }

  public void setPhoneNumber(String phoneNumber) {
    this.phoneNumber = phoneNumber;
  }

  public String getCellPhone() {
    return cellPhone;
  }

  public void setCellPhone(String cellPhone) {
    this.cellPhone = cellPhone;
  }

  public Long getProvinceBackendId() {
    return provinceBackendId;
  }

  public void setProvinceBackendId(Long provinceBackendId) {
    this.provinceBackendId = provinceBackendId;
  }

  public Long getCityBackendId() {
    return cityBackendId;
  }

  public void setCityBackendId(Long cityBackendId) {
    this.cityBackendId = cityBackendId;
  }

  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = address;
  }

  public Long getActivityBackendId() {
    return activityBackendId;
  }

  public void setActivityBackendId(Long activityBackendId) {
    this.activityBackendId = activityBackendId;
  }

  public String getActivityTitle() {
    return activityTitle;
  }

  public void setActivityTitle(String activityTitle) {
    this.activityTitle = activityTitle;
  }

  public int getStoreSurface() {
    return storeSurface;
  }

  public void setStoreSurface(int storeSurface) {
    this.storeSurface = storeSurface;
  }

  public Long getStoreLocationTypeBackendId() {
    return storeLocationTypeBackendId;
  }

  public void setStoreLocationTypeBackendId(Long storeLocationTypeBackendId) {
    this.storeLocationTypeBackendId = storeLocationTypeBackendId;
  }

  public Long getStatus() {
    return status;
  }

  public void setStatus(Long status) {
    this.status = status;
  }

  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public Long getVisitLineBackendId() {
    return visitLineBackendId;
  }

  public void setVisitLineBackendId(Long visitLineBackendId) {
    this.visitLineBackendId = visitLineBackendId;
  }

  public String getCreateDateTime() {
    return createDateTime;
  }

  public void setCreateDateTime(String createDateTime) {
    this.createDateTime = createDateTime;
  }

  public String getUpdateDateTime() {
    return updateDateTime;
  }

  public void setUpdateDateTime(String updateDateTime) {
    this.updateDateTime = updateDateTime;
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

  public Long getSalesmanId() {
    return salesmanId;
  }

  public void setSalesmanId(Long salesmanId) {
    this.salesmanId = salesmanId;
  }

  public Long getClassBackendId() {
    return classBackendId;
  }

  public void setClassBackendId(Long classBackendId) {
    this.classBackendId = classBackendId;
  }

  public String getShopName() {
    return shopName;
  }

  public void setShopName(String shopName) {
    this.shopName = shopName;
  }

  public String getNationalCode() {
    return nationalCode;
  }

  public void setNationalCode(String nationalCode) {
    this.nationalCode = nationalCode;
  }

  public String getMunicipalityCode() {
    return municipalityCode;
  }

  public void setMunicipalityCode(String municipalityCode) {
    this.municipalityCode = municipalityCode;
  }

  public String getPostalCode() {
    return postalCode;
  }

  public void setPostalCode(String postalCode) {
    this.postalCode = postalCode;
  }

  public String getCustomerDescription() {
    return customerDescription;
  }

  public void setCustomerDescription(String customerDescription) {
    this.customerDescription = customerDescription;
  }
}
