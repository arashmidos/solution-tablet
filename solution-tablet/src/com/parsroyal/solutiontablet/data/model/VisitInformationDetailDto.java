package com.parsroyal.solutiontablet.data.model;

import com.parsroyal.solutiontablet.constants.VisitInformationDetailType;

/**
 * Created by mahyar on 3/5/17.
 */
public class VisitInformationDetailDto
{
    private long id;
    private VisitInformationDetailType type;
    private long typeId;
    private String extraData;
    private long customerVisitId;

    public long getId()
    {
        return id;
    }

    public void setId(long id)
    {
        this.id = id;
    }

    public VisitInformationDetailType getType()
    {
        return type;
    }

    public void setType(VisitInformationDetailType type)
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

    public long getCustomerVisitId()
    {
        return customerVisitId;
    }

    public void setCustomerVisitId(long customerVisitId)
    {
        this.customerVisitId = customerVisitId;
    }

    public long getTypeId()
    {
        return typeId;
    }

    public void setTypeId(long typeId)
    {
        this.typeId = typeId;
    }
}
