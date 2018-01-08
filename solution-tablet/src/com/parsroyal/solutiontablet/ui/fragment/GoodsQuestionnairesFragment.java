package com.parsroyal.solutiontablet.ui.fragment;

import android.os.Bundle;
import android.widget.AdapterView;
import com.parsroyal.solutiontablet.constants.Constants;
import com.parsroyal.solutiontablet.data.listmodel.QuestionnaireListModel;
import com.parsroyal.solutiontablet.data.searchobject.QuestionnaireSo;
import com.parsroyal.solutiontablet.ui.OldMainActivity;
import com.parsroyal.solutiontablet.ui.adapter.QuestionnaireListAdapter;

/**
 * Created by Mahyar on 7/28/2015.
 */
public class GoodsQuestionnairesFragment extends GeneralQuestionnairesFragment {

  @Override
  protected QuestionnaireSo getSearchObject() {
    QuestionnaireSo questionnaireSo = new QuestionnaireSo();
    questionnaireSo.setGeneral(false);
    return questionnaireSo;
  }

  @Override
  protected AdapterView.OnItemClickListener getOnItemClickListener() {
    return (parent1, view, position, id) -> {
      QuestionnaireListModel questionnaireListModel = dataModel.get(position);
      Bundle args = new Bundle();
      args.putLong("qnId", questionnaireListModel.getBackendId());
      args.putLong(Constants.VISIT_ID, visitId);
      args.putLong(Constants.CUSTOMER_ID, customerId);
      args.putLong("ggBi", questionnaireListModel.getGoodsGroupBackendId());
      oldMainActivity
          .changeFragment(OldMainActivity.GOODS_LIST_FOR_QUESTIONNAIRES_FRAGMENT_ID, args, false);
    };
  }

  @Override
  protected QuestionnaireListAdapter getAdapter() {
    return new QuestionnaireListAdapter(oldMainActivity, dataModel, false);
  }

  @Override
  public int getFragmentId() {
    return OldMainActivity.GOODS_QUESTIONNAIRES_FRAGMENT_ID;
  }
}
