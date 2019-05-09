package com.parsroyal.solutiontablet.constants;

/**
 * Created by Arash on 2016-08-13
 */
public enum SendStatus {
  NEW(9999L, "NEW"),
  UPDATED(9998L, "UPDATED"),
  SENT(9997L, "SENT");

  private Long id;
  private String title;

  SendStatus(Long id, String title) {
    this.id = id;
    this.title = title;
  }

  private static SendStatus findById(Long statusId) {
    for (SendStatus sendStatus : SendStatus.values()) {
      if (sendStatus.getId().equals(statusId)) {
        return sendStatus;
      }
    }
    return null;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getStringId() {
    return String.valueOf(id);
  }
}
