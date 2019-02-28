package com.parsroyal.storemanagement.service;

import com.google.android.gms.maps.model.LatLng;
import com.parsroyal.storemanagement.data.entity.Position;
import com.parsroyal.storemanagement.data.model.PositionDto;
import com.parsroyal.storemanagement.service.impl.PositionServiceImpl.GpsStatus;
import java.util.Date;
import java.util.List;

/**
 * Created by Arash on 09/12/2016
 */
public interface PositionService extends BaseService {

  Position getPositionById(Long positionId);

  Long savePosition(Position position);

  void updatePosition(Position position);

  List<PositionDto> getAllPositionDtoByStatus(Long status);

  List<Position> getAllPositionByDate(Date from, Date to);

  List<LatLng> getAllPositionLatLngByDate(Date from, Date to);

  List<LatLng> getAllPositionLatLng();

  Position getLastPosition();

  void sendGpsChangedPosition(GpsStatus gpsStatus);
}
