package com.conta.comer.data.dao;

import com.conta.comer.data.entity.Question;
import com.conta.comer.data.listmodel.QuestionListModel;
import com.conta.comer.data.model.QuestionDto;
import com.conta.comer.data.searchobject.QuestionSo;

import java.util.List;

/**
 * Created by Mahyar on 7/24/2015.
 */
public interface QuestionDao extends BaseDao<Question, Long>
{
    List<QuestionListModel> searchForQuestions(QuestionSo questionSo);

    QuestionDto getQuestionDto(Long questionId, Long visitId, Long goodsBackendId);

    QuestionDto getQuestionDto(Long questionnaireBackendId, Long visitId, Integer order, Long goodsBackendId);
}
