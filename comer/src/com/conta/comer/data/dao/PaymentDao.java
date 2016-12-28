package com.conta.comer.data.dao;

import com.conta.comer.data.entity.Payment;
import com.conta.comer.data.entity.SaleOrder;
import com.conta.comer.data.listmodel.CustomerListModel;
import com.conta.comer.data.listmodel.PaymentListModel;
import com.conta.comer.data.model.SaleOrderDto;
import com.conta.comer.data.model.SaleOrderListModel;
import com.conta.comer.data.searchobject.PaymentSO;
import com.conta.comer.data.searchobject.SaleOrderSO;

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
