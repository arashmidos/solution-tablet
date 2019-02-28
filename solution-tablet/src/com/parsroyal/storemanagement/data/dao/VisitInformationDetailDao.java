package com.parsroyal.storemanagement.data.dao;

import com.parsroyal.storemanagement.constants.VisitInformationDetailType;
import com.parsroyal.storemanagement.data.entity.VisitInformationDetail;
import com.parsroyal.storemanagement.data.model.VisitInformationDetailDto;
import com.parsroyal.storemanagement.data.searchobject.VisitInformationDetailSO;
import java.util.List;

/**
 * Created by Arash on 2017-03-08.
 */
public interface VisitInformationDetailDao extends BaseDao<VisitInformationDetail, Long> {

  List<VisitInformationDetail> getAllVisitDetail(Long visitId);

  void deleteVisitDetail(VisitInformationDetailType type, Long typdId);

  void updateVisitDetailId(VisitInformationDetailType type, long id, long backendId);

  List<VisitInformationDetail> search(Long visitId, VisitInformationDetailType type, Long typeId);

  List<VisitInformationDetail> search(Long visitId, VisitInformationDetailType type);
  List<VisitInformationDetail> search(VisitInformationDetailSO visitInformationDetailSO);

  List<VisitInformationDetailDto> getAllVisitDetailDto(Long id);

}
