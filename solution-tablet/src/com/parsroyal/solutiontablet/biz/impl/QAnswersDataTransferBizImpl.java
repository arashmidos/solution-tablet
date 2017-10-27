package com.parsroyal.solutiontablet.biz.impl;

import android.content.Context;
import android.util.Log;
import com.crashlytics.android.Crashlytics;
import com.parsroyal.solutiontablet.R;
import com.parsroyal.solutiontablet.biz.AbstractDataTransferBizImpl;
import com.parsroyal.solutiontablet.constants.VisitInformationDetailType;
import com.parsroyal.solutiontablet.data.dao.QAnswerDao;
import com.parsroyal.solutiontablet.data.dao.impl.QAnswerDaoImpl;
import com.parsroyal.solutiontablet.data.entity.QAnswer;
import com.parsroyal.solutiontablet.data.model.AnswerDetailDto;
import com.parsroyal.solutiontablet.data.model.QAnswerDto;
import com.parsroyal.solutiontablet.service.impl.VisitServiceImpl;
import com.parsroyal.solutiontablet.ui.observer.ResultObserver;
import com.parsroyal.solutiontablet.util.DateUtil;
import com.parsroyal.solutiontablet.util.Empty;
import java.util.Locale;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;

/**
 * Created by Mahyar on 8/4/2015.
 */
public class QAnswersDataTransferBizImpl extends AbstractDataTransferBizImpl<String> {

  private final VisitServiceImpl visitService;
  private ResultObserver resultObserver;
  private QAnswerDao qAnswerDao;
  private QAnswerDto answer;
  private int success = 0;
  private int total = 0;

  public QAnswersDataTransferBizImpl(Context context, ResultObserver resultObserver) {
    super(context);
    this.resultObserver = resultObserver;
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
            qAnswerDao.update(qAnswer);
            visitService
                .updateVisitDetailId(VisitInformationDetailType.FILL_QUESTIONNAIRE, qAnswer.getId(),
                    backendId);
          }
        }
        success++;
      } catch (Exception ex) {
        Crashlytics
            .log(Log.ERROR, "Data transfer", "Error in receiving QAnswerData " + ex.getMessage());
        Log.e(TAG, ex.getMessage(), ex);
        resultObserver.publishResult(getExceptionMessage());
      }
    }
  }


  protected String getExceptionMessage() {
    return context.getString(R.string.message_exception_in_sending_answers);
  }

  public String getSuccessfulMessage() {
    return String
        .format(Locale.US, context.getString(R.string.data_transfered_result),
            String.valueOf(success),
            String.valueOf(total - success));
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
}
