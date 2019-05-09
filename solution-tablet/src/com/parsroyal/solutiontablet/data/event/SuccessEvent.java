package com.parsroyal.solutiontablet.data.event;

import com.parsroyal.solutiontablet.constants.StatusCodes;

public class SuccessEvent extends Event {

  public SuccessEvent(String message, StatusCodes statusCode) {
    this.message = message;
    this.statusCode = statusCode;
  }

  public SuccessEvent(StatusCodes statusCode) {
    this.statusCode = statusCode;
  }
}
