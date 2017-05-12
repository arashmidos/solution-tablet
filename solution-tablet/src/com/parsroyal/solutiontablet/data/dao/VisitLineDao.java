package com.parsroyal.solutiontablet.data.dao;

import com.parsroyal.solutiontablet.data.entity.VisitLine;
import com.parsroyal.solutiontablet.data.listmodel.VisitLineListModel;
import java.util.List;

/**
 * Created by Mahyar on 7/6/2015.
 */
public interface VisitLineDao extends BaseDao<VisitLine, Long> {

  List<VisitLineListModel> getAllVisitLineListModel();

  List<VisitLineListModel> getAllVisitLinesListModelByConstraint(String constraint);

  VisitLine getVisitLineByBackendId(Long backendId);
}
