package com.parsroyal.solutiontablet.data.model;

import com.parsroyal.solutiontablet.constants.SaleType;

/**
 * Created by Arash 2017-08-22
 */
public class PositionDto extends BaseModel {

  private Long id;
  private Double latitude;
  private Double longitude;
  private Float speed;
  private Long date;
  private int gpsOff;
  private int mode;
  private Long personId;
  private Float accuracy;
  private SaleType saleType;
  private int mockLocation;
  private int rooted;
  private int batteryLevel;
  private String batteryStatus;
  private Long createDateTime;
  private double distanceInMeter;
  private Long networkDate;
  private String imei;

  public PositionDto() {
  }

  public String getImei() {
    return imei;
  }

  public void setImei(String imei) {
    this.imei = imei;
  }

  public int getRooted() {
    return rooted;
  }

  public void setRooted(int rooted) {
    this.rooted = rooted;
  }

  public int getBatteryLevel() {
    return batteryLevel;
  }

  public void setBatteryLevel(int batteryLevel) {
    this.batteryLevel = batteryLevel;
  }

  public String getBatteryStatus() {
    return batteryStatus;
  }

  public void setBatteryStatus(String batteryStatus) {
    this.batteryStatus = batteryStatus;
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

  public Long getDate() {
    return date;
  }

  public void setDate(Long date) {
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

  public int getMockLocation() {
    return mockLocation;
  }

  public void setMockLocation(int mockLocation) {
    this.mockLocation = mockLocation;
  }

  public Long getCreateDateTime() {
    return createDateTime;
  }

  public void setCreateDateTime(Long createDateTime) {
    this.createDateTime = createDateTime;
  }

  public double getDistanceInMeter() {
    return distanceInMeter;
  }

  public void setDistanceInMeter(double distanceInMeter) {
    this.distanceInMeter = distanceInMeter;
  }

  public Long getNetworkDate() {
    return networkDate;
  }

  public void setNetworkDate(Long networkDate) {
    this.networkDate = networkDate;
  }
}
