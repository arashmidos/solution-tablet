package com.parsroyal.solutiontablet.data.event;

import com.parsroyal.solutiontablet.constants.StatusCodes;

public class ImageTransferSuccessEvent extends DataTransferEvent {

  public ImageTransferSuccessEvent(String message, StatusCodes statusCode) {
    super(message, statusCode);
  }

  public ImageTransferSuccessEvent(StatusCodes statusCode) {
    super(statusCode);
  }
}
