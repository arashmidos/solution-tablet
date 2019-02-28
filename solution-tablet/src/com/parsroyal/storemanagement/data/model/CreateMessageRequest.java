package com.parsroyal.storemanagement.data.model;

public class CreateMessageRequest {

  private long sender;
  private int receiverType;
  private int pushType;
  private String pushData;
  private int replyTo;

  public CreateMessageRequest(long sender, int receiverType, int pushType, String pushData,
      int replyTo) {
    this.sender = sender;
    this.receiverType = receiverType;
    this.pushType = pushType;
    this.pushData = pushData;
    this.replyTo = replyTo;
  }

  public long getSender() {
    return sender;
  }

  public void setSender(long sender) {
    this.sender = sender;
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

  public int getReplyTo() {
    return replyTo;
  }

  public void setReplyTo(int replyTo) {
    this.replyTo = replyTo;
  }
}
