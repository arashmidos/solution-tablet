package com.parsroyal.solutiontablet.service;

import com.parsroyal.solutiontablet.data.model.GoodsDtoList;
import com.parsroyal.solutiontablet.ui.observer.ResultObserver;

/**
 * Created by Mahyar on 6/15/2015.
 */
public interface DataTransferService {

  void getAllData(ResultObserver observer);

  void sendAllData(ResultObserver resultObserver);

  boolean isDataTransferPossible();

  GoodsDtoList getRejectedData(ResultObserver resultObserver, Long customerId);

  void clearData(int updateType);

}
