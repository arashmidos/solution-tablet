package com.parsroyal.solutiontablet.data.dao;

import com.parsroyal.solutiontablet.data.entity.Question;
import com.parsroyal.solutiontablet.data.listmodel.QuestionListModel;
import com.parsroyal.solutiontablet.data.model.QuestionDto;
import com.parsroyal.solutiontablet.data.searchobject.QuestionSo;

import java.util.List;

/**
 * Created by Mahyar on 7/24/2015.
 */
public interface QuestionDao extends BaseDao<Question, Long>
{
    List<QuestionListModel> searchForQuestions(QuestionSo questionSo);

    QuestionDto getQuestionDto(Long questionId, Long visitId, Long goodsBackendId);

    QuestionDto getQuestionDto(Long questionnaireBackendId, Long visitId, Integer order, Long goodsBackendId,boolean isNext);
}
