package com.parsroyal.solutiontablet.ui.formatter;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.parsroyal.solutiontablet.data.model.KPIDetail;
import com.parsroyal.solutiontablet.util.Empty;
import java.text.DecimalFormat;
import java.util.List;

/**
 * Created by Arashmidos on 2017-01-23.
 */
public class XAxisValueFormatter implements IAxisValueFormatter {

  private final List<KPIDetail> kpiDetails;
  private DecimalFormat mFormat;

  public XAxisValueFormatter(List<KPIDetail> kpiDetails) {
    this.kpiDetails = kpiDetails;
  }

  @Override
  public String getFormattedValue(float value, AxisBase axis) {

    String description = kpiDetails.get((int) value).getDescription();
    return (Empty.isNotEmpty(description) ? description : "--");
  }
}
