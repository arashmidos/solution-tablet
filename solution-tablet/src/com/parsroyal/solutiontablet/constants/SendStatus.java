package com.parsroyal.solutiontablet.constants;

import android.content.Context;
import com.parsroyal.solutiontablet.util.Empty;
import com.parsroyal.solutiontablet.util.ResourceUtil;

/**
 * Created by Arash on 2016-08-13
 */
public enum SendStatus {
  NEW(9999L, "NEW"),
  UPDATED(9998L, "UPDATED"),
  SENT(9997L, "SENT");

  private Long id;
  private String title;

  SendStatus(Long id, String title) {
    this.id = id;
    this.title = title;
  }

  public static String getDisplayTitle(Context context, Long status) {
    SendStatus foundStatus = findById(status);
    if (Empty.isNotEmpty(foundStatus)) {
      return ResourceUtil.getString(context, "payment_status_" + foundStatus.getTitle());
    } else {
      return "";
    }
  }

  private static SendStatus findById(Long statusId) {
    for (SendStatus sendStatus : SendStatus.values()) {
      if (sendStatus.getId().equals(statusId)) {
        return sendStatus;
      }
    }
    return null;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

}
