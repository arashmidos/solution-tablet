package com.parsroyal.solutiontablet.constants;

/**
 * Created by Arash on 2017-08-22
 */
public enum SaleType {
  COLD(1L, "COLD"),
  HOT(2L, "HOT"),
  DISTRIBUTOR(3L, "DISTRIBUTOR");

  private long value;
  private String title;

  SaleType(long value, String title) {
    this.value = value;
    this.title = title;
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

  public long getValue() {
    return value;
  }

  public void setValue(long value) {
    this.value = value;
  }
}
