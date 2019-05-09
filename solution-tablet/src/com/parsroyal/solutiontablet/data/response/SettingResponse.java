package com.parsroyal.solutiontablet.data.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.util.SortedSet;

/**
 * Created by Arash on 2017-08-31
 */

public class SettingResponse extends Response {

  @SerializedName("token")
  @Expose
  private String token;
  @SerializedName("settings")
  @Expose
  private SettingDetailsResponse settings;
  @SerializedName("authorities")
  @Expose
  private SortedSet<String> authorities;

  public SortedSet<String> getAuthorities() {
    return authorities;
  }

  public void setAuthorities(SortedSet<String> authorities) {
    this.authorities = authorities;
  }

  public String getToken() {
    return token;
  }

  public void setToken(String token) {
    this.token = token;
  }

  public SettingDetailsResponse getSettings() {
    return settings;
  }

  public void setSettings(SettingDetailsResponse settings) {
    this.settings = settings;
  }
}
