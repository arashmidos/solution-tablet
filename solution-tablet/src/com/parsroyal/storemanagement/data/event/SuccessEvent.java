package com.parsroyal.storemanagement.data.event;

import com.parsroyal.storemanagement.constants.StatusCodes;

public class SuccessEvent extends Event {

  public SuccessEvent(String message, StatusCodes statusCode) {
    this.message = message;
    this.statusCode = statusCode;
  }

  public SuccessEvent(StatusCodes statusCode) {
    this.statusCode = statusCode;
  }
}
