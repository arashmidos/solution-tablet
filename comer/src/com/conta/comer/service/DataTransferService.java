package com.conta.comer.service;

import com.conta.comer.data.entity.Position;
import com.conta.comer.data.model.GoodsDtoList;
import com.conta.comer.ui.fragment.VisitDetailFragment;
import com.conta.comer.ui.observer.ResultObserver;

/**
 * Created by Mahyar on 6/15/2015.
 * Edited by Arash on 7/11/2016
 */
public interface DataTransferService
{
    void getAllData(ResultObserver observer);

    void sendAllData(ResultObserver resultObserver);

    boolean isDataTransferPossible();

    GoodsDtoList getRejectedData(ResultObserver resultObserver, Long customerId);
}
