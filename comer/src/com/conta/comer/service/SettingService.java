package com.conta.comer.service;

import com.conta.comer.ui.observer.ResultObserver;

/**
 * Created by Mahyar on 6/4/2015.
 */
public interface SettingService
{

    void saveSetting(String settingKey, String settingValue);

    String getSettingValue(String key);

    void getUserInformation(ResultObserver observer);
}
