package com.parsroyal.storemanagement.service;

import com.parsroyal.storemanagement.data.response.CompanyInfoResponse;
import com.parsroyal.storemanagement.data.response.SettingResponse;
import com.parsroyal.storemanagement.data.response.UserInfoResponse;

/**
 * Created by Arash on 6/4/2015.
 */
public interface SettingService {

  void saveSetting(String settingKey, String settingValue);

  String getSettingValue(String key);

  void saveSetting(SettingResponse response);

  void saveUserInfo(UserInfoResponse userInfo);

  void clearAllSettings();

  void saveSetting(CompanyInfoResponse companyInfo);
}
