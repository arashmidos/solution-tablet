package com.parsroyal.solutiontablet.data.dao;

import com.parsroyal.solutiontablet.data.entity.Questionnaire;
import com.parsroyal.solutiontablet.data.model.QuestionnaireListModel;
import com.parsroyal.solutiontablet.data.searchobject.QuestionnaireSo;

import java.util.List;

/**
 * Created by Mahyar on 7/24/2015.
 */
public interface QuestionnaireDao extends BaseDao<Questionnaire, Long>
{

    List<QuestionnaireListModel> searchForQuestionnaires(QuestionnaireSo questionnaireSo);
}
