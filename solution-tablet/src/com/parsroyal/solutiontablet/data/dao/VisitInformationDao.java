package com.parsroyal.solutiontablet.data.dao;

import com.parsroyal.solutiontablet.data.entity.VisitInformation;
import com.parsroyal.solutiontablet.data.model.VisitInformationDto;
import java.util.List;

/**
 * Created by Mahyar on 7/17/2015.
 */
public interface VisitInformationDao extends BaseDao<VisitInformation, Long> {

  List<VisitInformation> getAllVisitInformationForSend();

  void updateLocation(Long visitInformationId, Double lat, Double lng);

  VisitInformation retrieveForNewCustomer(Long customerId);

  List<VisitInformationDto> getAllVisitInformationDtoForSend(Long visitId);

  void clearAllSent();

}
