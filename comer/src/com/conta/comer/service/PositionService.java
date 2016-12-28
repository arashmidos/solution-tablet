package com.conta.comer.service;

import com.conta.comer.data.entity.Payment;
import com.conta.comer.data.entity.Position;
import com.conta.comer.data.listmodel.PaymentListModel;
import com.conta.comer.data.searchobject.PaymentSO;
import com.conta.comer.ui.observer.ResultObserver;
import com.google.android.gms.maps.model.LatLng;

import java.util.Date;
import java.util.List;

/**
 * Created by Arash on 09/12/2016
 */
public interface PositionService
{
    Position getPositionById(Long positionId);

    Long savePosition(Position position);

    void updatePosition(Position position);

    List<Position> getAllPositionByStatus(Long status);

    List<Position> getAllPositionByDate(Date from, Date to);

    List<LatLng> getAllPositionLatLngByDate(Date from, Date to);

    void sendPosition(Position position);

    List<LatLng> getAllPositionLatLng();
}
