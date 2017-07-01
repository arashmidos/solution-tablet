package com.parsroyal.solutiontablet.data.event;

import com.parsroyal.solutiontablet.constants.StatusCodes;

public class ErrorEvent extends Event {

  public ErrorEvent(String message, StatusCodes statusCode) {
    this.message = message;
    this.statusCode = statusCode;
  }

  public ErrorEvent(StatusCodes statusCode) {
    this.statusCode = statusCode;
  }
}
