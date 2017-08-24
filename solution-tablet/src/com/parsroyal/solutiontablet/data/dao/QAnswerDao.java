package com.parsroyal.solutiontablet.data.dao;

import com.parsroyal.solutiontablet.data.entity.QAnswer;
import com.parsroyal.solutiontablet.data.model.AnswerDetailDto;
import com.parsroyal.solutiontablet.data.model.QAnswerDto;
import java.util.List;

/**
 * Created by Mahyar on 7/27/2015.
 */
public interface QAnswerDao extends BaseDao<QAnswer, Long> {

  List<QAnswerDto> getAllQAnswersDtoForSend();

  void updateCustomerBackendIdForAnswers(long customerId, long customerBackendId);

  List<AnswerDetailDto> getAllAnswerDetailByGroupId(Long answersGroupNo);
}
