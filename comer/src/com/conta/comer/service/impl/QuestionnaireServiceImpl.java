package com.conta.comer.service.impl;

import android.content.Context;

import com.conta.comer.data.dao.QAnswerDao;
import com.conta.comer.data.dao.QuestionDao;
import com.conta.comer.data.dao.QuestionnaireDao;
import com.conta.comer.data.dao.impl.QAnswerDaoImpl;
import com.conta.comer.data.dao.impl.QuestionDaoImpl;
import com.conta.comer.data.dao.impl.QuestionnaireDaoImpl;
import com.conta.comer.data.entity.QAnswer;
import com.conta.comer.data.listmodel.QuestionListModel;
import com.conta.comer.data.model.QuestionDto;
import com.conta.comer.data.model.QuestionnaireListModel;
import com.conta.comer.data.searchobject.QuestionSo;
import com.conta.comer.data.searchobject.QuestionnaireSo;
import com.conta.comer.service.QuestionnaireService;
import com.conta.comer.util.DateUtil;
import com.conta.comer.util.Empty;

import java.util.List;

/**
 * Created by Mahyar on 7/24/2015.
 */
public class QuestionnaireServiceImpl implements QuestionnaireService
{

    private Context context;
    private QuestionnaireDao questionnaireDao;
    private QuestionDao questionDao;
    private QAnswerDao qAnswerDao;

    public QuestionnaireServiceImpl(Context context)
    {
        this.context = context;
        this.questionnaireDao = new QuestionnaireDaoImpl(context);
        this.questionDao = new QuestionDaoImpl(context);
        this.qAnswerDao = new QAnswerDaoImpl(context);
    }

    @Override
    public List<QuestionnaireListModel> searchForQuestionnaires(QuestionnaireSo questionnaireSo)
    {
        return questionnaireDao.searchForQuestionnaires(questionnaireSo);
    }

    @Override
    public List<QuestionListModel> searchForQuestions(QuestionSo questionSo)
    {
        return questionDao.searchForQuestions(questionSo);
    }

    @Override
    public QuestionDto getQuestionDto(Long questionId, Long visitId, Long goodsBackendId)
    {
        return questionDao.getQuestionDto(questionId, visitId, goodsBackendId);
    }

    @Override
    public QuestionDto getQuestionDto(Long questionnaireBackendId, Long visitId, Integer order, Long goodsBackendId)
    {
        return questionDao.getQuestionDto(questionnaireBackendId, visitId, order, goodsBackendId);
    }

    @Override
    public Long saveAnswer(QAnswer qAnswer)
    {
        if (Empty.isEmpty(qAnswer.getId()) || qAnswer.getId().equals(0L))
        {
            qAnswer.setCreateDateTime(DateUtil.getCurrentGregorianFullWithTimeDate());
            qAnswer.setUpdateDateTime(DateUtil.getCurrentGregorianFullWithTimeDate());
            return qAnswerDao.create(qAnswer);
        } else
        {
            qAnswer.setUpdateDateTime(DateUtil.getCurrentGregorianFullWithTimeDate());
            qAnswerDao.update(qAnswer);
            return qAnswer.getId();
        }
    }

    @Override
    public List<QAnswer> getAllAnswersForSend()
    {
        return qAnswerDao.getAllQAnswersForSend();
    }

    @Override
    public QAnswer getAnswerById(Long id)
    {
        return qAnswerDao.retrieve(id);
    }
}
