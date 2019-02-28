package com.parsroyal.storemanagement.data.dao;

import com.parsroyal.storemanagement.data.entity.QAnswer;
import com.parsroyal.storemanagement.data.model.AnswerDetailDto;
import com.parsroyal.storemanagement.data.model.QAnswerDto;
import java.util.List;

/**
 * Created by Mahyar on 7/27/2015.
 */
public interface QAnswerDao extends BaseDao<QAnswer, Long> {

  List<QAnswerDto> getAllQAnswersDtoForSend();
  List<QAnswerDto> getAllQAnswersDtoForSend(Long visitId);

  void updateCustomerBackendIdForAnswers(long customerId, long customerBackendId);

  List<AnswerDetailDto> getAllAnswerDetailByGroupId(Long answersGroupNo);

  void deleteAllAnswer(Long visitId, Long answersGroupNo);

}
