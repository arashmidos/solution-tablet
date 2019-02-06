package com.parsroyal.solutiontablet.data.model;

/**
 * Created by arash on 8/31/17.
 */

public class SettingDto {

  private String username;
  private String password;
  private String saleType;

  public SettingDto(String username, String password, String saleType) {
    this.username = username;
    this.password = password;
    this.saleType = saleType;
  }

  public SettingDto(String username, String password) {
    this.username = username;
    this.password = password;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getSaleType() {
    return saleType;
  }

  public void setSaleType(String saleType) {
    this.saleType = saleType;
  }
}
