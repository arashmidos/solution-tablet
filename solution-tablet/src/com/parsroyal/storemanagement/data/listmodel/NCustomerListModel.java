package com.parsroyal.storemanagement.data.listmodel;

/**
 * Created by Mahyar on 7/13/2015.
 */
public class NCustomerListModel extends CustomerListModel {

  private Long status;
  private String createDateTime;

  public NCustomerListModel(long primaryKey, String title, String phoneNumber, String cellPhone,
      long status, String createDateTime, long backendId, String shopName) {
    this.primaryKey = primaryKey;
    this.title = title;
    this.phoneNumber = phoneNumber;
    this.cellPhone = cellPhone;
    this.status = status;
    this.createDateTime = createDateTime;
    this.backendId = backendId;
    this.shopName = shopName;
  }

  public Long getStatus() {
    return status;
  }

  public void setStatus(Long status) {
    this.status = status;
  }

  public String getCreateDateTime() {
    return createDateTime;
  }

  public void setCreateDateTime(String createDateTime) {
    this.createDateTime = createDateTime;
  }
}
