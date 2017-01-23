package com.conta.comer.ui.formatter;

import com.conta.comer.data.model.KPIDetail;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import java.text.DecimalFormat;
import java.util.List;

/**
 * Created by Arashmidos on 2017-01-23.
 */
public class XAxisValueFormatter implements IAxisValueFormatter
{
    private final List<KPIDetail> kpiDetails;
    private DecimalFormat mFormat;

    public XAxisValueFormatter(List<KPIDetail> kpiDetails)
    {
        this.kpiDetails = kpiDetails;
    }

    @Override
    public String getFormattedValue(float value, AxisBase axis)
    {
        return kpiDetails.get((int) value).getCode();
    }
}
