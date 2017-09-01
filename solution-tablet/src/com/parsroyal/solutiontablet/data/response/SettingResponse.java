package com.parsroyal.solutiontablet.data.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

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
