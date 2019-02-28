package com.parsroyal.storemanagement.biz.impl;

import android.content.Context;
import android.util.Log;
import com.crashlytics.android.Crashlytics;
import com.parsroyal.storemanagement.R;
import com.parsroyal.storemanagement.biz.AbstractDataTransferBizImpl;
import com.parsroyal.storemanagement.constants.SendStatus;
import com.parsroyal.storemanagement.constants.StatusCodes;
import com.parsroyal.storemanagement.constants.VisitInformationDetailType;
import com.parsroyal.storemanagement.data.dao.QAnswerDao;
import com.parsroyal.storemanagement.data.dao.impl.QAnswerDaoImpl;
import com.parsroyal.storemanagement.data.entity.QAnswer;
import com.parsroyal.storemanagement.data.event.DataTransferSuccessEvent;
import com.parsroyal.storemanagement.data.model.AnswerDetailDto;
import com.parsroyal.storemanagement.data.model.QAnswerDto;
import com.parsroyal.storemanagement.service.impl.VisitServiceImpl;
import com.parsroyal.storemanagement.ui.observer.ResultObserver;
import com.parsroyal.storemanagement.util.DateUtil;
import com.parsroyal.storemanagement.util.Empty;
import com.parsroyal.storemanagement.util.NumberUtil;
import java.util.Locale;
import org.greenrobot.eventbus.EventBus;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;

/**
 * Created by Arash on 29/12/2017
 */
public class QAnswersDataTransferBizImpl extends AbstractDataTransferBizImpl<String> {

  private final VisitServiceImpl visitService;
  private ResultObserver resultObserver;
  private QAnswerDao qAnswerDao;
  private QAnswerDto answer;
  private int success = 0;
  private int total = 0;

  public QAnswersDataTransferBizImpl(Context context) {
    super(context);
    this.qAnswerDao = new QAnswerDaoImpl(context);
    this.visitService = new VisitServiceImpl(context);
  }

  @Override
  public void receiveData(String response) {
    if (Empty.isNotEmpty(response)) {
      try {
        Long backendId = Long.valueOf(response);
        for (int i = 0; i < answer.getAnswers().size(); i++) {
          AnswerDetailDto answerDetailDto = answer.getAnswers().get(i);
          QAnswer qAnswer = qAnswerDao.retrieve(answerDetailDto.getAnswerId());
          if (Empty.isNotEmpty(qAnswer)) {
            qAnswer.setBackendId(backendId);
            qAnswer.setUpdateDateTime(DateUtil.getCurrentGregorianFullWithTimeDate());
            qAnswer.setStatus(SendStatus.SENT.getId());
            qAnswerDao.update(qAnswer);
          }
        }
        visitService.updateVisitDetailId(VisitInformationDetailType.FILL_QUESTIONNAIRE,
            answer.getAnswersGroupNo(), backendId);
        success++;
      } catch (Exception ex) {
        Crashlytics
            .log(Log.ERROR, "Data transfer", "Error in receiving QAnswerData " + ex.getMessage());
        Log.e(TAG, ex.getMessage(), ex);
      }
    }
    EventBus.getDefault()
        .post(new DataTransferSuccessEvent(getSuccessfulMessage(), StatusCodes.UPDATE));
  }


  protected String getExceptionMessage() {
    return context.getString(R.string.message_exception_in_sending_answers);
  }

  public String getSuccessfulMessage() {
    return NumberUtil.digitsToPersian(String
        .format(Locale.getDefault(), context.getString(R.string.data_transfered_result),
            String.valueOf(success),
            String.valueOf(total - success)));
  }

  @Override
  public void beforeTransfer() {
  }

  @Override
  public ResultObserver getObserver() {
    return resultObserver;
  }

  @Override
  public String getMethod() {
    return "questionnaire/answers";
  }

  @Override
  public Class getType() {
    return String.class;
  }

  @Override
  public HttpMethod getHttpMethod() {
    return HttpMethod.POST;
  }

  @Override
  protected MediaType getContentType() {
    return MediaType.APPLICATION_JSON;
  }

  @Override
  protected HttpEntity getHttpEntity(HttpHeaders headers) {
    return new HttpEntity<>(answer, headers);
  }

  public void setAnswer(QAnswerDto answer) {
    this.answer = answer;
    this.total++;
  }

  public int getSuccess() {
    return success;
  }

  public int getTotal() {
    return total;
  }
}
