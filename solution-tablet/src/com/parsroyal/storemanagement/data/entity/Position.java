package com.parsroyal.storemanagement.data.entity;

import android.location.Location;
import com.parsroyal.storemanagement.BuildConfig;
import com.parsroyal.storemanagement.SolutionTabletApplication;
import com.parsroyal.storemanagement.constants.SendStatus;
import com.parsroyal.storemanagement.util.DateUtil;
import com.parsroyal.storemanagement.util.GPSUtil;
import com.parsroyal.storemanagement.util.NumberUtil;
import java.util.Date;

/**
 * @author Arashmidos 2016-09-10
 */
public class Position extends BaseEntity<Long> {

  public static final String TABLE_NAME = "COMMER_POSITION";
  public static final String COL_ID = "_id";
  public static final String COL_LATITUDE = "LATITUDE";
  public static final String COL_LONGITUDE = "LONGITUDE";
  public static final String COL_SPEED = "SPEED";
  public static final String COL_ACCURACY = "ACCURACY";
  public static final String COL_STATUS = "STATUS";
  public static final String COL_GPS_DATE = "GPS_DATE";
  public static final String COL_GPS_OFF = "GPS_OFF";
  public static final String COL_MODE = "MODE";
  public static final String COL_SALESMAN_ID = "SALESMAN_ID";
  public static final String COL_BACKEND_ID = "BACKEND_ID";
  public static final String COL_MOCK_LOCATION = "MOCK_LOCATION";
  public static final String COL_ROOTED = "ROOTED";
  public static final String COL_BATTERY_LEVEL = "BATTERY_LEVEL";
  public static final String COL_BATTERY_STATUS = "BATTERY_STATUS";
  public static final String COL_DISTANCE = "DISTANCE";
  public static final String COL_NETWORK_DATE = "NETWORK_DATE";
  public static final String COL_IMEI = "IMEI";

  public static final String CREATE_TABLE_SCRIPT = "CREATE TABLE " + Position.TABLE_NAME + " (" +
      " " + Position.COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
      " " + Position.COL_LATITUDE + " REAL," +
      " " + Position.COL_LONGITUDE + " REAL," +
      " " + Position.COL_SPEED + " REAL," +
      " " + Position.COL_STATUS + " INTEGER," +
      " " + Position.COL_GPS_DATE + " TEXT," +
      " " + Position.COL_GPS_OFF + " INTEGER," +
      " " + Position.COL_MODE + " INTEGER," +
      " " + Position.COL_SALESMAN_ID + " INTEGER," +
      " " + Position.COL_BACKEND_ID + " INTEGER," +
      " " + Position.COL_ACCURACY + " REAL," +//10
      " " + Position.COL_CREATE_DATE_TIME + " TEXT," +
      " " + Position.COL_UPDATE_DATE_TIME + " TEXT," +
      " " + Position.COL_MOCK_LOCATION + " INTEGER," +
      " " + Position.COL_ROOTED + " INTEGER," +
      " " + Position.COL_BATTERY_LEVEL + " INTEGER," +
      " " + Position.COL_BATTERY_STATUS + " TEXT," +
      " " + Position.COL_DISTANCE + " REAL," +
      " " + Position.COL_NETWORK_DATE + " INTEGER," +//18
      " " + Position.COL_IMEI + " TEXT" +//19
      " );";

  private Long id;
  private Double latitude;
  private Double longitude;
  private Float speed;
  private int status;
  private String date;
  private int gpsOff;
  private int mode;
  private Long salesmanId;
  private Long backendId;
  private Float accuracy;
  private int mockLocation;
  private boolean rooted;
  private int batteryLevel;
  private String batteryStatus;
  private Double distanceInMeter;
  private Long networkDate;
  private String imei;

  public Position(Double latitude, Double longitude, Float speed, int gpsOff, int mode,
      Float accuracy, long gpsTime) {
    this();
    this.latitude = latitude;
    this.longitude = longitude;
    this.speed = speed;
    Date gpsDate = new Date(gpsTime);
    if (DateUtil.isTooOld(gpsDate)) {
      gpsDate = new Date();
    }
    this.createDateTime = DateUtil
        .convertDate(gpsDate, DateUtil.FULL_FORMATTER_GREGORIAN_WITH_TIME, "EN");
    this.gpsOff = gpsOff;
    this.accuracy = accuracy;
  }

  public Position() {
    this.status = SendStatus.NEW.getId().intValue();
    this.createDateTime = DateUtil
        .convertDate(new Date(), DateUtil.FULL_FORMATTER_GREGORIAN_WITH_TIME, "EN");
    this.date = DateUtil
        .convertDate(new Date(), DateUtil.FULL_FORMATTER_GREGORIAN_WITH_TIME, "EN");
//    this.batteryLevel = MainActivity.batteryLevel;
//    this.batteryStatus = MainActivity.batteryStatusTitle;
    this.rooted = GPSUtil.isDeviceRooted();
    Date trueTime = SolutionTabletApplication.getInstance().getTrueTime();

    this.networkDate = trueTime == null ? new Date().getTime() : trueTime.getTime();
    this.imei = SolutionTabletApplication.getInstance().getInstanceId();
    this.mode = BuildConfig.VERSION_CODE;
  }

  public Position(Long id) {
    this.id = id;
  }

  public Position(Location l) {
    this(l.getLatitude(), l.getLongitude(), l.getSpeed(), 0, 0, l.getAccuracy(), l.getTime());
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

  public int getStatus() {
    return status;
  }

  public void setStatus(int status) {
    this.status = status;
  }

  public String getDate() {
    return date;
  }

  public void setDate(String date) {
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

  public Long getSalesmanId() {
    return salesmanId;
  }

  public void setSalesmanId(Long salesmanId) {
    this.salesmanId = salesmanId;
  }

  public Long getBackendId() {
    return backendId;
  }

  public void setBackendId(Long backendId) {
    this.backendId = backendId;
  }

  @Override
  public Long getPrimaryKey() {
    return id;
  }

  public Float getAccuracy() {
    return accuracy;
  }

  public void setAccuracy(Float accuracy) {
    this.accuracy = accuracy;
  }

  public Location getLocation() {
    Location location = new Location("Dummy");
    location.setLatitude(latitude == null ? 0.0 : latitude);
    location.setLongitude(longitude == null ? 0.0 : longitude);

    return location;
  }

  public int getMockLocation() {
    return mockLocation;
  }

  public void setMockLocation(int mockLocation) {
    this.mockLocation = mockLocation;
  }

  public boolean isRooted() {
    return rooted;
  }

  public void setRooted(boolean rooted) {
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

  public Double getDistanceInMeter() {
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

  public String getImei() {
    return imei;
  }

  public void setImei(String imei) {
    this.imei = imei;
  }

  @Override
  public String toString() {
    return "Position{" +
        "id=" + id +
        ", latitude=" + latitude +
        ", longitude=" + longitude +
        ", speed=" + speed +
        ", status=" + status +
        ", date='" + date + '\'' +
        ", gpsOff=" + gpsOff +
        ", mode=" + mode +
        ", salesmanId=" + salesmanId +
        ", backendId=" + backendId +
        ", accuracy=" + accuracy +
        ", mockLocation=" + mockLocation +
        ", rooted=" + rooted +
        ", batteryLevel=" + batteryLevel +
        ", batteryStatus='" + batteryStatus + '\'' +
        ", distanceInMeter=" + distanceInMeter +
        ", networkDate=" + NumberUtil.digitsToEnglish(DateUtil.getZonedDate(new Date(networkDate)))
        +
        ", imei='" + imei + '\'' +
        '}';
  }
}
