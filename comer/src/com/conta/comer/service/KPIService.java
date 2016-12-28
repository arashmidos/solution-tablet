package com.conta.comer.service;

import com.conta.comer.data.model.KPIDto;
import com.conta.comer.ui.observer.ResultObserver;

/**
 * Created by Arash on 2016-09-21.
 */
public interface KPIService
{
    KPIDto getCustomerKPI(long customerBackendId, ResultObserver observer);
    KPIDto getSalesmanKPI(ResultObserver observer);
}
