package com.parsroyal.solutiontablet.data.event;

import com.parsroyal.solutiontablet.constants.StatusCodes;

public class NavigateErrorEvent extends Event {

  public NavigateErrorEvent(String message, StatusCodes statusCode) {
    this.message = message;
    this.statusCode = statusCode;
  }

  public NavigateErrorEvent(StatusCodes statusCode) {
    this.statusCode = statusCode;
  }
}
