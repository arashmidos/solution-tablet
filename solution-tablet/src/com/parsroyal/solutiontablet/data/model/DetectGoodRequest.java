package com.parsroyal.solutiontablet.data.model;

public class DetectGoodRequest {

  private String barcode;
  private Long asnSerial;

  public DetectGoodRequest(String barcode, Long asnSerial) {
    this.barcode = barcode;
    this.asnSerial = asnSerial;
  }

  public String getBarcode() {
    return barcode;
  }

  public void setBarcode(String barcode) {
    this.barcode = barcode;
  }

  public Long getAsnSerial() {
    return asnSerial;
  }

  public void setAsnSerial(Long asnSerial) {
    this.asnSerial = asnSerial;
  }
}
