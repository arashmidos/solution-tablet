package com.parsroyal.solutiontablet.data.event;

import com.parsroyal.solutiontablet.constants.StatusCodes;

public class ActionEvent extends Event {

  public ActionEvent(StatusCodes statusCodes) {
    this.statusCode = statusCodes;
  }
}
