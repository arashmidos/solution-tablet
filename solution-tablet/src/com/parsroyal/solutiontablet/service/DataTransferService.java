package com.parsroyal.solutiontablet.service;

import com.parsroyal.solutiontablet.data.model.GoodsDtoList;
import com.parsroyal.solutiontablet.ui.observer.ResultObserver;

/**
 * Created by Mahyar on 6/15/2015.
 * Edited by Arash on 7/11/2016
 */
public interface DataTransferService {

  void getAllData(ResultObserver observer);

  void sendAllData(ResultObserver resultObserver);

  boolean isDataTransferPossible();

  GoodsDtoList getRejectedData(ResultObserver resultObserver, Long customerId);
}
