package com.parsroyal.solutiontablet.data.model;

import com.google.gson.annotations.Expose;

import java.io.Serializable;
import java.util.Date;

public class PushNotification implements Serializable {

  private Long id;
  private Long receiver;
  private Long sender;
  private Long replyTo;
  private Long receiverType;
  private Long pushType;
  private String pushData;
  private Long pushDate;
  private String pushTitle;
  private boolean seen;
  private String fullName;

  public PushNotification() {
  }

  public String getFullName() {
    return fullName;
  }

  public void setFullName(String fullName) {
    this.fullName = fullName;
  }

  public Long getReceiverType() {
    return receiverType;
  }

  public void setReceiverType(Long receiverType) {
    this.receiverType = receiverType;
  }

  public Long getPushType() {
    return pushType;
  }

  public void setPushType(Long pushType) {
    this.pushType = pushType;
  }

  public Long getSender() {
    return sender;
  }

  public void setSender(Long sender) {
    this.sender = sender;
  }

  public Long getReplyTo() {
    return replyTo;
  }

  public void setReplyTo(Long replyTo) {
    this.replyTo = replyTo;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Long getReceiver() {
    return receiver;
  }

  public void setReceiver(Long receiver) {
    this.receiver = receiver;
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

  public Date getPushDate() {
    return pushDate == null ? null : new Date(pushDate);
  }

  public void setPushDate(Long pushDate) {
    this.pushDate = pushDate;
  }

  public boolean isSeen() {
    return seen;
  }

  public void setSeen(boolean seen) {
    this.seen = seen;
  }

  @Override
  public String toString() {
    return "PushNotification{" +
        "id=" + id +
        ", receiver=" + receiver +
        ", sender=" + sender +
        ", replyTo=" + replyTo +
        ", receiverType=" + receiverType +
        ", pushType=" + pushType +
        ", pushData='" + pushData + '\'' +
        ", pushDate=" + pushDate +
        ", pushTitle='" + pushTitle + '\'' +
        ", seen=" + seen +
        '}';
  }
}
