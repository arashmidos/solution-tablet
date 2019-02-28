package com.parsroyal.storemanagement.data.event;

import com.parsroyal.storemanagement.constants.StatusCodes;

public class DataTransferErrorEvent extends DataTransferEvent {

  public DataTransferErrorEvent(String message,
      StatusCodes statusCode) {
    super(message, statusCode);
  }

  public DataTransferErrorEvent(StatusCodes statusCode) {
    super(statusCode);
  }
}
