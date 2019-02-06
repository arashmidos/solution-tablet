package com.parsroyal.solutiontablet.data.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by arash on 9/1/17.
 */

public class UserInfoDetailsResponse {

  @SerializedName(value = "salesmanId", alternate = {"distributorId", "managementId"})
  @Expose
  private Integer salesmanId;
  @SerializedName(value = "salesmanCode", alternate = {"distributorCode", "managementCode"})
  @Expose
  private Integer salesmanCode;
  @SerializedName(value = "salesmanName", alternate = {"distributorName", "managementName"})
  @Expose
  private String salesmanName;
  @SerializedName("companyId")
  @Expose
  private String companyId;
  @SerializedName("companyName")
  @Expose
  private String companyName;
  @SerializedName("stockSerial")
  @Expose
  private String stockSerial;

  public String getStockSerial() {
    return stockSerial;
  }

  public void setStockSerial(String stockSerial) {
    this.stockSerial = stockSerial;
  }

  public Integer getSalesmanId() {
    return salesmanId;
  }

  public void setSalesmanId(Integer salesmanId) {
    this.salesmanId = salesmanId;
  }

  public Integer getSalesmanCode() {
    return salesmanCode;
  }

  public void setSalesmanCode(Integer salesmanCode) {
    this.salesmanCode = salesmanCode;
  }

  public String getSalesmanName() {
    return salesmanName;
  }

  public void setSalesmanName(String salesmanName) {
    this.salesmanName = salesmanName;
  }

  public String getCompanyId() {
    return companyId;
  }

  public void setCompanyId(String companyId) {
    this.companyId = companyId;
  }

  public String getCompanyName() {
    return companyName;
  }

  public void setCompanyName(String companyName) {
    this.companyName = companyName;
  }
}
