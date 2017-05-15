package com.parsroyal.solutiontablet.service;

import com.parsroyal.solutiontablet.data.model.KPIDto;
import com.parsroyal.solutiontablet.ui.observer.ResultObserver;

/**
 * Created by Arash on 2016-09-21.
 */
public interface KPIService {

  KPIDto getCustomerKPI(long customerBackendId, ResultObserver observer);

  KPIDto getSalesmanKPI(ResultObserver observer);
}
