package com.parsroyal.solutiontablet.data.model;

import com.google.gson.annotations.Expose;
import java.util.Date;

/**
 * Created by shkbhbb on 11/24/18.
 */

public class Message {

  @Expose
  private long id;
  @Expose
  private long salesmanId;
  @Expose
  private String pushTitle;
  @Expose
  private String pushData;
  @Expose
  private long pushDate;
  @Expose
  private boolean seen;
  @Expose
  private int pushType;

  public Message(long id, long salesmanId, String pushTitle, String pushData, long pushDate,
      boolean seen, int pushType) {
    this.id = id;
    this.salesmanId = salesmanId;
    this.pushTitle = pushTitle;
    this.pushData = pushData;
    this.pushDate = pushDate;
    this.seen = seen;
    this.pushType = pushType;
  }

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public long getSalesmanId() {
    return salesmanId;
  }

  public void setSalesmanId(long salesmanId) {
    this.salesmanId = salesmanId;
  }

  public String getPushTitle() {
    return pushTitle;
  }

  public void setPushTitle(String pushTitle) {
    this.pushTitle = pushTitle;
  }

  public String getPushData() {
    return pushData;
  }

  public void setPushData(String pushData) {
    this.pushData = pushData;
  }

  public long getPushDate() {
    return pushDate;
  }

  public void setPushDate(long pushDate) {
    this.pushDate = pushDate;
  }

  public boolean isSeen() {
    return seen;
  }

  public void setSeen(boolean seen) {
    this.seen = seen;
  }

  public int getPushType() {
    return pushType;
  }

  public void setPushType(int pushType) {
    this.pushType = pushType;
  }
}
