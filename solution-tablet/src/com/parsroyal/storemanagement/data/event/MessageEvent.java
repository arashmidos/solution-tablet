package com.parsroyal.storemanagement.data.event;

import com.parsroyal.storemanagement.data.model.Message;
import java.util.List;

/**
 * Created by shkbhbb on 11/24/18.
 */

public class MessageEvent extends Event {

  private List<Message> messages;
  private boolean isCreateResponse;

  public MessageEvent(List<Message> messages, boolean isCreateResponse) {
    this.messages = messages;
    this.isCreateResponse = isCreateResponse;
  }

  public boolean isCreateResponse() {
    return isCreateResponse;
  }

  public void setCreateResponse(boolean createResponse) {
    isCreateResponse = createResponse;
  }

  public List<Message> getMessages() {
    return messages;
  }

  public void setMessages(List<Message> messages) {
    this.messages = messages;
  }
}
