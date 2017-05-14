package com.parsroyal.solutiontablet.data.dao;

import com.parsroyal.solutiontablet.data.entity.QAnswer;

import java.util.List;

/**
 * Created by Mahyar on 7/27/2015.
 */
public interface QAnswerDao extends BaseDao<QAnswer, Long>
{

    List<QAnswer> getAllQAnswersForSend();

    void updateCustomerBackendIdForAnswers(long customerId, long customerBackendId);

}
