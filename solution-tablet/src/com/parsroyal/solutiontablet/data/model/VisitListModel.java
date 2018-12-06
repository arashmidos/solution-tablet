package com.parsroyal.solutiontablet.data.model;

import com.parsroyal.solutiontablet.util.DateUtil;
import com.parsroyal.solutiontablet.util.NumberUtil;
import java.util.Date;

/**
 * Created by Arash on 6/12/2018
 */
public class VisitListModel extends BaseModel {

  private Long id;
  private String visitDate;
  private String startTime;
  private String endTime;
  private String customer;
  private String networkDate;
  private String endNetworkDate;
  private Long distance;
  private boolean phoneVisit;
  private boolean sent;

  public String getCustomer() {
    return customer;
  }

  public void setCustomer(String customer) {
    this.customer = customer;
  }

  public boolean isPhoneVisit() {
    return phoneVisit;
  }

  public void setPhoneVisit(boolean phoneVisit) {
    this.phoneVisit = phoneVisit;
  }

  public String getEndNetworkDate() {
    return endNetworkDate;
  }

  public void setEndNetworkDate(String endNetworkDate) {
    this.endNetworkDate = endNetworkDate;
  }

  public Long getDistance() {
    return distance;
  }

  public void setDistance(Long distance) {
    this.distance = distance;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getVisitDate() {
    return visitDate;
  }

  public void setVisitDate(String visitDate) {
    this.visitDate = visitDate;
  }

  public String getStartTime() {
    return startTime;
  }

  public void setStartTime(String startTime) {
    this.startTime = startTime;
  }

  public String getEndTime() {
    return endTime;
  }

  public void setEndTime(String endTime) {
    this.endTime = endTime;
  }

  public String getNetworkDate() {
    return networkDate;
  }

  public void setNetworkDate(String networkDate) {
    this.networkDate = networkDate;
  }

  public boolean isSent() {
    return sent;
  }

  public void setSent(boolean sent) {
    this.sent = sent;
  }

  public int getPeriod() {

    Date date1 = DateUtil.convertStringToDateTime(startTime, DateUtil.TIME_24, "EN");
    Date date2 = DateUtil.convertStringToDateTime(endTime, DateUtil.TIME_24, "EN");
    return DateUtil.compareDatesInMinutes(date2, date1);
  }

  public String getFormattedTime() {
    try {
      String start = startTime.substring(0, startTime.lastIndexOf(":"));
      String end = endTime.substring(0, endTime.lastIndexOf(":"));

      if (start.startsWith("0")) {
        start = start.substring(1);
      }
      if (end.startsWith("0")) {
        end = end.substring(1);
      }
      return NumberUtil.digitsToPersian(String.format("%s الی %s (%s)", start, end, getPeriod()));
    } catch (Exception ex) {
      ex.printStackTrace();
    }
    return "--";
  }
}
