package com.parsroyal.solutiontablet.data.dao;

import com.parsroyal.solutiontablet.data.entity.VisitInformationDetail;

import java.util.List;

/**
 * Created by Arash on 2017-03-08.
 */
public interface VisitInformationDetailDao extends BaseDao<VisitInformationDetail, Long>
{
    List<VisitInformationDetail> getAllVisitDetail(Long visitId);

}
