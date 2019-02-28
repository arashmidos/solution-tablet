package com.parsroyal.storemanagement.data.event;

import com.parsroyal.storemanagement.constants.StatusCodes;

public class ImageTransferSuccessEvent extends DataTransferEvent {

  public ImageTransferSuccessEvent(String message, StatusCodes statusCode) {
    super(message, statusCode);
  }

  public ImageTransferSuccessEvent(StatusCodes statusCode) {
    super(statusCode);
  }
}
