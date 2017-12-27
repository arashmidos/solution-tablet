package com.parsroyal.solutiontablet.biz.impl;

import android.content.Context;
import android.util.Log;
import com.parsroyal.solutiontablet.R;
import com.parsroyal.solutiontablet.constants.StatusCodes;
import com.parsroyal.solutiontablet.data.dao.QAnswerDao;
import com.parsroyal.solutiontablet.data.dao.QuestionDao;
import com.parsroyal.solutiontablet.data.dao.QuestionnaireDao;
import com.parsroyal.solutiontablet.data.dao.impl.QAnswerDaoImpl;
import com.parsroyal.solutiontablet.data.dao.impl.QuestionDaoImpl;
import com.parsroyal.solutiontablet.data.dao.impl.QuestionnaireDaoImpl;
import com.parsroyal.solutiontablet.data.entity.Question;
import com.parsroyal.solutiontablet.data.entity.Questionnaire;
import com.parsroyal.solutiontablet.data.event.DataTransferErrorEvent;
import com.parsroyal.solutiontablet.data.event.DataTransferSuccessEvent;
import com.parsroyal.solutiontablet.data.model.QuestionDto;
import com.parsroyal.solutiontablet.data.model.QuestionnaireDto;
import com.parsroyal.solutiontablet.data.model.QuestionnaireDtoList;
import com.parsroyal.solutiontablet.service.GetDataRestService;
import com.parsroyal.solutiontablet.service.ServiceGenerator;
import com.parsroyal.solutiontablet.util.CharacterFixUtil;
import com.parsroyal.solutiontablet.util.DateUtil;
import com.parsroyal.solutiontablet.util.Empty;
import com.parsroyal.solutiontablet.util.Logger;
import com.parsroyal.solutiontablet.util.NetworkUtil;
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
              EventBus.getDefault().post(new DataTransferSuccessEvent(
                  context.getString(R.string.provinces_data_transferred_successfully)
                  , StatusCodes.SUCCESS));
            } catch (Exception e) {
              Logger.sendError("Data transfer",
                  "Error in receiving QuestionaireData " + e.getMessage());
              Log.e(TAG, e.getMessage(), e);
              EventBus.getDefault().post(new DataTransferErrorEvent(StatusCodes.INVALID_DATA));
            }
          } else {
            EventBus.getDefault().post(new DataTransferSuccessEvent(
                context.getString(R.string.provinces_data_transferred_successfully)
                , StatusCodes.SUCCESS));
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
