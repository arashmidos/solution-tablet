package com.parsroyal.solutiontablet.data.event;

import com.parsroyal.solutiontablet.data.model.Message;
import java.util.List;

/**
 * Created by shkbhbb on 11/24/18.
 */

public class MessageEvent extends Event {

  private List<Message> messages;

  public MessageEvent(List<Message> messages) {
    this.messages = messages;
  }

  public List<Message> getMessages() {
    return messages;
  }

  public void setMessages(List<Message> messages) {
    this.messages = messages;
  }
}
