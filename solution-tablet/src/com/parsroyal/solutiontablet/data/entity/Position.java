package com.parsroyal.solutiontablet.data.entity;

import android.location.Location;

import com.parsroyal.solutiontablet.constants.SendStatus;

import java.util.Date;

/**
 * @author Arashmidos 2016-09-10
 */
public class Position extends BaseEntity<Long>
{

    public static final String TABLE_NAME = "COMMER_POSITION";
    public static final String COL_ID = "_id";
    public static final String COL_LATITUDE = "LATITUDE";
    public static final String COL_LONGITUDE = "LONGITUDE";
    public static final String COL_SPEED = "SPEED";
    public static final String COL_STATUS = "STATUS";
    public static final String COL_GPS_DATE = "GPS_DATE";
    public static final String COL_GPS_OFF = "GPS_OFF";
    public static final String COL_MODE = "MODE";
    public static final String COL_SALESMAN_ID = "SALESMAN_ID";
    public static final String COL_BACKEND_ID = "BACKEND_ID";

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
            " " + Position.COL_CREATE_DATE_TIME + " TEXT," +
            " " + Position.COL_UPDATE_DATE_TIME + " TEXT" +
            " );";

    private Long id;
    private Double latitude;
    private Double longitude;
    private Float speed;
    private int status;
    private Date date;
    private int gpsOff;
    private int mode;
    private Long salesmanId;
    private Long backendId;

    public Position(Double latitude, Double longitude, Float speed, Date date, int gpsOff, int mode)
    {
        this.latitude = latitude;
        this.longitude = longitude;
        this.speed = speed;
        this.status = SendStatus.NEW.getId().intValue();
        this.date = date;
        this.gpsOff = gpsOff;
        this.mode = mode;
    }

    public Position()
    {
    }

    public Position(Long id)
    {
        this.id = id;
    }

    public Position(Location l)
    {
        this(l.getLatitude(), l.getLongitude(), l.getSpeed(), new Date(l.getTime()), 0, 0);
    }

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public Double getLatitude()
    {
        return latitude;
    }

    public void setLatitude(Double latitude)
    {
        this.latitude = latitude;
    }

    public Double getLongitude()
    {
        return longitude;
    }

    public void setLongitude(Double longitude)
    {
        this.longitude = longitude;
    }

    public Float getSpeed()
    {
        return speed;
    }

    public void setSpeed(Float speed)
    {
        this.speed = speed;
    }

    public int getStatus()
    {
        return status;
    }

    public void setStatus(int status)
    {
        this.status = status;
    }

    public Date getDate()
    {
        return date;
    }

    public void setDate(Date date)
    {
        this.date = date;
    }

    public int getGpsOff()
    {
        return gpsOff;
    }

    public void setGpsOff(int gpsOff)
    {
        this.gpsOff = gpsOff;
    }

    public int getMode()
    {
        return mode;
    }

    public void setMode(int mode)
    {
        this.mode = mode;
    }

    public Long getSalesmanId()
    {
        return salesmanId;
    }

    public void setSalesmanId(Long salesmanId)
    {
        this.salesmanId = salesmanId;
    }

    public Long getBackendId()
    {
        return backendId;
    }

    public void setBackendId(Long backendId)
    {
        this.backendId = backendId;
    }

    @Override
    public Long getPrimaryKey()
    {
        return id;
    }

}
