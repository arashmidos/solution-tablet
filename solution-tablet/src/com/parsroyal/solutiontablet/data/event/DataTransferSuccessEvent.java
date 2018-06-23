package com.parsroyal.solutiontablet.data.event;

import com.parsroyal.solutiontablet.constants.StatusCodes;
import java.util.ArrayList;
import java.util.List;

public class DataTransferSuccessEvent extends DataTransferEvent {

  private List<String> giftData = new ArrayList<>();

  public DataTransferSuccessEvent(String message, StatusCodes statusCode) {
    super(message, statusCode);
  }

  public DataTransferSuccessEvent(String message, StatusCodes statusCode, List<String> giftResult) {
    super(message, statusCode);
    this.giftData = giftResult;
  }

  public DataTransferSuccessEvent(StatusCodes statusCode) {
    super(statusCode);
  }

  public List<String> getGiftData() {
    return giftData;
  }

  public void setGiftData(List<String> giftData) {
    this.giftData = giftData;
  }
}
