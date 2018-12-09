package com.parsroyal.solutiontablet.data.dao;

import com.parsroyal.solutiontablet.data.entity.VisitLine;
import com.parsroyal.solutiontablet.data.listmodel.VisitLineListModel;
import com.parsroyal.solutiontablet.data.model.LabelValue;
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
