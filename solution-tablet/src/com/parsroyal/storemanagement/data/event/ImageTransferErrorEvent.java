package com.parsroyal.storemanagement.data.event;

import com.parsroyal.storemanagement.constants.StatusCodes;

public class ImageTransferErrorEvent extends DataTransferEvent {

  public ImageTransferErrorEvent(String message,
      StatusCodes statusCode) {
    super(message, statusCode);
  }

  public ImageTransferErrorEvent(StatusCodes statusCode) {
    super(statusCode);
  }
}
