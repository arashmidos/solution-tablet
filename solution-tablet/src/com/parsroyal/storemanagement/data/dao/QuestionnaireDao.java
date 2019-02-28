package com.parsroyal.storemanagement.data.dao;

import com.parsroyal.storemanagement.data.entity.Questionnaire;
import com.parsroyal.storemanagement.data.listmodel.QuestionnaireListModel;
import com.parsroyal.storemanagement.data.searchobject.QuestionnaireSo;
import java.util.List;

/**
 * Created by Mahyar on 7/24/2015.
 */
public interface QuestionnaireDao extends BaseDao<Questionnaire, Long> {

  List<QuestionnaireListModel> searchForQuestionnaires(QuestionnaireSo questionnaireSo);
  List<QuestionnaireListModel> searchForQuestionsList(QuestionnaireSo questionnaireSo);

  Long getNextAnswerGroupNo();
}
