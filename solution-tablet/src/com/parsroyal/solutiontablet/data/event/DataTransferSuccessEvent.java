package com.parsroyal.solutiontablet.data.event;

import com.parsroyal.solutiontablet.constants.StatusCodes;

public class DataTransferSuccessEvent extends DataTransferEvent {

  public DataTransferSuccessEvent(String message, StatusCodes statusCode) {
    super(message, statusCode);
  }

  public DataTransferSuccessEvent(StatusCodes statusCode) {
    super(statusCode);
  }
}
