package com.parsroyal.solutiontablet.data.model;

import java.util.List;

/**
 * Created by Mahyar on 7/5/2015.
 */
public class VisitLineDtoList extends BaseModel
{

    private List<VisitLineDto> visitLineDtoList;

    public VisitLineDtoList()
    {
    }

    public VisitLineDtoList(List<VisitLineDto> visitLineDtoList)
    {
        this.visitLineDtoList = visitLineDtoList;
    }

    public List<VisitLineDto> getVisitLineDtoList()
    {
        return visitLineDtoList;
    }

    public void setVisitLineDtoList(List<VisitLineDto> visitLineDtoList)
    {
        this.visitLineDtoList = visitLineDtoList;
    }
}
