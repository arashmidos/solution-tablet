package com.parsroyal.solutiontablet.data.entity;

import com.parsroyal.solutiontablet.util.Empty;

/**
 * Created by Mahyar on 6/14/2015.
 */

public class Customer extends BaseEntity<Long> {

  public static final String COL_ID = "_id";
  public static final String COL_BACKEND_ID = "BACKEND_ID";
  public static final String COL_FULL_NAME = "FULL_NAME";
  public static final String COL_PHONE_NUMBER = "PHONE_NUMBER";
  public static final String COL_CELL_PHONE = "CELL_PHONE";
  public static final String COL_PROVINCE_BACKEND_ID = "PROVINCE_BACKEND_ID";
  public static final String COL_CITY_BACKEND_ID = "PROVINCE_CITY_ID";
  public static final String COL_ADDRESS = "ADDRESS";
  public static final String COL_ACTIVITY_BACKEND_ID = "ACTIVITY_BACKEND_ID";
  public static final String COL_STORE_SURFACE = "STORE_SURFACE";
  public static final String COL_STORE_LOCATION_TYPE_BACKEND_ID = "STORE_LOCATION_TYPE_BACKEND_ID";
  public static final String COL_CLASS_BACKEND_ID = "CLASS_BACKEND_ID";
  public static final String COL_STATUS = "STATUS";
  public static final String COL_CODE = "CODE";
  public static final String COL_X_LOCATION = "X_LOCATION";
  public static final String COL_Y_LOCATION = "Y_LOCATION";
  public static final String COL_VISIT_LINE_BACKEND_ID = "VISIT_LINE_BACKEND_ID";
  public static final String COL_SHOP_NAME = "SHOP_NAME";
  public static final String COL_NATIONAL_CODE = "NATIONAL_CODE";
  public static final String COL_MUNICIPALITY_CODE = "MUNICIPALITY_CODE";
  public static final String COL_POSTAL_CODE = "POSTAL_CODE";
  public static final String COL_APPROVED = "APPROVED";

  public static final String TABLE_NAME = "COMMER_CUSTOMER";

  public static final String CREATE_TABLE_SQL = "CREATE TABLE " + Customer.TABLE_NAME + " (" +
      " " + Customer.COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
      " " + Customer.COL_BACKEND_ID + " INTEGER," +
      " " + Customer.COL_FULL_NAME + " TEXT NOT NULL," +
      " " + Customer.COL_PHONE_NUMBER + " TEXT," +
      " " + Customer.COL_CELL_PHONE + " TEXT," +
      " " + Customer.COL_PROVINCE_BACKEND_ID + " INTEGER," +
      " " + Customer.COL_CITY_BACKEND_ID + " INTEGER," +
      " " + Customer.COL_ADDRESS + " TEXT NOT NULL," +
      " " + Customer.COL_ACTIVITY_BACKEND_ID + " INTEGER," +
      " " + Customer.COL_STORE_SURFACE + " INTEGER," +
      " " + Customer.COL_STORE_LOCATION_TYPE_BACKEND_ID + " INTEGER," +
      " " + Customer.COL_CLASS_BACKEND_ID + " INTEGER," +
      " " + Customer.COL_STATUS + " INTEGER," +
      " " + Customer.COL_CODE + " TEXT," +
      " " + Customer.COL_X_LOCATION + " REAL," +
      " " + Customer.COL_Y_LOCATION + " REAL," +
      " " + Customer.COL_CREATE_DATE_TIME + " TEXT," +
      " " + Customer.COL_UPDATE_DATE_TIME + " TEXT," +
      " " + Customer.COL_VISIT_LINE_BACKEND_ID + " INTEGER," +
      " " + Customer.COL_SHOP_NAME + " TEXT," +
      " " + Customer.COL_NATIONAL_CODE + " TEXT," +
      " " + Customer.COL_MUNICIPALITY_CODE + " TEXT," +
      " " + Customer.COL_POSTAL_CODE + " TEXT," +
      " " + Customer.COL_APPROVED + " INTEGER" +
      " );";

