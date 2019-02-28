package com.parsroyal.storemanagement.exception;

/**
 * Created by Mahyar on 8/29/2015.
 */
public class SaleOrderItemCountExceedExistingException extends BusinessException {

  public SaleOrderItemCountExceedExistingException() {
  }

  public SaleOrderItemCountExceedExistingException(String detailMessage, String... args) {
    super(detailMessage, args);
  }

  public SaleOrderItemCountExceedExistingException(String detailMessage) {
    super(detailMessage);
  }

  public SaleOrderItemCountExceedExistingException(String detailMessage, Throwable throwable) {
    super(detailMessage, throwable);
  }

  public SaleOrderItemCountExceedExistingException(Throwable throwable) {
    super(throwable);
  }
}
