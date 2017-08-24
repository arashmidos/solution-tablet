package com.parsroyal.solutiontablet.data.dao.impl;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.google.android.gms.maps.model.LatLng;
import com.parsroyal.solutiontablet.data.dao.PositionDao;
import com.parsroyal.solutiontablet.data.entity.Position;
import com.parsroyal.solutiontablet.data.helper.CommerDatabaseHelper;
import com.parsroyal.solutiontablet.data.model.PositionDto;
import com.parsroyal.solutiontablet.util.DateUtil;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by Arash on 11/8/2016.
 */
public class PositionDaoImpl extends AbstractDao<Position, Long> implements PositionDao {

  private Context context;

  public PositionDaoImpl(Context context) {
    this.context = context;
  }

  @Override
  protected Context getContext() {
    return context;
  }

  @Override
  protected ContentValues getContentValues(Position entity) {
    ContentValues contentValues = new ContentValues();
    contentValues.put(Position.COL_ID, entity.getId());
    contentValues.put(Position.COL_LATITUDE, entity.getLatitude());
    contentValues.put(Position.COL_LONGITUDE, entity.getLongitude());
    contentValues.put(Position.COL_SPEED, entity.getSpeed());
    contentValues.put(Position.COL_STATUS, entity.getStatus());
    contentValues.put(Position.COL_GPS_DATE, DateUtil.convertDate(
        entity.getDate(), DateUtil.FULL_FORMATTER_GREGORIAN_WITH_TIME, "EN"));
    contentValues.put(Position.COL_GPS_OFF, entity.getGpsOff());
    contentValues.put(Position.COL_MODE, entity.getMode());
    contentValues.put(Position.COL_SALESMAN_ID, entity.getSalesmanId());
    contentValues.put(Position.COL_BACKEND_ID, entity.getBackendId());
    contentValues.put(Position.COL_CREATE_DATE_TIME, entity.getCreateDateTime());
    contentValues.put(Position.COL_UPDATE_DATE_TIME, entity.getUpdateDateTime());
    contentValues.put(Position.COL_ACCURACY, entity.getAccuracy());
    return contentValues;
  }

  @Override
  protected String getTableName() {
    return Position.TABLE_NAME;
  }

  @Override
  protected String getPrimaryKeyColumnName() {
    return Position.COL_ID;
  }

  @Override
  protected String[] getProjection() {
    String[] projection = {
        Position.COL_ID,
        Position.COL_LATITUDE,
        Position.COL_LONGITUDE,
        Position.COL_SPEED,
        Position.COL_STATUS, //4
        Position.COL_GPS_DATE,
        Position.COL_GPS_OFF,
        Position.COL_MODE,
        Position.COL_SALESMAN_ID,
        Position.COL_BACKEND_ID,
        Position.COL_CREATE_DATE_TIME,//10
        Position.COL_UPDATE_DATE_TIME,
        Position.COL_ACCURACY
    };
    return projection;
  }

  @Override
  protected Position createEntityFromCursor(Cursor cursor) {
    Position position = new Position();

    position.setId(cursor.getLong(0));
    position.setLatitude(cursor.getDouble(1));
    position.setLongitude(cursor.getDouble(2));
    position.setSpeed(cursor.getFloat(3));
    position.setStatus(cursor.getInt(4));
    position.setDate(DateUtil
        .convertStringToDate(cursor.getString(5), DateUtil.FULL_FORMATTER_GREGORIAN_WITH_TIME,
            "EN"));
    position.setGpsOff(cursor.getInt(6));
    position.setMode(cursor.getInt(7));
    position.setSalesmanId(cursor.getLong(8));
    position.setBackendId(cursor.getLong(9));
    position.setCreateDateTime(cursor.getString(10));
    position.setUpdateDateTime(cursor.getString(11));
    position.setAccuracy(cursor.getFloat(12));
    return position;
  }

