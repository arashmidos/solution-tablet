package com.parsroyal.solutiontablet.service;

import com.parsroyal.solutiontablet.data.entity.Position;
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

    Position getLastPosition();
}
