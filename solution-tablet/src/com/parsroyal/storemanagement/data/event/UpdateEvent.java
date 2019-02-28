package com.parsroyal.storemanagement.data.event;

import android.net.Uri;

/**
 * Created by Arash on 2017-02-17.
 */
public class UpdateEvent extends Event {

  private final Uri downloadUri;
  private final boolean forceUpdate;

  public UpdateEvent(Uri uri, boolean forceUpdate) {
    this.forceUpdate = forceUpdate;
    this.downloadUri = uri;
  }

  public boolean isForceUpdate() {
    return forceUpdate;
  }

  public Uri getDownloadUri() {
    return downloadUri;
  }
}
