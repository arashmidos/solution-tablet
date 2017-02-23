package com.parsroyal.solutiontablet.service;

import com.parsroyal.solutiontablet.ui.observer.ResultObserver;

/**
 * Created by Mahyar on 6/4/2015.
 */
public interface SettingService
{

    void saveSetting(String settingKey, String settingValue);

    String getSettingValue(String key);

    void getUserInformation(ResultObserver observer);
}
