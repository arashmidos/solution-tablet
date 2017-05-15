package com.parsroyal.solutiontablet.constants;

/**
 * Created by Arash on 2017-04-11
 */
public enum SortType {
  DEFAULT(0, "پیشفرض"),
  NAME(1, "نام"),
  DISTANCE(2, "فاصله"),
  HAS_ORDER(3, "سفارش"),
  HAS_REJECT(4, "مرجوعی");

  private int id;
  private String title;

  SortType(int id, String title) {
    this.id = id;
    this.title = title;
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

}
