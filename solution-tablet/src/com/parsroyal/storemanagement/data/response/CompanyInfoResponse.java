package com.parsroyal.storemanagement.data.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Arash on 2017-08-30
 */

public class CompanyInfoResponse extends Response {

  @SerializedName("companyKey")
  @Expose
  private String companyKey;
  @SerializedName("companyName")
  @Expose
  private String companyName;
  @SerializedName("backendUri")
  @Expose
  private String backendUri;

  public String getCompanyKey() {
    return companyKey;
  }

  public void setCompanyKey(String companyKey) {
    this.companyKey = companyKey;
  }

  public String getCompanyName() {
    return companyName;
  }

  public void setCompanyName(String companyName) {
    this.companyName = companyName;
  }

  public String getBackendUri() {
    return backendUri;
  }

  public void setBackendUri(String backendUri) {
    this.backendUri = backendUri;
  }

}
