package com.conta.comer.service;

import com.conta.comer.data.entity.QAnswer;
import com.conta.comer.data.listmodel.QuestionListModel;
import com.conta.comer.data.model.QuestionDto;
import com.conta.comer.data.model.QuestionnaireListModel;
import com.conta.comer.data.searchobject.QuestionSo;
import com.conta.comer.data.searchobject.QuestionnaireSo;

import java.util.List;

/**
 * Created by Mahyar on 7/24/2015.
 */
public interface QuestionnaireService
{

    List<QuestionnaireListModel> searchForQuestionnaires(QuestionnaireSo questionnaireSo);

    List<QuestionListModel> searchForQuestions(QuestionSo questionSo);

    QuestionDto getQuestionDto(Long primaryKey, Long visitId, Long goodsBackendId);

    QuestionDto getQuestionDto(Long questionnaireBackendId, Long visitId, Integer order, Long goodsBackendId);

    Long saveAnswer(QAnswer qAnswer);

    List<QAnswer> getAllAnswersForSend();

    QAnswer getAnswerById(Long id);
}
