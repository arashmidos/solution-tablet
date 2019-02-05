package com.parsroyal.solutiontablet.data.model;

import com.google.gson.annotations.Expose;

/**
 * Created by shkbhbb on 11/24/18.
 */

public class Message {

  @Expose
  private long id;
  @Expose
  private long receiver;
  @Expose
  private long sender;
  @Expose
  private long replyTo;
  @Expose
  private int receiverType;
  @Expose
  private int pushType;
  @Expose
  private String pushData;
  @Expose
  private long pushDate;
  @Expose
  private String pushTitle;
  @Expose
  private boolean seen;
  @Expose
  private String fullName;

  public Message(long id, long receiver, long sender, long replyTo, int receiverType, int pushType,
      String pushData, long pushDate, String pushTitle, boolean seen, String fullName) {
    this.id = id;
    this.receiver = receiver;
    this.sender = sender;
    this.replyTo = replyTo;
    this.receiverType = receiverType;
    this.pushType = pushType;
    this.pushData = pushData;
    this.pushDate = pushDate;
    this.pushTitle = pushTitle;
    this.seen = seen;
    this.fullName = fullName;
  }

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public long getReceiver() {
    return receiver;
  }

  public void setReceiver(long receiver) {
    this.receiver = receiver;
  }

  public long getSender() {
    return sender;
  }

  public void setSender(long sender) {
    this.sender = sender;
  }

  public long getReplyTo() {
    return replyTo;
  }

  public void setReplyTo(long replyTo) {
    this.replyTo = replyTo;
  }

  public int getReceiverType() {
    return receiverType;
  }

  public void setReceiverType(int receiverType) {
    this.receiverType = receiverType;
  }

  public int getPushType() {
    return pushType;
  }

  public void setPushType(int pushType) {
    this.pushType = pushType;
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

  public String getPushTitle() {
    return pushTitle;
  }

  public void setPushTitle(String pushTitle) {
    this.pushTitle = pushTitle;
  }

  public boolean isSeen() {
    return seen;
  }

  public void setSeen(boolean seen) {
    this.seen = seen;
  }

  public String getFullName() {
    return fullName;
  }

  public void setFullName(String fullName) {
    this.fullName = fullName;
  }
}
