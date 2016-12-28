package com.conta.comer.data.model;

import java.util.List;

/**
 * Created by mahyarsefidi1 on 9/20/16.
 * //Key Performance Indicator
 */
public class KPIDto extends BaseModel
{
    private List<KPIDetail> details;
    private Double kpiGauge;

    public List<KPIDetail> getDetails()
    {
        return details;
    }

    public void setDetails(List<KPIDetail> details)
    {
        this.details = details;
    }

    public Double getKpiGauge()
    {
        return kpiGauge;
    }

    public void setKpiGauge(Double kpiGauge)
    {
        this.kpiGauge = kpiGauge;
    }
}
