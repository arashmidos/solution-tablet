package com.parsroyal.solutiontablet.data.event;

import com.parsroyal.solutiontablet.constants.StatusCodes;

/**
 * Created by arash on 8/23/17.
 */

public class DataTransferEvent extends Event {

  public DataTransferEvent(String message, StatusCodes statusCode) {
    this.message = message;
    this.statusCode = statusCode;
  }

  public DataTransferEvent() {

  }

  public DataTransferEvent(StatusCodes statusCode) {
    this.statusCode = statusCode;
  }
}
