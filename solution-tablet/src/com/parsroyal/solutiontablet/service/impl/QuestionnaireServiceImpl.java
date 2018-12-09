package com.parsroyal.solutiontablet.service.impl;

import android.content.Context;
import com.parsroyal.solutiontablet.constants.SendStatus;
import com.parsroyal.solutiontablet.data.dao.QAnswerDao;
import com.parsroyal.solutiontablet.data.dao.QuestionDao;
import com.parsroyal.solutiontablet.data.dao.QuestionnaireDao;
import com.parsroyal.solutiontablet.data.dao.impl.QAnswerDaoImpl;
import com.parsroyal.solutiontablet.data.dao.impl.QuestionDaoImpl;
import com.parsroyal.solutiontablet.data.dao.impl.QuestionnaireDaoImpl;
import com.parsroyal.solutiontablet.data.entity.QAnswer;
import com.parsroyal.solutiontablet.data.listmodel.QuestionListModel;
import com.parsroyal.solutiontablet.data.listmodel.QuestionnaireListModel;
import com.parsroyal.solutiontablet.data.model.QAnswerDto;
import com.parsroyal.solutiontablet.data.model.QuestionDto;
import com.parsroyal.solutiontablet.data.searchobject.QuestionSo;
import com.parsroyal.solutiontablet.data.searchobject.QuestionnaireSo;
import com.parsroyal.solutiontablet.service.QuestionnaireService;
import com.parsroyal.solutiontablet.util.DateUtil;
import com.parsroyal.solutiontablet.util.Empty;
import com.parsroyal.solutiontablet.util.constants.ApplicationKeys;
import java.util.List;

/**
 * Created by Mahyar on 7/24/2015.
 */
public class QuestionnaireServiceImpl implements QuestionnaireService {

  private final SettingServiceImpl settingService;
  private Context context;
  private QuestionnaireDao questionnaireDao;
  private QuestionDao questionDao;
  private QAnswerDao qAnswerDao;

  public QuestionnaireServiceImpl(Context context) {
    this.context = context;
    this.questionnaireDao = new QuestionnaireDaoImpl(context);
    this.questionDao = new QuestionDaoImpl(context);
    this.qAnswerDao = new QAnswerDaoImpl(context);
    this.settingService = new SettingServiceImpl();
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
  public QuestionDto getQuestionDto(Long questionId, Long visitId, Long goodsBackendId,
      Long answersGroupNo) {
    return questionDao.getQuestionDto(questionId, visitId, goodsBackendId, answersGroupNo);
  }

  @Override
  public QuestionDto getQuestionDtoByBackendId(Long backendId, Long visitId, Long goodsBackendId,
      Long answersGroupNo) {
    return questionDao.getQuestionDtoByBackendId(backendId, visitId, goodsBackendId, answersGroupNo);

  }

  @Override
  public QuestionDto getQuestionDto(Long questionnaireBackendId, Long visitId, Integer order,
      Long goodsBackendId, boolean isNext, Long answersGroupNo) {
    return questionDao
        .getQuestionDto(questionnaireBackendId, visitId, order, goodsBackendId, isNext,
            answersGroupNo);
  }

  @Override
  public Long saveAnswer(QAnswer qAnswer) {
    if (Empty.isEmpty(qAnswer.getId()) || qAnswer.getId().equals(0L)) {
      qAnswer.setCreateDateTime(DateUtil.getCurrentGregorianFullWithTimeDate());
      qAnswer.setUpdateDateTime(DateUtil.getCurrentGregorianFullWithTimeDate());
      qAnswer.setStatus(SendStatus.NEW.getId());
      return qAnswerDao.create(qAnswer);
    } else {
      qAnswer.setUpdateDateTime(DateUtil.getCurrentGregorianFullWithTimeDate());
      qAnswer.setStatus(SendStatus.NEW.getId());
      qAnswerDao.update(qAnswer);
      return qAnswer.getId();
    }
  }

  @Override
  public List<QAnswerDto> getAllAnswersDtoForSend() {
    List<QAnswerDto> answersGroupList = qAnswerDao.getAllQAnswersDtoForSend();

    String salesmanId = settingService.getSettingValue(ApplicationKeys.SALESMAN_ID);
    for (int i = 0; i < answersGroupList.size(); i++) {
      QAnswerDto qAnswerDto = answersGroupList.get(i);
      qAnswerDto.setAnswers(qAnswerDao.getAllAnswerDetailByGroupId(qAnswerDto.getAnswersGroupNo()));
      qAnswerDto.setSalesmanId(Long.valueOf(salesmanId));
    }

    return answersGroupList;
  }

  @Override
  public List<QAnswerDto> getAllAnswersDtoForSend(Long visitId) {
    List<QAnswerDto> answersGroupList = qAnswerDao.getAllQAnswersDtoForSend(visitId);

    String salesmanId = settingService.getSettingValue(ApplicationKeys.SALESMAN_ID);
    for (int i = 0; i < answersGroupList.size(); i++) {
      QAnswerDto qAnswerDto = answersGroupList.get(i);
      qAnswerDto.setAnswers(qAnswerDao.getAllAnswerDetailByGroupId(qAnswerDto.getAnswersGroupNo()));
      qAnswerDto.setSalesmanId(Long.valueOf(salesmanId));
    }

    return answersGroupList;
  }

  @Override
  public QAnswer getAnswerById(Long id) {
    return qAnswerDao.retrieve(id);
  }

  @Override
  public List<QuestionnaireListModel> searchForQuestionsList(QuestionnaireSo questionnaireSo) {
    return questionnaireDao.searchForQuestionsList(questionnaireSo);
  }

  @Override
  public Long getNextAnswerGroupNo() {
    return questionnaireDao.getNextAnswerGroupNo();
  }

  @Override
  public void deleteAllAnswer(Long visitId, Long answersGroupNo) {
    qAnswerDao.deleteAllAnswer(visitId, answersGroupNo);
  }

  @Override
  public void deleteAnswerById(Long answerId) {
    qAnswerDao.delete(answerId);
  }

  @Override
  public void deleteAll() {
    questionDao.deleteAll();
    questionnaireDao.deleteAll();
    qAnswerDao.deleteAll();
  }
}
