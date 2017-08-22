package com.parsroyal.solutiontablet.data.model;

import com.parsroyal.solutiontablet.constants.SaleType;
import java.util.Date;

/**
 * Created by Arash 2017-08-22
 */
public class PositionDto extends BaseModel {

  private Long id;
  private Double latitude;
  private Double longitude;
  private Float speed;
  private Date date;
  private int gpsOff;
  private int mode;
  private Long personId;
  private Float accuracy;
  private SaleType saleType;

  public PositionDto() {
  }

  public PositionDto(Long id, Double latitude, Double longitude, Float speed, Date date, int gpsOff,
      int mode, Long personId, Float accuracy,
      SaleType saleType) {
    this.id = id;
    this.latitude = latitude;
    this.longitude = longitude;
    this.speed = speed;
    this.date = date;
    this.gpsOff = gpsOff;
    this.mode = mode;
    this.personId = personId;
    this.accuracy = accuracy;
    this.saleType = saleType;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Double getLatitude() {
    return latitude;
  }

  public void setLatitude(Double latitude) {
    this.latitude = latitude;
  }

  public Double getLongitude() {
    return longitude;
  }

  public void setLongitude(Double longitude) {
    this.longitude = longitude;
  }

  public Float getSpeed() {
    return speed;
  }

  public void setSpeed(Float speed) {
    this.speed = speed;
  }

  public Date getDate() {
    return date;
  }

  public void setDate(Date date) {
    this.date = date;
  }

  public int getGpsOff() {
    return gpsOff;
  }

  public void setGpsOff(int gpsOff) {
    this.gpsOff = gpsOff;
  }

  public int getMode() {
    return mode;
  }

  public void setMode(int mode) {
    this.mode = mode;
  }

  public Long getPersonId() {
    return personId;
  }

  public void setPersonId(Long personId) {
    this.personId = personId;
  }

  public Float getAccuracy() {
    return accuracy;
  }

  public void setAccuracy(Float accuracy) {
    this.accuracy = accuracy;
  }

  public SaleType getSaleType() {
    return saleType;
  }

  public void setSaleType(SaleType saleType) {
    this.saleType = saleType;
  }
}
