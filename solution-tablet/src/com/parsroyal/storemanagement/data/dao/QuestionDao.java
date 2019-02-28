package com.parsroyal.storemanagement.data.dao;

import com.parsroyal.storemanagement.data.entity.Question;
import com.parsroyal.storemanagement.data.listmodel.QuestionListModel;
import com.parsroyal.storemanagement.data.model.QuestionDto;
import com.parsroyal.storemanagement.data.searchobject.QuestionSo;
import java.util.List;

/**
 * Created by Mahyar on 7/24/2015.
 */
public interface QuestionDao extends BaseDao<Question, Long> {

  List<QuestionListModel> searchForQuestions(QuestionSo questionSo);

  QuestionDto getQuestionDto(Long questionId, Long visitId, Long goodsBackendId,
      Long answersGroupNo);

  QuestionDto getQuestionDtoByBackendId(Long questionId, Long visitId, Long goodsBackendId,
      Long answersGroupNo);

  QuestionDto getQuestionDto(Long questionnaireBackendId, Long visitId, Integer order,
      Long goodsBackendId, boolean isNext, Long answersGroupNo);
}
