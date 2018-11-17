package com.parsroyal.solutiontablet.data.dao;

import com.parsroyal.solutiontablet.data.entity.VisitLine;
import com.parsroyal.solutiontablet.data.listmodel.VisitLineListModel;
import com.parsroyal.solutiontablet.data.model.LabelValue;
import java.util.List;

/**
 * Created by Mahyar on 7/6/2015.
 */
public interface VisitLineDao extends BaseDao<VisitLine, Long> {

  List<VisitLineListModel> getAllVisitLineListModel();

  List<LabelValue> getAllVisitLineLabelValue();

  List<VisitLineListModel> getAllVisitLinesListModelByConstraint(String constraint);

  VisitLineListModel getVisitLineListModelByBackendId(long visitlineBackendId);

  VisitLine getVisitLineByBackendId(Long backendId);

}
