package com.parsroyal.solutiontablet.service.impl;

import static com.parsroyal.solutiontablet.service.LocationUpdatesService.EXTRA_POSITION;

import android.content.Context;
import android.content.Intent;
import com.google.android.gms.maps.model.LatLng;
import com.parsroyal.solutiontablet.data.dao.PositionDao;
import com.parsroyal.solutiontablet.data.dao.impl.PositionDaoImpl;
import com.parsroyal.solutiontablet.data.entity.Position;
import com.parsroyal.solutiontablet.data.model.PositionDto;
import com.parsroyal.solutiontablet.service.PositionService;
import com.parsroyal.solutiontablet.service.SaveLocationService;
import com.parsroyal.solutiontablet.util.DateUtil;
import com.parsroyal.solutiontablet.util.Empty;
import java.util.Date;
import java.util.List;

/**
 * Edited by Arash on 8/19/2016
 */
public class PositionServiceImpl implements PositionService {

  private Context context;
  private PositionDao positionDao;

  public PositionServiceImpl(Context context) {
    this.context = context;
    this.positionDao = new PositionDaoImpl(context);
  }

  @Override
  public Position getPositionById(Long positionId) {
    return positionDao.retrieve(positionId);
  }

  @Override
  public Long savePosition(Position position) {
    if (Empty.isEmpty(position.getId())) {
      position.setCreateDateTime(
          DateUtil.convertDate(new Date(), DateUtil.FULL_FORMATTER_GREGORIAN_WITH_TIME, "EN"));
      position.setUpdateDateTime(
          DateUtil.convertDate(new Date(), DateUtil.FULL_FORMATTER_GREGORIAN_WITH_TIME, "EN"));
      return positionDao.create(position);
    } else {
      position.setUpdateDateTime(
          DateUtil.convertDate(new Date(), DateUtil.FULL_FORMATTER_GREGORIAN_WITH_TIME, "EN"));
      positionDao.update(position);
      return position.getId();
    }
  }

  @Override
  public void updatePosition(Position position) {
    positionDao.update(position);
  }

  @Override
  public List<PositionDto> getAllPositionDtoByStatus(Long status) {
    return positionDao.findPositionDtoByStatusId(status);
  }

  @Override
  public List<Position> getAllPositionByDate(Date from, Date to) {
    return positionDao.findPositionByDate(from, to);
  }

  @Override
  public List<LatLng> getAllPositionLatLngByDate(Date from, Date to) {
    return positionDao.findPositionLatLngByDate(from, to);
  }

  @Override
  public List<LatLng> getAllPositionLatLng() {
    return positionDao.findPositionLatLng();
  }

  @Override
  public Position getLastPosition() {
    return positionDao.getLastPosition();
  }

  @Override
  public void sendGpsChangedPosition(GpsStatus gpsStatus) {
    Position position = positionDao.getLastPosition();
    position.setId(null);
    position.setGpsOff(gpsStatus.equals(GpsStatus.OFF) ? 1 : 0);
    Intent intent = new Intent(context, SaveLocationService.class);
    intent.putExtra(EXTRA_POSITION, position);

    context.startService(intent);

  }

  @Override
  public void deleteAll() {
    positionDao.deleteAll();
  }

  public enum GpsStatus {
    OFF, ON
  }
}
