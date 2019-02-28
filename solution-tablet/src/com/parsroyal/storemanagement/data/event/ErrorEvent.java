package com.parsroyal.storemanagement.data.event;

import com.parsroyal.storemanagement.constants.StatusCodes;

public class ErrorEvent extends Event {

  public ErrorEvent(String message, StatusCodes statusCode) {
    this.message = message;
    this.statusCode = statusCode;
  }

  public ErrorEvent(StatusCodes statusCode) {
    this.statusCode = statusCode;
  }
}
