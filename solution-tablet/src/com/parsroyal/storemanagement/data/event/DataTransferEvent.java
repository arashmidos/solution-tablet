package com.parsroyal.storemanagement.data.event;

import com.parsroyal.storemanagement.constants.StatusCodes;

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
