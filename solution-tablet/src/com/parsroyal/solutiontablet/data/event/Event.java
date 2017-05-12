package com.parsroyal.solutiontablet.data.event;

import java.io.Serializable;

public class Event implements Serializable {

  protected String message;

  public int getStatusCode() {
    return statusCode;
  }

  public void setStatusCode(int statusCode) {
    this.statusCode = statusCode;
  }

  protected int statusCode;

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }
}
