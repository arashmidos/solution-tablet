package com.parsroyal.solutiontablet.data.dao;

import com.parsroyal.solutiontablet.constants.VisitInformationDetailType;
import com.parsroyal.solutiontablet.data.entity.VisitInformationDetail;
import com.parsroyal.solutiontablet.data.model.VisitInformationDetailDto;
import java.util.List;

/**
 * Created by Arash on 2017-03-08.
 */
public interface VisitInformationDetailDao extends BaseDao<VisitInformationDetail, Long> {

  List<VisitInformationDetail> getAllVisitDetail(Long visitId);

  void deleteVisitDetail(VisitInformationDetailType type, Long typdId);

  void updateVisitDetailId(VisitInformationDetailType type, long id, long backendId);

  List<VisitInformationDetail> search(Long visitId, VisitInformationDetailType type, Long typeId);

  List<VisitInformationDetailDto> getAllVisitDetailDto(Long id);
}
