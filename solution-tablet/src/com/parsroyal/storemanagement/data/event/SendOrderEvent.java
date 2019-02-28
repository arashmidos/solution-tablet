package com.parsroyal.storemanagement.data.event;

import com.parsroyal.storemanagement.constants.StatusCodes;

public class SendOrderEvent extends Event {

  private Long orderId;

  public Long getOrderId() {
    return orderId;
  }

  public SendOrderEvent(String message, StatusCodes statusCode) {
    this.message = message;
    this.statusCode = statusCode;
  }

  public SendOrderEvent(StatusCodes statusCode, Long orderId, String exceptionMessage) {
    this.statusCode = statusCode;
    this.orderId = orderId;
    this.message = exceptionMessage;
  }
}
