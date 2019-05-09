package com.parsroyal.solutiontablet.exception;

/**
 * Created by Mahyar on 6/4/2015.
 */
public class BusinessException extends RuntimeException {

  private String[] args;

  public BusinessException() {
  }

  public BusinessException(String detailMessage, String... args) {
    super(detailMessage);
    this.args = args;
  }

  public BusinessException(String detailMessage) {
    super(detailMessage);
  }

  public BusinessException(String detailMessage, Throwable throwable) {
    super(detailMessage, throwable);
  }

  public BusinessException(Throwable throwable) {
    super(throwable);
  }

  public String[] getArgs() {
    return args;
  }

  public void setArgs(String[] args) {
    this.args = args;
  }
}
