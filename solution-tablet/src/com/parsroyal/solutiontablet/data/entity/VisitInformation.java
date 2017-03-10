package com.parsroyal.solutiontablet.data.entity;

import com.parsroyal.solutiontablet.util.Empty;

/**
 * Created by Mahyar on 7/16/2015.
 */
public class VisitInformation extends BaseEntity<Long>
{
    public static final String TABLE_NAME = "COMMER_VISIT_INFORMATION";
    public static final String COL_ID = "_id";
    public static final String COL_VISIT_DATE = "VISIT_DATE_TIME";
    public static final String COL_VISIT_BACKEND_ID = "VISIT_BACKEND_ID";
    public static final String COL_START_TIME = "START_TIME";
    public static final String COL_END_TIME = "END_TIME";
    public static final String COL_X_LOCATION = "X_LOCATION";
    public static final String COL_Y_LOCATION = "Y_LOCATION";
    public static final String COL_CUSTOMER_BACKEND_ID = "CUSTOMER_BACKEND_ID";
    public static final String COL_RESULT = "RESULT";
    public static final String COL_CUSTOMER_ID = "CUSTOMER_ID";
    public static final String CREATE_TABLE_SQL = "CREATE TABLE " + VisitInformation.TABLE_NAME + " (" +
            " " + VisitInformation.COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            " " + VisitInformation.COL_VISIT_DATE + " TEXT," +
            " " + VisitInformation.COL_START_TIME + " TEXT," +
            " " + VisitInformation.COL_VISIT_BACKEND_ID + " INTEGER," +
            " " + VisitInformation.COL_END_TIME + " TEXT," +
            " " + VisitInformation.COL_X_LOCATION + " REAL," +
            " " + VisitInformation.COL_Y_LOCATION + " REAL," +
            " " + VisitInformation.COL_CUSTOMER_BACKEND_ID + " INTEGER," +
            " " + VisitInformation.COL_RESULT + " INTEGER," +
            " " + VisitInformation.COL_CREATE_DATE_TIME + " TEXT," +
            " " + VisitInformation.COL_UPDATE_DATE_TIME + " TEXT," +
            " " + VisitInformation.COL_CUSTOMER_ID + " INTEGER" +
            " );";
    private Long id;
    private String visitDate;
    private String startTime;
    private String endTime;
    private Double xLocation;
    private Double yLocation;
    private Long customerBackendId;
    private Long result;
    private Long visitBackendId;
    private Long customerId;

    public Long getCustomerId()
    {
        return customerId;
    }

    public void setCustomerId(Long customerId)
    {
        this.customerId = customerId;
    }

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public String getVisitDate()
    {
        return visitDate;
    }

    public void setVisitDate(String visitDate)
    {
        this.visitDate = visitDate;
    }

    public String getStartTime()
    {
        return startTime;
    }

    public void setStartTime(String startTime)
    {
        this.startTime = startTime;
    }

    public String getEndTime()
    {
        return endTime;
    }

    public void setEndTime(String endTime)
    {
        this.endTime = endTime;
    }

    public Double getxLocation()
    {
        return xLocation;
    }

    public void setxLocation(Double xLocation)
    {
        this.xLocation = xLocation;
    }

    public Double getyLocation()
    {
        return yLocation;
    }

    public void setyLocation(Double yLocation)
    {
        this.yLocation = yLocation;
    }

    public Long getCustomerBackendId()
    {
        return customerBackendId;
    }

    public void setCustomerBackendId(Long customerBackendId)
    {
        this.customerBackendId = customerBackendId;
    }

    public Long getResult()
    {
        return result;
    }

    public void setResult(Long result)
    {
        this.result = result;
    }

    public Long getVisitBackendId()
    {
        return visitBackendId;
    }

    public void setVisitBackendId(Long visitBackendId)
    {
        this.visitBackendId = visitBackendId;
    }

    public String getString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append(id);
        sb.append("&");
        sb.append(customerBackendId);
        sb.append("&");
        sb.append(Empty.isEmpty(result) ? "NULL" : result);
        sb.append("&");
        sb.append(Empty.isEmpty(visitDate) ? "NULL" : visitDate);
        sb.append("&");
        sb.append(Empty.isEmpty(startTime) ? "NULL" : startTime);
        sb.append("&");
        sb.append(Empty.isEmpty(endTime) ? "NULL" : endTime);
        sb.append("&");
        sb.append(Empty.isEmpty(xLocation) ? "NULL" : xLocation);
        sb.append("&");
        sb.append(Empty.isEmpty(yLocation) ? "NULL" : yLocation);
        return sb.toString();
    }

    @Override
    public Long getPrimaryKey()
    {
        return id;
    }


}
