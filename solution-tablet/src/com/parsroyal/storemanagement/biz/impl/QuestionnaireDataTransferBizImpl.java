package com.parsroyal.storemanagement.biz.impl;

import android.content.Context;
import android.util.Log;
import com.parsroyal.storemanagement.constants.StatusCodes;
import com.parsroyal.storemanagement.data.dao.QAnswerDao;
import com.parsroyal.storemanagement.data.dao.QuestionDao;
import com.parsroyal.storemanagement.data.dao.QuestionnaireDao;
import com.parsroyal.storemanagement.data.dao.impl.QAnswerDaoImpl;
import com.parsroyal.storemanagement.data.dao.impl.QuestionDaoImpl;
import com.parsroyal.storemanagement.data.dao.impl.QuestionnaireDaoImpl;
import com.parsroyal.storemanagement.data.entity.Question;
import com.parsroyal.storemanagement.data.entity.Questionnaire;
import com.parsroyal.storemanagement.data.event.DataTransferErrorEvent;
import com.parsroyal.storemanagement.data.event.DataTransferSuccessEvent;
import com.parsroyal.storemanagement.data.model.QuestionDto;
import com.parsroyal.storemanagement.data.model.QuestionnaireDto;
import com.parsroyal.storemanagement.data.model.QuestionnaireDtoList;
import com.parsroyal.storemanagement.service.GetDataRestService;
import com.parsroyal.storemanagement.service.ServiceGenerator;
import com.parsroyal.storemanagement.util.CharacterFixUtil;
import com.parsroyal.storemanagement.util.DateUtil;
import com.parsroyal.storemanagement.util.Empty;
import com.parsroyal.storemanagement.util.Logger;
import com.parsroyal.storemanagement.util.NetworkUtil;
import java.io.IOException;
import org.greenrobot.eventbus.EventBus;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Arash on 27/12/2017
 */
public class QuestionnaireDataTransferBizImpl {

  public static final String TAG = QuestionnaireDataTransferBizImpl.class.getSimpleName();

  private Context context;
  private QuestionnaireDao questionnaireDao;
  private QuestionDao questionDao;
  private QAnswerDao qAnswerDao;

  public QuestionnaireDataTransferBizImpl(Context context) {
    this.context = context;
    this.questionnaireDao = new QuestionnaireDaoImpl(context);
    this.questionDao = new QuestionDaoImpl(context);
    this.qAnswerDao = new QAnswerDaoImpl(context);
  }

  public void exchangeData() {
    if (!NetworkUtil.isNetworkAvailable(context)) {
      EventBus.getDefault().post(new DataTransferErrorEvent(StatusCodes.NO_NETWORK));
    }

    GetDataRestService restService = ServiceGenerator.createService(GetDataRestService.class);

    Call<QuestionnaireDtoList> call = restService
        .getAllQuestionnaire(DateUtil.getCurrentGregorianFullWithDate());

    call.enqueue(new Callback<QuestionnaireDtoList>() {
      @Override
      public void onResponse(Call<QuestionnaireDtoList> call,
          Response<QuestionnaireDtoList> response) {
        if (response.isSuccessful()) {
          QuestionnaireDtoList data = response.body();
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
              EventBus.getDefault().post(new DataTransferSuccessEvent("", StatusCodes.SUCCESS));
            } catch (Exception e) {
              Logger.sendError("Data transfer",
                  "Error in receiving QuestionaireData " + e.getMessage());
              Log.e(TAG, e.getMessage(), e);
              EventBus.getDefault().post(new DataTransferErrorEvent(StatusCodes.INVALID_DATA));
            }
          } else {
            EventBus.getDefault().post(new DataTransferSuccessEvent("", StatusCodes.NO_DATA_ERROR));
          }
        } else {
          try {
            Log.d("TAG", response.errorBody().string());
          } catch (IOException e) {
            e.printStackTrace();
          }
          EventBus.getDefault().post(new DataTransferErrorEvent(StatusCodes.SERVER_ERROR));
        }
      }

      @Override
      public void onFailure(Call<QuestionnaireDtoList> call, Throwable t) {
        EventBus.getDefault().post(new DataTransferErrorEvent(StatusCodes.NETWORK_ERROR));
      }
    });
  }

  private Question createQuestionEntityFromDto(QuestionDto questionDto) {
    return new Question(questionDto.getBackendId(), questionDto.getQuestionnaireId(),
        CharacterFixUtil.fixString(questionDto.getQuestion()), questionDto.getAnswer(),
        questionDto.getStatus(),
        questionDto.getOrder(), questionDto.getType(), questionDto.isRequired(),
        questionDto.getPrerequisite());

  }

  private Questionnaire createQuestionnaireEntityFromDto(QuestionnaireDto questionnaireDto) {

    return new Questionnaire(questionnaireDto.getId(),
        questionnaireDto.getFromDate(), questionnaireDto.getToDate(),
        questionnaireDto.getGoodsGroupId(), questionnaireDto.getCustomerGroupId(),
        questionnaireDto.getDescription(), questionnaireDto.getStatus());
  }
}
