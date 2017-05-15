package com.parsroyal.solutiontablet.data.model;

/**
 * Created by mahyarsefidi1 on 9/20/16.
 */
public class KPIDetail extends BaseModel {

  private String code;
  private String description;
  private Double value;

  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public Double getValue() {
    return value;
  }

  public void setValue(Double value) {
    this.value = value;
  }
}
