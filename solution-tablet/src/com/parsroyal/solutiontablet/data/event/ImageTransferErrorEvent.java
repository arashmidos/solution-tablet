package com.parsroyal.solutiontablet.data.event;

import com.parsroyal.solutiontablet.constants.StatusCodes;

public class ImageTransferErrorEvent extends DataTransferEvent {

  public ImageTransferErrorEvent(String message,
      StatusCodes statusCode) {
    super(message, statusCode);
  }

  public ImageTransferErrorEvent(StatusCodes statusCode) {
    super(statusCode);
  }
}
