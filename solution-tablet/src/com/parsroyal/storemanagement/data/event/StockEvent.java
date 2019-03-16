package com.parsroyal.storemanagement.data.event;

import com.parsroyal.storemanagement.constants.StatusCodes;

public class StockEvent extends Event {

  private boolean isActive;

  public StockEvent(StatusCodes statusCode, boolean isActive) {
    this.isActive = isActive;
    this.statusCode = statusCode;
  }

  public boolean isActive() {
    return isActive;
  }

  public void setActive(boolean active) {
    isActive = active;
  }
}
