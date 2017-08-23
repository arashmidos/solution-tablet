package com.parsroyal.solutiontablet.biz.impl;

import android.content.Context;
import android.util.Log;
import com.crashlytics.android.Crashlytics;
import com.parsroyal.solutiontablet.R;
import com.parsroyal.solutiontablet.biz.AbstractDataTransferBizImpl;
import com.parsroyal.solutiontablet.data.dao.QAnswerDao;
import com.parsroyal.solutiontablet.data.dao.QuestionDao;
import com.parsroyal.solutiontablet.data.dao.QuestionnaireDao;
import com.parsroyal.solutiontablet.data.dao.impl.QAnswerDaoImpl;
import com.parsroyal.solutiontablet.data.dao.impl.QuestionDaoImpl;
import com.parsroyal.solutiontablet.data.dao.impl.QuestionnaireDaoImpl;
import com.parsroyal.solutiontablet.data.entity.Question;
import com.parsroyal.solutiontablet.data.entity.Questionnaire;
import com.parsroyal.solutiontablet.data.model.QuestionDto;
import com.parsroyal.solutiontablet.data.model.QuestionnaireDto;
import com.parsroyal.solutiontablet.data.model.QuestionnaireDtoList;
import com.parsroyal.solutiontablet.service.impl.SettingServiceImpl;
import com.parsroyal.solutiontablet.ui.observer.ResultObserver;
import com.parsroyal.solutiontablet.util.CharacterFixUtil;
import com.parsroyal.solutiontablet.util.DateUtil;
import com.parsroyal.solutiontablet.util.Empty;
import com.parsroyal.solutiontablet.util.constants.ApplicationKeys;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;

/**
 * Created by Mahyar on 7/24/2015.
 */
public class QuestionnaireDataTransferBizImpl extends
    AbstractDataTransferBizImpl<QuestionnaireDtoList> {

  public static final String TAG = QuestionnaireDataTransferBizImpl.class.getSimpleName();
  private final SettingServiceImpl settingService;

  private Context context;
  private QuestionnaireDao questionnaireDao;
  private QuestionDao questionDao;
  private QAnswerDao qAnswerDao;
  private ResultObserver resultObserver;

  public QuestionnaireDataTransferBizImpl(Context context, ResultObserver resultObserver) {
    super(context);
    this.context = context;
    this.questionnaireDao = new QuestionnaireDaoImpl(context);
    this.questionDao = new QuestionDaoImpl(context);
    this.qAnswerDao = new QAnswerDaoImpl(context);
    this.resultObserver = resultObserver;
    this.settingService = new SettingServiceImpl(context);
  }

  @Override
  public void receiveData(QuestionnaireDtoList data) {
    if (Empty.isNotEmpty(data) && Empty.isNotEmpty(data.getQuestionnaires())) {
      try {
        questionDao.deleteAll();
        qAnswerDao.deleteAll();
        questionnaireDao.deleteAll();
        for (QuestionnaireDto questionnaireDto : data.getQuestionnaires()) {
          Questionnaire questionnaire = createQuestionnaireEntityFromDto(questionnaireDto);

          questionnaireDao.create(questionnaire);
          for (QuestionDto questionDto : questionnaireDto.getQuestions()) {
            Question question = createQuestionEntityFromDto(questionDto);
            questionDao.create(question);
          }
        }
        getObserver().publishResult(
            context.getString(R.string.message_questionnaires_transferred_successfully));
      } catch (Exception e) {
        Crashlytics.log(Log.ERROR, "Data transfer",
            "Error in receiving QuestionaireData " + e.getMessage());
        Log.e(TAG, e.getMessage(), e);
        getObserver().publishResult(
            context.getString(R.string.message_exception_in_transferring_questionnaires));
      }
    }
  }

  private Question createQuestionEntityFromDto(QuestionDto questionDto) {
    return new Question(questionDto.getBackendId(), questionDto.getQuestionnaireId(),
        CharacterFixUtil.fixString(questionDto.getQuestion()), questionDto.getAnswer(),
        questionDto.getStatus(),
        questionDto.getOrder(), questionDto.getType());

  }

  private Questionnaire createQuestionnaireEntityFromDto(QuestionnaireDto questionnaireDto) {

    return new Questionnaire(questionnaireDto.getId(),
        questionnaireDto.getFromDate(), questionnaireDto.getToDate(),
        questionnaireDto.getGoodsGroupId(), questionnaireDto.getCustomerGroupId(),
        questionnaireDto.getDescription(), questionnaireDto.getStatus());
  }

  @Override
  public void beforeTransfer() {
    getObserver()
        .publishResult(context.getString(R.string.message_transferring_questionnaires_data));
  }

  @Override
  public ResultObserver getObserver() {
    return resultObserver;
  }

  @Override
  public String getMethod() {
    String salesmanId = settingService.getSettingValue(ApplicationKeys.SALESMAN_ID);
    String today = DateUtil.getCurrentGregorianFullWithDate();
    return String.format("questionnaire/%s/%s", salesmanId, today);
  }

  @Override
  public Class getType() {
    return QuestionnaireDtoList.class;
  }

  @Override
  public HttpMethod getHttpMethod() {
    return HttpMethod.GET;
  }

  @Override
  protected MediaType getContentType() {
    return MediaType.APPLICATION_JSON;
  }

  @Override
  protected HttpEntity getHttpEntity(HttpHeaders headers) {
    return new HttpEntity<>(headers);
  }
}
