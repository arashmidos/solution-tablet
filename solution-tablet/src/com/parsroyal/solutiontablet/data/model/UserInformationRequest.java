package com.parsroyal.solutiontablet.data.model;

public class UserInformationRequest extends BaseModel {

  private Integer code;
  private Integer type;

  public UserInformationRequest(String code, String type) {
    this.code = Integer.valueOf(code);
    this.type = Integer.valueOf(type);
  }

  public Integer getCode() {
    return code;
  }

  public void setCode(Integer code) {
    this.code = code;
  }

  public Integer getType() {
    return type;
  }

  public void setType(Integer type) {
    this.type = type;
  }
}
