package com.parsroyal.storemanagement.data.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by arash on 9/1/17.
 */

public class UserInfoResponse {

  @SerializedName("id")
  @Expose
  private Integer id;
  @SerializedName("details")
  @Expose
  private UserInfoDetailsResponse details;
  @SerializedName("sub")
  @Expose
  private String sub;
  @SerializedName("exp")
  @Expose
  private Integer exp;

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public UserInfoDetailsResponse getDetails() {
    return details;
  }

  public void setDetails(UserInfoDetailsResponse details) {
    this.details = details;
  }

  public String getSub() {
    return sub;
  }

  public void setSub(String sub) {
    this.sub = sub;
  }

  public Integer getExp() {
    return exp;
  }

  public void setExp(Integer exp) {
    this.exp = exp;
  }
}
