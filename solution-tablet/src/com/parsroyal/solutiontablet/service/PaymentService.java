package com.parsroyal.solutiontablet.service;

import com.parsroyal.solutiontablet.data.entity.Payment;
import com.parsroyal.solutiontablet.data.listmodel.PaymentListModel;
import com.parsroyal.solutiontablet.data.searchobject.PaymentSO;

import java.util.List;

/**
 * Created by Arash on 08/19/2016
 */
public interface PaymentService
{

    Payment getPaymentById(Long paymentID);

    long savePayment(Payment payment);

    void updatePayment(Payment payment);

    List<Payment> getAllPaymentsByStatus(Long status);

    List<Payment> getAllPaymentByCustomerID(Long customerId);

    List<PaymentListModel> getAllPaymentListModelByCustomerIdWithConstraint(Long customerId,
                                                                            String constraint);

    List<PaymentListModel> getAllPaymentsListModelByCustomerBackendId(Long customerBackendId);

    List<PaymentListModel> searchForPayments(PaymentSO paymentSO);

    void clearAllSentPayment();
}
