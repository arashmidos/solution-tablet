package com.conta.comer.data.dao;

import com.conta.comer.data.entity.Payment;
import com.conta.comer.data.entity.Position;
import com.conta.comer.data.listmodel.PaymentListModel;
import com.conta.comer.data.searchobject.PaymentSO;
import com.google.android.gms.maps.model.LatLng;

import java.util.Date;
import java.util.List;

/**
 * Created by Arash on 10/9/2016.
 */
public interface PositionDao extends BaseDao<Position, Long>
{
    Position getPositionById(Long positionId);

    List<Position> findPositionByStatusId(Long statusId);

    List<Position> findPositionByDate(Date from,Date to);

    List<LatLng> findPositionLatLngByDate(Date from, Date to);

    List<LatLng> findPositionLatLng();
}
