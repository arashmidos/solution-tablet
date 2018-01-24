package com.parsroyal.solutiontablet.service.impl;

import android.content.Context;
import com.parsroyal.solutiontablet.biz.KeyValueBiz;
import com.parsroyal.solutiontablet.biz.impl.KeyValueBizImpl;
import com.parsroyal.solutiontablet.data.dao.KeyValueDao;
import com.parsroyal.solutiontablet.data.dao.impl.KeyValueDaoImpl;
import com.parsroyal.solutiontablet.data.entity.KeyValue;
import com.parsroyal.solutiontablet.data.response.SettingDetailsResponse;
import com.parsroyal.solutiontablet.data.response.SettingResponse;
import com.parsroyal.solutiontablet.data.response.UserInfoDetailsResponse;
import com.parsroyal.solutiontablet.data.response.UserInfoResponse;
import com.parsroyal.solutiontablet.service.SettingService;
import com.parsroyal.solutiontablet.util.Empty;
import com.parsroyal.solutiontablet.util.constants.ApplicationKeys;

/**
 * Created by Arash on 6/4/2015.
 */
public class SettingServiceImpl implements SettingService {

  private Context context;
  private KeyValueBiz keyValueBiz;
  private KeyValueDao keyValueDao;

  public SettingServiceImpl(Context context) {
    this.context = context;
    this.keyValueBiz = new KeyValueBizImpl(context);
    this.keyValueDao = new KeyValueDaoImpl(context);
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
    keyValueBiz.save(new KeyValue(ApplicationKeys.TOKEN, response.getToken()));
    SettingDetailsResponse settingDetail = response.getSettings();

    keyValueBiz
        .save(new KeyValue(ApplicationKeys.SETTING_STOCK_ID, settingDetail.getStockId()));
    keyValueBiz
        .save(new KeyValue(ApplicationKeys.SETTING_BRANCH_ID, settingDetail.getBranchId()));
    keyValueBiz
        .save(new KeyValue(ApplicationKeys.SETTING_ORDER_TYPE, settingDetail.getOrderType()));
    keyValueBiz
        .save(new KeyValue(ApplicationKeys.SETTING_INVOICE_TYPE, settingDetail.getFactorType()));
    keyValueBiz
        .save(new KeyValue(ApplicationKeys.SETTING_REJECT_TYPE, settingDetail.getRejectType()));
    keyValueBiz.save(new KeyValue(ApplicationKeys.SETTING_SALE_RATE_ENABLE,
        String.valueOf(settingDetail.isUseSaleRate())));
    keyValueBiz.save(new KeyValue(ApplicationKeys.SETTING_CALCULATE_DISTANCE_ENABLE,
        String.valueOf(settingDetail.isCheckDistanceFromCustomer())));
    keyValueBiz.save(new KeyValue(ApplicationKeys.SETTING_CHECK_CREDIT_ENABLE,
        String.valueOf(settingDetail.isCheckCustomerCredit())));
    keyValueBiz.save(new KeyValue(ApplicationKeys.SETTING_DISTANCE_CUSTOMER_VALUE,
        String.valueOf(settingDetail.getCheckDistanceFromCustomerValue())));

    try {
      keyValueBiz
          .save(new KeyValue(ApplicationKeys.SETTING_STOCK_CODE, settingDetail.getStockCode()));
      keyValueBiz
          .save(new KeyValue(ApplicationKeys.SETTING_BRANCH_CODE, settingDetail.getBranchCode()));
    } catch (Exception ignore) {
    }

  }

  @Override
  public void saveUserInfo(UserInfoResponse userInfo) {
    UserInfoDetailsResponse userInfoDetailsResponse = userInfo.getDetails();
    keyValueBiz.save(new KeyValue(ApplicationKeys.SALESMAN_ID,
        String.valueOf(userInfoDetailsResponse.getSalesmanId())));
    keyValueBiz.save(new KeyValue(ApplicationKeys.SETTING_USER_CODE,
        String.valueOf(userInfoDetailsResponse.getSalesmanCode())));
    keyValueBiz.save(new KeyValue(ApplicationKeys.USER_FULL_NAME,
        String.valueOf(userInfoDetailsResponse.getSalesmanName())));
    keyValueBiz.save(new KeyValue(ApplicationKeys.USER_COMPANY_ID,
        String.valueOf(userInfoDetailsResponse.getCompanyId())));
    keyValueBiz.save(new KeyValue(ApplicationKeys.USER_COMPANY_NAME,
        String.valueOf(userInfoDetailsResponse.getCompanyName())));
    keyValueBiz
        .save(new KeyValue(ApplicationKeys.TOKEN_EXPIRE_DATE, String.valueOf(userInfo.getExp())));
  }

  @Override
  public void clearAllSettings() {
    keyValueDao.clearAllKeys();
  }
}
