package com.parsroyal.storemanagement.constants;

/**
 * Created by Mahyar on 6/22/2015.
 */
public enum CustomerStatus {
  NEW(9999L, "NEW_CUSTOMER"),
  UPDATED(9998L, "UPDATED_CUSTOMER"),
  UPDATED_LOCATION(9995L, "UPDATED_CUSTOMER_LOCATION"),
  SENT(9997L, "SENT_CUSTOMER"),
  SYSTEM(9996L, "SYSTEM");

  private Long id;
  private String title;

  CustomerStatus(Long id, String title) {
    this.id = id;
    this.title = title;
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

}
