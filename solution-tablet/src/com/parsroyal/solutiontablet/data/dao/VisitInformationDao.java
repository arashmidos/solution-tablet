package com.parsroyal.solutiontablet.data.dao;

import android.location.Location;

import com.parsroyal.solutiontablet.data.entity.VisitInformation;

import java.util.List;

/**
 * Created by Mahyar on 7/17/2015.
 */
public interface VisitInformationDao extends BaseDao<VisitInformation, Long>
{
    List<VisitInformation> getAllVisitInformationForSend();

    void updateLocation(Long visitInformationId, Location location);

    VisitInformation retrieveForNewCustomer(Long customerId);
}
