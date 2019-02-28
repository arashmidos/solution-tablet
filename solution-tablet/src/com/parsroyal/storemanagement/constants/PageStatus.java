package com.parsroyal.storemanagement.constants;

/**
 * Created by shkbhbb on 11/7/17.
 */

public enum  PageStatus {

  VIEW("View"),
  EDIT("Edit");

  private String state;

   PageStatus(String state) {
    this.state = state;
  }

  public String getState() {
    return state;
  }

  public void setState(String state) {
    this.state = state;
  }
}
