package com.conta.comer.data.dao;

import com.conta.comer.data.entity.Questionnaire;
import com.conta.comer.data.model.QuestionnaireListModel;
import com.conta.comer.data.searchobject.QuestionnaireSo;

import java.util.List;

/**
 * Created by Mahyar on 7/24/2015.
 */
public interface QuestionnaireDao extends BaseDao<Questionnaire, Long>
{

    List<QuestionnaireListModel> searchForQuestionnaires(QuestionnaireSo questionnaireSo);
}
