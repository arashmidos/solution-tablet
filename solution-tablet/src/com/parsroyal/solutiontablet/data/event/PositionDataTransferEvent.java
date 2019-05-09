package com.parsroyal.solutiontablet.data.event;

import com.parsroyal.solutiontablet.constants.StatusCodes;

public class PositionDataTransferEvent extends DataTransferEvent {

  public PositionDataTransferEvent(String message, StatusCodes statusCode) {
    super(message, statusCode);
  }

  public PositionDataTransferEvent(StatusCodes statusCode) {
    super(statusCode);
  }
}
