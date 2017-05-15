package com.parsroyal.solutiontablet.ui.observer;

import android.location.Location;

/**
 * Created by Mahyar on 6/22/2015.
 */
public interface FindLocationListener {

  void foundLocation(Location location);

  void timeOut();
}
