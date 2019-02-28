package com.parsroyal.storemanagement.service;

import com.parsroyal.storemanagement.data.entity.QAnswer;
import com.parsroyal.storemanagement.data.listmodel.QuestionListModel;
import com.parsroyal.storemanagement.data.listmodel.QuestionnaireListModel;
import com.parsroyal.storemanagement.data.model.QAnswerDto;
import com.parsroyal.storemanagement.data.model.QuestionDto;
import com.parsroyal.storemanagement.data.searchobject.QuestionSo;
import com.parsroyal.storemanagement.data.searchobject.QuestionnaireSo;
import java.util.List;

/**
 * Created by Mahyar on 7/24/2015.
 */
public interface QuestionnaireService extends BaseService {

  List<QuestionnaireListModel> searchForQuestionnaires(QuestionnaireSo questionnaireSo);

  List<QuestionListModel> searchForQuestions(QuestionSo questionSo);

  QuestionDto getQuestionDto(Long primaryKey, Long visitId, Long goodsBackendId,
      Long answersGroupNo);

  QuestionDto getQuestionDtoByBackendId(Long primaryKey, Long visitId, Long goodsBackendId,
      Long answersGroupNo);

  QuestionDto getQuestionDto(Long questionnaireBackendId, Long visitId, Integer order,
      Long goodsBackendId, boolean isNext, Long answersGroupNo);

  Long saveAnswer(QAnswer qAnswer);

  List<QAnswerDto> getAllAnswersDtoForSend();
  List<QAnswerDto> getAllAnswersDtoForSend(Long visitId);

  QAnswer getAnswerById(Long id);

  List<QuestionnaireListModel> searchForQuestionsList(QuestionnaireSo questionnaireSo);

  Long getNextAnswerGroupNo();

  void deleteAllAnswer(Long visitId, Long answersGroupNo);

  void deleteAnswerById(Long answerId);
}
