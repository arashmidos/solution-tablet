package com.parsroyal.solutiontablet.ui.formatter;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import java.text.DecimalFormat;

/**
 * Created by Arashmidos on 2017-01-23.
 */
public class YAxisValueFormatter implements IAxisValueFormatter {

  private DecimalFormat mFormat;

  public YAxisValueFormatter() {
    mFormat = new DecimalFormat("###,###,###,##0.0");
  }

  @Override
  public String getFormattedValue(float value, AxisBase axis) {
    return mFormat.format(value);
  }
}
