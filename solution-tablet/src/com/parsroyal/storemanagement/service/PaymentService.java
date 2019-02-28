package com.parsroyal.storemanagement.service;

import com.parsroyal.storemanagement.data.entity.Payment;
import com.parsroyal.storemanagement.data.listmodel.PaymentListModel;
import com.parsroyal.storemanagement.data.searchobject.PaymentSO;
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
