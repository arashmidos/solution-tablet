package com.parsroyal.solutiontablet.service;

import com.parsroyal.solutiontablet.data.entity.Payment;
import com.parsroyal.solutiontablet.data.listmodel.PaymentListModel;
import com.parsroyal.solutiontablet.data.searchobject.PaymentSO;
import java.util.List;

/**
 * Created by Arash on 08/19/2016
 */
public interface PaymentService extends BaseService {

  Payment getPaymentById(Long paymentID);

  long savePayment(Payment payment);

  void updatePayment(Payment payment);

  List<Payment> getAllPaymentsByStatus(Long status);
  List<Payment> getAllPaymentsByVisitId(Long visitId);

  List<PaymentListModel> searchForPayments(PaymentSO paymentSO);

  void clearAllSentPayment();

  void deletePayment(Long paymentId);
}
