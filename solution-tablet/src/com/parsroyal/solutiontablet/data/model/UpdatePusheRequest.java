package com.parsroyal.solutiontablet.data.model;

public class UpdatePusheRequest {

  private String pusheId;
  private String GCMToken;

  public UpdatePusheRequest(String pusheId, String GCMToken) {
    this.pusheId = pusheId;
    this.GCMToken = GCMToken;
  }

  public UpdatePusheRequest() {

  }

  public String getPusheId() {
    return pusheId;
  }

  public void setPusheId(String pusheId) {
    this.pusheId = pusheId;
  }

  public String getGCMToken() {
    return GCMToken;
  }

  public void setGCMToken(String GCMToken) {
    this.GCMToken = GCMToken;
  }
}
