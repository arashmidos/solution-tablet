package com.parsroyal.storemanagement.service.impl;

import com.parsroyal.storemanagement.biz.KeyValueBiz;
import com.parsroyal.storemanagement.biz.impl.KeyValueBizImpl;
import com.parsroyal.storemanagement.data.dao.KeyValueDao;
import com.parsroyal.storemanagement.data.dao.impl.KeyValueDaoImpl;
import com.parsroyal.storemanagement.data.entity.KeyValue;
import com.parsroyal.storemanagement.data.response.CompanyInfoResponse;
import com.parsroyal.storemanagement.data.response.SettingResponse;
import com.parsroyal.storemanagement.data.response.UserInfoDetailsResponse;
import com.parsroyal.storemanagement.data.response.UserInfoResponse;
import com.parsroyal.storemanagement.service.SettingService;
import com.parsroyal.storemanagement.util.Empty;
import com.parsroyal.storemanagement.util.NetworkUtil;
import com.parsroyal.storemanagement.util.PreferenceHelper;
import com.parsroyal.storemanagement.util.constants.ApplicationKeys;

/**
 * Created by Arash on 6/4/2015.
 */
public class SettingServiceImpl implements SettingService {

  private KeyValueBiz keyValueBiz;
  private KeyValueDao keyValueDao;

  public SettingServiceImpl() {
    this.keyValueBiz = new KeyValueBizImpl();
    this.keyValueDao = new KeyValueDaoImpl();
  }

  @Override
  public void saveSetting(String settingKey, String settingValue) {
    keyValueBiz.save(new KeyValue(settingKey, settingValue));
  }

  /**
   * @param key Setting key
   * @return Setting saved locally or null if no setting found
   */
  @Override
  public String getSettingValue(String key) {
    KeyValue keyValue = keyValueBiz.findByKey(key);
    if (Empty.isNotEmpty(keyValue)) {
      return keyValue.getValue();
    }
    return null;
  }

  @Override
  public void saveSetting(SettingResponse response) {
    String token = response.getToken();

    saveUserInfo(NetworkUtil.extractUserInfo(token));

    keyValueBiz.save(new KeyValue(ApplicationKeys.TOKEN, token));

    PreferenceHelper.setAuthorities(response.getAuthorities());
  }

  @Override
  public void saveUserInfo(UserInfoResponse userInfo) {
    UserInfoDetailsResponse userInfoDetailsResponse = userInfo.getDetails();
    keyValueBiz.save(new KeyValue(ApplicationKeys.SALESMAN_ID,
        String.valueOf(userInfoDetailsResponse.getSalesmanId())));
    keyValueBiz.save(new KeyValue(ApplicationKeys.SETTING_USER_CODE,
        String.valueOf(userInfoDetailsResponse.getSalesmanCode())));
    keyValueBiz.save(new KeyValue(ApplicationKeys.USER_FULL_NAME,
        String.valueOf(userInfoDetailsResponse.getSalesmanName()).trim()));
    keyValueBiz.save(new KeyValue(ApplicationKeys.USER_COMPANY_ID,
        String.valueOf(userInfoDetailsResponse.getCompanyId())));
//    keyValueBiz.save(new KeyValue(ApplicationKeys.USER_COMPANY_NAME,
//        String.valueOf(userInfoDetailsResponse.getCompanyName())));
    keyValueBiz.save(new KeyValue(ApplicationKeys.SETTING_STOCK_ID,
        String.valueOf(userInfoDetailsResponse.getStockSerial())));
    keyValueBiz.save(new KeyValue(ApplicationKeys.SETTING_USER_ID,
        String.valueOf(userInfo.getId())));//4155
    keyValueBiz
        .save(new KeyValue(ApplicationKeys.TOKEN_EXPIRE_DATE, String.valueOf(userInfo.getExp())));
  }

  @Override
  public void clearAllSettings() {
    keyValueDao.clearAllKeys();
  }

  @Override
  public void saveSetting(CompanyInfoResponse companyInfo) {
    saveSetting(ApplicationKeys.USER_COMPANY_KEY, companyInfo.getCompanyKey());
    saveSetting(ApplicationKeys.USER_COMPANY_NAME, companyInfo.getCompanyName());
    saveSetting(ApplicationKeys.BACKEND_URI, companyInfo.getBackendUri());

  }
}
