package com.conta.comer.data.dao;

import android.location.Location;

import com.conta.comer.data.entity.VisitInformation;

import java.util.List;

/**
 * Created by Mahyar on 7/17/2015.
 */
public interface VisitInformationDao extends BaseDao<VisitInformation, Long>
{
    List<VisitInformation> getAllVisitInformationForSend();

    void updateLocation(Long visitInformationId, Location location);

    List<VisitInformation> retrieveForNewCustomer(Long customerId);
}
