package com.parsroyal.storemanagement.data.dao;

import com.parsroyal.storemanagement.data.entity.Payment;
import com.parsroyal.storemanagement.data.listmodel.PaymentListModel;
import com.parsroyal.storemanagement.data.searchobject.PaymentSO;
import java.util.List;

/**
 * Created by Arash on 11/8/2016.
 */
public interface PaymentDao extends BaseDao<Payment, Long> {

  Payment getPaymentById(Long paymentID);

  List<Payment> findPaymentsByStatusId(Long statusId);

  List<Payment> findPaymentsByVisitId(Long visitId);

  List<PaymentListModel> searchForPayments(PaymentSO paymentSO);

  void deleteAllSentPayment();

}
