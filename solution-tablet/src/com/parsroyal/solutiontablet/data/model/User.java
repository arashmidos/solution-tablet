package com.parsroyal.solutiontablet.data.model;

/**
 * Created by Mahyar on 6/13/2015.
 */
public class User extends BaseModel {

  private String fullName;
  private String userCode;
  private String companyName;
  private String salesmanId;
  private long companyId;

  public long getCompanyId() {
    return companyId;
  }

  public void setCompanyId(long companyId) {
    this.companyId = companyId;
  }


  public String getFullName() {
    return fullName;
  }

  public void setFullName(String fullName) {
    this.fullName = fullName;
  }

  public String getUserCode() {
    return userCode;
  }

  public void setUserCode(String userCode) {
    this.userCode = userCode;
  }

  public String getCompanyName() {
    return companyName;
  }

  public void setCompanyName(String companyName) {
    this.companyName = companyName;
  }

  public String getSalesmanId() {
    return salesmanId;
  }

  public void setSalesmanId(String salesmanId) {
    this.salesmanId = salesmanId;
  }
}
