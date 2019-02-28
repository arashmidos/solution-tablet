package com.parsroyal.storemanagement.data.dao;

import com.parsroyal.storemanagement.data.entity.VisitLine;
import com.parsroyal.storemanagement.data.listmodel.VisitLineListModel;
import com.parsroyal.storemanagement.data.model.LabelValue;
import java.util.Date;
import java.util.List;

/**
 * Created by Mahyar on 7/6/2015.
 */
public interface VisitLineDao extends BaseDao<VisitLine, Long> {

  List<VisitLineListModel> getAllVisitLineListModel();

  List<VisitLineListModel> getAllVisitLineListModel(Date from, Date to);

  List<LabelValue> getAllVisitLineLabelValue();

  VisitLineListModel getVisitLineListModelByBackendId(long visitlineBackendId);

  VisitLine getVisitLineByBackendId(Long backendId);

}
