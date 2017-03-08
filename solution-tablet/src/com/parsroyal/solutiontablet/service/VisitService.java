package com.parsroyal.solutiontablet.service;

import android.location.Location;

import com.parsroyal.solutiontablet.constants.VisitInformationDetailType;
import com.parsroyal.solutiontablet.data.entity.VisitInformation;
import com.parsroyal.solutiontablet.data.entity.VisitInformationDetail;
import com.parsroyal.solutiontablet.data.entity.VisitLine;
import com.parsroyal.solutiontablet.data.listmodel.VisitLineListModel;

import java.util.List;

/**
 * Created by Arash on 2017-03-08
 */
public interface VisitService
{
    List<VisitLine> getAllVisitLines();

    List<VisitLineListModel> getAllVisitLinesListModel();

    List<VisitLineListModel> getAllFilteredVisitLinesListModel(String constraint);

    Long startVisiting(Long customerBackendId);

    Long startVisitingNewCustomer(Long customerId);

    void finishVisiting(Long visitId);

    List<VisitInformation> getAllVisitInformationForSend();

    VisitInformation getVisitInformationById(Long visitId);

    VisitInformation getVisitInformationForNewCustomer(Long customerId);

    Long saveVisit(VisitInformation visit);

    void updateVisitLocation(Long visitInformationId, Location location);

    void saveVisitDetail(VisitInformationDetail visitDetail);

    List<VisitInformationDetail> getAllVisitDetailById(Long visitId);
}
