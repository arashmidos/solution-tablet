package com.parsroyal.solutiontablet.service;

import com.parsroyal.solutiontablet.data.entity.QAnswer;
import com.parsroyal.solutiontablet.data.listmodel.QuestionListModel;
import com.parsroyal.solutiontablet.data.model.QuestionDto;
import com.parsroyal.solutiontablet.data.model.QuestionnaireListModel;
import com.parsroyal.solutiontablet.data.searchobject.QuestionSo;
import com.parsroyal.solutiontablet.data.searchobject.QuestionnaireSo;

import java.util.List;

/**
 * Created by Mahyar on 7/24/2015.
 */
public interface QuestionnaireService
{

    List<QuestionnaireListModel> searchForQuestionnaires(QuestionnaireSo questionnaireSo);

    List<QuestionListModel> searchForQuestions(QuestionSo questionSo);

    QuestionDto getQuestionDto(Long primaryKey, Long visitId, Long goodsBackendId);

    QuestionDto getQuestionDto(Long questionnaireBackendId, Long visitId, Integer order, Long goodsBackendId,boolean isNext);

    Long saveAnswer(QAnswer qAnswer);

    List<QAnswer> getAllAnswersForSend();

    QAnswer getAnswerById(Long id);
}
