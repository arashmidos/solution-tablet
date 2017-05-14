package com.parsroyal.solutiontablet.service;

import com.parsroyal.solutiontablet.ui.observer.FindLocationListener;

/**
 * Created with IntelliJ IDEA.
 * User: m.sefidi
 * Date: 7/20/13
 * Time: 2:18 PM
 * To change this template use File | Settings | File Templates.
 */
public interface LocationService
{

    void findCurrentLocation(FindLocationListener listener);

    void stopFindingLocation();
}
