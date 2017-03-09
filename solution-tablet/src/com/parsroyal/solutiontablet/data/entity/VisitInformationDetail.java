package com.parsroyal.solutiontablet.data.entity;

import com.parsroyal.solutiontablet.constants.VisitInformationDetailType;

/**
 * Created by mahyar on 3/5/17.
 */
public class VisitInformationDetail extends BaseEntity<Long>
{
    public static final String TABLE_NAME = "COMMER_VISIT_INFORMATION_DETAIL";
    public static final String COL_ID = "_id";
    public static final String COL_TYPE = "TYPE";
    public static final String COL_TYPE_ID = "TYPE_ID";
    public static final String COL_EXTRA_DATA = "EXTRA_DATA";
    public static final String COL_VISIT_INFORMATION_ID = "VISIT_INFORMATION_ID";
    public static final String CREATE_TABLE_SQL = "CREATE TABLE " + VisitInformationDetail.TABLE_NAME + " (" +
            " " + VisitInformationDetail.COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            " " + VisitInformationDetail.COL_TYPE + " INTEGER," +
            " " + VisitInformationDetail.COL_TYPE_ID + " INTEGER," +
            " " + VisitInformationDetail.COL_EXTRA_DATA + " TEXT," +
            " " + VisitInformationDetail.COL_VISIT_INFORMATION_ID + " INTEGER," +
            " " + VisitInformationDetail.COL_CREATE_DATE_TIME + " TEXT," +
            " " + VisitInformationDetail.COL_UPDATE_DATE_TIME + " TEXT" +
            " );";
    private Long id;
    private long type;
    private long typeId;
    private String extraData;
    private long visitInformationId;

    public VisitInformationDetail(long visitId, VisitInformationDetailType type, long typeId)
    {
        this.visitInformationId = visitId;
        this.type = type.getValue();
        this.typeId = typeId;
    }

    public VisitInformationDetail()
    {

    }

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public long getType()
    {
        return type;
    }

    public void setType(long type)
    {
        this.type = type;
    }

    public String getExtraData()
    {
        return extraData;
    }

    public void setExtraData(String extraData)
    {
        this.extraData = extraData;
    }

    public long getVisitInformationId()
    {
        return visitInformationId;
    }

    public void setVisitInformationId(long visitInformationId)
    {
        this.visitInformationId = visitInformationId;
    }

    public long getTypeId()
    {
        return typeId;
    }

    public void setTypeId(long typeId)
    {
        this.typeId = typeId;
    }

    @Override
    public Long getPrimaryKey()
    {
        return id;
    }
}
