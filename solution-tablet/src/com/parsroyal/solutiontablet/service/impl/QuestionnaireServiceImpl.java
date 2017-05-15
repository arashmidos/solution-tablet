package com.parsroyal.solutiontablet.service.impl;

import android.content.Context;
import com.parsroyal.solutiontablet.data.dao.QAnswerDao;
import com.parsroyal.solutiontablet.data.dao.QuestionDao;
import com.parsroyal.solutiontablet.data.dao.QuestionnaireDao;
import com.parsroyal.solutiontablet.data.dao.impl.QAnswerDaoImpl;
import com.parsroyal.solutiontablet.data.dao.impl.QuestionDaoImpl;
import com.parsroyal.solutiontablet.data.dao.impl.QuestionnaireDaoImpl;
import com.parsroyal.solutiontablet.data.entity.QAnswer;
import com.parsroyal.solutiontablet.data.listmodel.QuestionListModel;
import com.parsroyal.solutiontablet.data.listmodel.QuestionnaireListModel;
import com.parsroyal.solutiontablet.data.model.QuestionDto;
import com.parsroyal.solutiontablet.data.searchobject.QuestionSo;
import com.parsroyal.solutiontablet.data.searchobject.QuestionnaireSo;
import com.parsroyal.solutiontablet.service.QuestionnaireService;
import com.parsroyal.solutiontablet.util.DateUtil;
import com.parsroyal.solutiontablet.util.Empty;
import java.util.List;

/**
 * Created by Mahyar on 7/24/2015.
 */
public class QuestionnaireServiceImpl implements QuestionnaireService {

  private Context context;
  private QuestionnaireDao questionnaireDao;
  private QuestionDao questionDao;
  private QAnswerDao qAnswerDao;

  public QuestionnaireServiceImpl(Context context) {
    this.context = context;
    this.questionnaireDao = new QuestionnaireDaoImpl(context);
    this.questionDao = new QuestionDaoImpl(context);
    this.qAnswerDao = new QAnswerDaoImpl(context);
  }

  @Override
  public List<QuestionnaireListModel> searchForQuestionnaires(QuestionnaireSo questionnaireSo) {
    return questionnaireDao.searchForQuestionnaires(questionnaireSo);
  }

  @Override
  public List<QuestionListModel> searchForQuestions(QuestionSo questionSo) {
    return questionDao.searchForQuestions(questionSo);
  }

  @Override
  public QuestionDto getQuestionDto(Long questionId, Long visitId, Long goodsBackendId) {
    return questionDao.getQuestionDto(questionId, visitId, goodsBackendId);
  }

  @Override
  public QuestionDto getQuestionDto(Long questionnaireBackendId, Long visitId, Integer order,
      Long goodsBackendId, boolean isNext) {
    return questionDao
        .getQuestionDto(questionnaireBackendId, visitId, order, goodsBackendId, isNext);
  }

  @Override
  public Long saveAnswer(QAnswer qAnswer) {
    if (Empty.isEmpty(qAnswer.getId()) || qAnswer.getId().equals(0L)) {
      qAnswer.setCreateDateTime(DateUtil.getCurrentGregorianFullWithTimeDate());
      qAnswer.setUpdateDateTime(DateUtil.getCurrentGregorianFullWithTimeDate());
      return qAnswerDao.create(qAnswer);
    } else {
      qAnswer.setUpdateDateTime(DateUtil.getCurrentGregorianFullWithTimeDate());
      qAnswerDao.update(qAnswer);
      return qAnswer.getId();
    }
  }

  @Override
  public List<QAnswer> getAllAnswersForSend() {
    return qAnswerDao.getAllQAnswersForSend();
  }

  @Override
  public QAnswer getAnswerById(Long id) {
    return qAnswerDao.retrieve(id);
  }

  @Override
  public List<QuestionnaireListModel> searchForAnonymousQuestionaire(
      QuestionnaireSo questionnaireSo) {
    return questionDao.searchForAnonymousQuestions(questionnaireSo);
  }
}
