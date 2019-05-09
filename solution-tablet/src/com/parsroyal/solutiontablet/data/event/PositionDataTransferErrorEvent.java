package com.parsroyal.solutiontablet.data.event;

import com.parsroyal.solutiontablet.constants.StatusCodes;

public class PositionDataTransferErrorEvent extends DataTransferEvent {

  public PositionDataTransferErrorEvent(String message, StatusCodes statusCode) {
    super(message, statusCode);
  }

  public PositionDataTransferErrorEvent(StatusCodes statusCode) {
    super(statusCode);
  }
}
