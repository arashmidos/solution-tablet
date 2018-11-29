package com.parsroyal.solutiontablet.service;

import com.parsroyal.solutiontablet.ui.observer.ResultObserver;

/**
 * Created by Arash on 6/15/2015.
 */
public interface DataTransferService {

  void sendAllData();

  void clearData(int updateType);

  boolean hasUnsentData();
}