  private Long id;
  private Long backendId;
  private String fullName;
  private String phoneNumber;
  private String cellPhone;
  private Long provinceBackendId;
  private Long cityBackendId;
  private String address;
  private Long activityBackendId;
  private int storeSurface;
  private Long storeLocationTypeBackendId;
  private Long classBackendId;
  private Long status;
  private String code;
  private Long visitLineBackendId;
  private Double xLocation;
  private Double yLocation;
  private Long salesmanId;
  private String shopName;
  private String postalCode;
  private String nationalCode;
  private String municipalityCode;
  private boolean approved = true;

  public Customer(Double xLocation, String fullName, String shopName, String code) {
    this.xLocation = xLocation;
    this.fullName = fullName;
    this.shopName = shopName;
    this.code = code;
  }

  public Customer() {
  }

  public boolean isApproved() {
    return approved;
  }

  public void setApproved(boolean approved) {
    this.approved = approved;
  }

  public String getShopName() {
    return shopName;
  }

  public void setShopName(String shopName) {
    this.shopName = shopName;
  }

  public String getPostalCode() {
    return postalCode;
  }

  public void setPostalCode(String postalCode) {
    this.postalCode = postalCode;
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

  public Long getClassBackendId() {
    return classBackendId;
  }

  public void setClassBackendId(Long classBackendId) {
    this.classBackendId = classBackendId;
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

  public Double getyLocation() {
    return yLocation;
  }

  public void setyLocation(Double yLocation) {
    this.yLocation = yLocation;
  }

  public Double getxLocation() {
    return xLocation;
  }

  public void setxLocation(Double xLocation) {
    this.xLocation = xLocation;
  }

  public Long getSalesmanId() {
    return salesmanId;
  }

  public void setSalesmanId(Long salesmanId) {
    this.salesmanId = salesmanId;
  }

  public String getString() {
    StringBuilder sb = new StringBuilder();

    sb.append(this.getId());
    sb.append("&");
    sb.append(Empty.isNotEmpty(fullName) ? fullName : "NULL");
    sb.append("&");
    sb.append(Empty.isNotEmpty(phoneNumber) ? phoneNumber : "NULL");
    sb.append("&");
    sb.append(Empty.isNotEmpty(cellPhone) ? cellPhone : "NULL");
    sb.append("&");
    sb.append(
        Empty.isNotEmpty(provinceBackendId) && provinceBackendId != 0 ? provinceBackendId : "NULL");
    sb.append("&");
    sb.append(Empty.isNotEmpty(cityBackendId) && cityBackendId != 0 ? cityBackendId : "NULL");
    sb.append("&");
    sb.append(Empty.isNotEmpty(address) ? address : "NULL");
    sb.append("&");
    sb.append(Empty.isNotEmpty(activityBackendId) ? activityBackendId : "NULL");
    sb.append("&");
    sb.append(Empty.isNotEmpty(storeSurface) ? storeSurface : "NULL");
    sb.append("&");
    sb.append(Empty.isNotEmpty(storeLocationTypeBackendId) ? storeLocationTypeBackendId
        : "NULL");
    sb.append("&");
    sb.append(Empty.isNotEmpty(createDateTime) ? createDateTime : "NULL");
    sb.append("&");
    sb.append(Empty.isNotEmpty(updateDateTime) ? updateDateTime : "NULL");
    sb.append("&");
    sb.append(Empty.isNotEmpty(xLocation) ? xLocation : "NULL");
    sb.append("&");
    sb.append(Empty.isNotEmpty(yLocation) ? yLocation : "NULL");
    sb.append("&");
    sb.append(Empty.isNotEmpty(classBackendId) && classBackendId != 0 ? classBackendId : "NULL");
    //New
    sb.append("&");
    sb.append(Empty.isNotEmpty(shopName) ? shopName : "NULL");
    sb.append("&");
    sb.append(Empty.isNotEmpty(postalCode) ? postalCode : "NULL");
    sb.append("&");
    sb.append(Empty.isNotEmpty(nationalCode) ? nationalCode : "NULL");
    sb.append("&");
    sb.append(Empty.isNotEmpty(municipalityCode) ? municipalityCode : "NULL");
    return sb.toString();
  }

  @Override
  public Long getPrimaryKey() {
    return id;
  }

  @Override
  public boolean equals(Object o) {
    if (o instanceof Customer) {
      Customer cu = (Customer) o;
      if (this.getBackendId().equals(cu.getBackendId())) {
        return true;
      } else {
        return false;
      }
    } else {
      return false;
    }
  }
}
