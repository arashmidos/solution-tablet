package com.parsroyal.solutiontablet.data.event;

public class ErrorEvent extends Event {

  public ErrorEvent(String message, int statusCode) {
    this.message = message;
    this.statusCode = statusCode;
  }

  public ErrorEvent() {
  }
}
