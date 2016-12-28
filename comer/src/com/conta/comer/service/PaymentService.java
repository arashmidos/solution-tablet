package com.conta.comer.service;

import android.location.Location;

import com.conta.comer.data.entity.Customer;
import com.conta.comer.data.entity.CustomerPic;
import com.conta.comer.data.entity.Payment;
import com.conta.comer.data.entity.VisitInformation;
import com.conta.comer.data.entity.VisitLine;
import com.conta.comer.data.listmodel.CustomerListModel;
import com.conta.comer.data.listmodel.NCustomerListModel;
import com.conta.comer.data.listmodel.PaymentListModel;
import com.conta.comer.data.listmodel.VisitLineListModel;
import com.conta.comer.data.model.CustomerDto;
import com.conta.comer.data.searchobject.NCustomerSO;
import com.conta.comer.data.searchobject.PaymentSO;

import java.io.File;
import java.util.List;

/**
 * Created by Arash on 08/19/2016
 */
public interface PaymentService
{
    Payment getPaymentById(Long paymentID);

    void savePayment(Payment payment);

    void updatePayment(Payment payment);

    List<Payment> getAllPaymentsByStatus(Long status);

    List<Payment> getAllPaymentByCustomerID(Long customerId);

    List<PaymentListModel> getAllPaymentListModelByCustomerIdWithConstraint(Long customerId, String constraint);

    List<PaymentListModel> getAllPaymentsListModelByCustomerBackendId(Long customerBackendId);

    List<PaymentListModel> searchForPayments(PaymentSO paymentSO);
}