  protected PositionDto createDtoFromCursor(Cursor cursor) {
    PositionDto position = new PositionDto();

    position.setId(cursor.getLong(0));
    position.setLatitude(cursor.getDouble(1));
    position.setLongitude(cursor.getDouble(2));
    position.setSpeed(cursor.getFloat(3));
    position.setDate(DateUtil
        .convertStringToDate(cursor.getString(5), DateUtil.FULL_FORMATTER_GREGORIAN_WITH_TIME,
            "EN"));
    position.setGpsOff(cursor.getInt(6));
    position.setMode(cursor.getInt(7));
    position.setPersonId(cursor.getLong(8));
    position.setAccuracy(cursor.getFloat(12));

    return position;
  }

  @Override
  public Position getPositionById(Long positionId) {
    CommerDatabaseHelper databaseHelper = CommerDatabaseHelper.getInstance(getContext());
    SQLiteDatabase db = databaseHelper.getReadableDatabase();
    String selection = getPrimaryKeyColumnName() + " = ?";
    String[] args = {String.valueOf(positionId)};
    Cursor cursor = db.query(getTableName(), getProjection(), selection, args, null, null, null);
    Position position = null;
    if (cursor.moveToFirst()) {
      position = createEntityFromCursor(cursor);
    }
    cursor.close();
    return position;
  }

  @Override
  public List<PositionDto> findPositionDtoByStatusId(Long statusId) {
    CommerDatabaseHelper databaseHelper = CommerDatabaseHelper.getInstance(getContext());
    SQLiteDatabase db = databaseHelper.getReadableDatabase();
    String selection = Position.COL_STATUS + " = ?";
    String[] args = {String.valueOf(statusId)};
    Cursor cursor = db.query(getTableName(), getProjection(), selection, args, null, null, null);

    List<PositionDto> positionList = new ArrayList<>();

    while (cursor.moveToNext()) {
      positionList.add(createDtoFromCursor(cursor));
    }

    cursor.close();
    return positionList;
  }

  @Override
  public List<Position> findPositionByDate(Date from, Date to) {
    CommerDatabaseHelper databaseHelper = CommerDatabaseHelper.getInstance(getContext());
    SQLiteDatabase db = databaseHelper.getReadableDatabase();
    String selection = " 1 = ? ";//Position.COL_GPS_DATE + " = ? ";
    String[] args = {"1"};
    Cursor cursor = db.query(getTableName(), getProjection(), selection, args, null, null, null);

    List<Position> positionList = new ArrayList<>();
    while (cursor.moveToNext()) {
      positionList.add(createEntityFromCursor(cursor));
    }
    cursor.close();
    return positionList;
  }

  @Override
  public List<LatLng> findPositionLatLngByDate(Date from, Date to) {
    CommerDatabaseHelper databaseHelper = CommerDatabaseHelper.getInstance(getContext());
    SQLiteDatabase db = databaseHelper.getReadableDatabase();
    String selection = Position.COL_CREATE_DATE_TIME + " between ? and ? ";
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd - HH:mm:ss", Locale.US);

    String[] args = {sdf.format(from), sdf.format(to)};
    String orderBy = Position.COL_CREATE_DATE_TIME;
    Cursor cursor = db.query(getTableName(), getProjection(), selection, args, null, null, orderBy);

    List<LatLng> positionList = new ArrayList<>();
    while (cursor.moveToNext()) {
      positionList.add(new LatLng(cursor.getDouble(1), cursor.getDouble(2)));
    }
    cursor.close();
    return positionList;
  }

  @Override
  public List<LatLng> findPositionLatLng() {
    Date to = DateUtil.endOfDay(new Date());
    Date from = DateUtil.startOfDay(to);

    return findPositionLatLngByDate(from, to);
  }

  @Override
  public Position getLastPosition() {
    List<Position> positionList = retrieveAll(null, null, null, null, Position.COL_ID + " DESC ",
        "1");
    if (positionList.size() == 1) {
      return positionList.get(0);
    }
    return null;
  }
}
