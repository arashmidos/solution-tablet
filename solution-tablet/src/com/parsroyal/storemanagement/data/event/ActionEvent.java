package com.parsroyal.storemanagement.data.event;

import com.parsroyal.storemanagement.constants.StatusCodes;

public class ActionEvent extends Event {

  public ActionEvent(StatusCodes statusCodes) {
    this.statusCode = statusCodes;
  }
}
