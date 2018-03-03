package com.parsroyal.solutiontablet.data.event;

import com.parsroyal.solutiontablet.data.model.KPIDetail;
import java.util.List;

public class KpiEvent extends Event {

  protected List<KPIDetail> detailList;

  public KpiEvent(List<KPIDetail> detailList) {
    this.detailList = detailList;
  }

  public List<KPIDetail> getDetailList() {

    return detailList;
  }

  public void setDetailList(List<KPIDetail> detailList) {
    this.detailList = detailList;
  }
}
