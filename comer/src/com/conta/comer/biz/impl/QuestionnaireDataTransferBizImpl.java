package com.conta.comer.biz.impl;

import android.content.Context;
import android.util.Log;

import com.conta.comer.R;
import com.conta.comer.biz.AbstractDataTransferBizImpl;
import com.conta.comer.data.dao.QuestionDao;
import com.conta.comer.data.dao.QuestionnaireDao;
import com.conta.comer.data.dao.impl.QuestionDaoImpl;
import com.conta.comer.data.dao.impl.QuestionnaireDaoImpl;
import com.conta.comer.data.entity.Question;
import com.conta.comer.data.entity.Questionnaire;
import com.conta.comer.data.model.QuestionnaireDto;
import com.conta.comer.data.model.QuestionnaireDtoList;
import com.conta.comer.ui.observer.ResultObserver;
import com.conta.comer.util.CharacterFixUtil;
import com.conta.comer.util.DateUtil;
import com.conta.comer.util.Empty;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;

import java.util.Date;

/**
 * Created by Mahyar on 7/24/2015.
 */
public class QuestionnaireDataTransferBizImpl extends AbstractDataTransferBizImpl<QuestionnaireDtoList>
{

    public static final String TAG = QuestionnaireDataTransferBizImpl.class.getSimpleName();

    private Context context;
    private QuestionnaireDao questionnaireDao;
    private QuestionDao questionDao;
    private ResultObserver resultObserver;

    public QuestionnaireDataTransferBizImpl(Context context, ResultObserver resultObserver)
    {
        super(context);
        this.context = context;
        this.questionnaireDao = new QuestionnaireDaoImpl(context);
        this.questionDao = new QuestionDaoImpl(context);
        this.resultObserver = resultObserver;
    }

    @Override
    public void receiveData(QuestionnaireDtoList data)
    {
        if (Empty.isNotEmpty(data) && Empty.isNotEmpty(data.getQuestionnaireDtoList()))
        {
            try
            {
                questionDao.deleteAll();
                questionnaireDao.deleteAll();
                for (QuestionnaireDto questionnaireDto : data.getQuestionnaireDtoList())
                {
                    Questionnaire questionnaire = createQuestionnaireEntityFromDto(questionnaireDto);
                    questionnaire.setCreateDateTime(DateUtil.convertDate(new Date(), DateUtil.FULL_FORMATTER_GREGORIAN_WITH_TIME, "EN"));
                    questionnaire.setUpdateDateTime(DateUtil.convertDate(new Date(), DateUtil.FULL_FORMATTER_GREGORIAN_WITH_TIME, "EN"));
                    questionnaireDao.create(questionnaire);
                    for (Question question : questionnaireDto.getQuestions())
                    {
                        question.setQuestion(CharacterFixUtil.fixString(question.getQuestion()));
                        question.setCreateDateTime(DateUtil.convertDate(new Date(), DateUtil.FULL_FORMATTER_GREGORIAN_WITH_TIME, "EN"));
                        question.setUpdateDateTime(DateUtil.convertDate(new Date(), DateUtil.FULL_FORMATTER_GREGORIAN_WITH_TIME, "EN"));
                        questionDao.create(question);
                    }
                }
                getObserver().publishResult(context.getString(R.string.message_questionnaires_transferred_successfully));
            } catch (Exception e)
            {
                Log.e(TAG, e.getMessage(), e);
                getObserver().publishResult(context.getString(R.string.message_exception_in_transferring_questionnaires));
            }
        }
    }

    private Questionnaire createQuestionnaireEntityFromDto(QuestionnaireDto questionnaireDto)
    {
        Questionnaire questionnaire = new Questionnaire();
        questionnaire.setBackendId(questionnaireDto.getBackendId());
        questionnaire.setFromDate(questionnaireDto.getFromDate());
        questionnaire.setToDate(questionnaireDto.getToDate());
        questionnaire.setGoodsGroupBackendId(questionnaireDto.getGoodsGroupBackendId());
        questionnaire.setCustomerGroupBackendId(questionnaireDto.getCustomerGroupBackendId());
        questionnaire.setDescription(questionnaireDto.getDescription());
        questionnaire.setStatus(questionnaireDto.getStatus());
        return questionnaire;
    }

    @Override
    public void beforeTransfer()
    {
        getObserver().publishResult(context.getString(R.string.message_transferring_questionnaires_data));
    }

    @Override
    public ResultObserver getObserver()
    {
        return resultObserver;
    }

    @Override
    public String getMethod()
    {
        return "questionnaire/all";
    }

    @Override
    public Class getType()
    {
        return QuestionnaireDtoList.class;
    }

    @Override
    public HttpMethod getHttpMethod()
    {
        return HttpMethod.POST;
    }

    @Override
    protected MediaType getContentType()
    {
        return MediaType.TEXT_PLAIN;
    }

    @Override
    protected HttpEntity getHttpEntity(HttpHeaders headers)
    {
        HttpEntity requestEntity = new HttpEntity<String>(DateUtil.convertDate(new Date(), DateUtil.GLOBAL_FORMATTER, "FA"), headers);
        return requestEntity;
    }
}
