package com.parsroyal.solutiontablet.data.dao;

import com.parsroyal.solutiontablet.data.entity.Payment;
import com.parsroyal.solutiontablet.data.listmodel.PaymentListModel;
import com.parsroyal.solutiontablet.data.searchobject.PaymentSO;

import java.util.List;

/**
 * Created by Arash on 11/8/2016.
 */
public interface PaymentDao extends BaseDao<Payment, Long>
{
    Payment getPaymentById(Long paymentID);

    List<PaymentListModel> getAllPaymentListModelByCustomerIdWithConstraint(Long customerId, String constraint);

    List<Payment> findPaymentsByStatusId(Long statusId);

    List<Payment> findPaymentByCustomerId(Long customerID);

    List<PaymentListModel> searchForPayments(PaymentSO paymentSO);
}
