package com.parsroyal.storemanagement.constants;

/**
 * Created by Arash on 2017-08-22
 */
public enum SaleType {
  COLD(1L, "COLD", 2, "ویزیتور"),
  HOT(2L, "HOT", 1, "ویزیتور"),
  DISTRIBUTOR(3L, "DISTRIBUTOR", 0, "توزیع کننده"),
  COLLECTOR(4L, "COLLECTOR", 5, "مامور وصول"),
  MERCHANDISER(5L, "MERCHANDISER", 4, "مرچندایزر"),
  AGENT(6L, "AGENT", 3, "نماینده"),
  STORE_MANAGEMENT(10L, "STOREMANAGEMENT", 10, "مدیریت انبار");

  private final int order;
  private final String role;
  private long value;
  private String title;

  SaleType(long value, String title, int order, String role) {
    this.value = value;
    this.title = title;
    this.order = order;
    this.role = role;
  }

  public static SaleType getByValue(long value) {
    SaleType found = null;
    for (SaleType visitType : SaleType.values()) {
      if (visitType.getValue() == value) {
        found = visitType;
      }
    }
    return found;
  }

  public String getRole() {
    return role;
  }

  public int getOrder() {
    return order;
  }

  public String getTitle() {
    return title;
  }

  public long getValue() {
    return value;
  }

  public void setValue(long value) {
    this.value = value;
  }
}
