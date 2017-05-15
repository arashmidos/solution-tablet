package com.parsroyal.solutiontablet.constants;

/**
 * Created by Mahyar on 6/20/2015.
 */
public enum BaseInfoTypes {
  ACTIVITY_TYPE(1001L, "ACTIVITY_TYPE"),
  OWNERSHIP_TYPE(1002L, "OWNERSHIP_TYPE"),
  CUSTOMER_CLASS(1003L, "CUSTOMER_CLASS"),
  PAYMENT_TYPE(1004L, "PAYMENT_TYPE"),
  WANT_TYPE(1005L, "WANT_TYPE"),
  BANK_NAME_TYPE(1006L, "BANK_NAME_TYPE"),
  REJECT_TYPE(1007L, "REJECT_TYPE");
  private Long id;
  private String title;

  BaseInfoTypes(Long typeId, String title) {
    this.id = typeId;
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
