package com.conta.comer.service.impl;

import android.content.Context;

import com.conta.comer.constants.CustomerStatus;
import com.conta.comer.data.dao.CustomerDao;
import com.conta.comer.data.dao.PaymentDao;
import com.conta.comer.data.dao.impl.CustomerDaoImpl;
import com.conta.comer.data.dao.impl.PaymentDaoImpl;
import com.conta.comer.data.entity.Payment;
import com.conta.comer.data.listmodel.PaymentListModel;
import com.conta.comer.data.searchobject.PaymentSO;
import com.conta.comer.service.PaymentService;
import com.conta.comer.util.DateUtil;
import com.conta.comer.util.Empty;

import java.util.Date;
import java.util.List;

/**
 * Edited by Arash on 8/19/2016
 */
public class PaymentServiceImpl implements PaymentService
{

    private Context context;
    private PaymentDao paymentDao;
    private CustomerDao customerDao;

    public PaymentServiceImpl(Context context)
    {
        this.context = context;
        this.paymentDao = new PaymentDaoImpl(context);
        this.customerDao = new CustomerDaoImpl(context);
    }

    @Override
    public Payment getPaymentById(Long paymentID)
    {
        return paymentDao.retrieve(paymentID);
    }

    @Override
    public void savePayment(Payment payment)
    {
        if (Empty.isEmpty(payment.getId()))
        {
            payment.setCreateDateTime(DateUtil.convertDate(new Date(), DateUtil.FULL_FORMATTER_GREGORIAN_WITH_TIME, "EN"));
            payment.setUpdateDateTime(DateUtil.convertDate(new Date(), DateUtil.FULL_FORMATTER_GREGORIAN_WITH_TIME, "EN"));
            paymentDao.create(payment);
        } else
        {
            payment.setUpdateDateTime(new Date().toString());
            paymentDao.update(payment);
        }
    }

    @Override
    public void updatePayment(Payment payment)
    {
        paymentDao.update(payment);
    }

    @Override
    public List<Payment> getAllPaymentsByStatus(Long status)
    {
        return paymentDao.findPaymentsByStatusId(status);
    }

    @Override
    public List<Payment> getAllPaymentByCustomerID(Long customerId)
    {
        return paymentDao.findPaymentByCustomerId(customerId);
    }

    @Override
    public List<PaymentListModel> getAllPaymentListModelByCustomerIdWithConstraint(Long customerId, String constraint)
    {
        return paymentDao.getAllPaymentListModelByCustomerIdWithConstraint(customerId, constraint);
    }

    @Override
    public List<PaymentListModel> getAllPaymentsListModelByCustomerBackendId(Long customerBackendId)
    {
        return paymentDao.getAllPaymentListModelByCustomerIdWithConstraint(customerBackendId, "");
    }

    @Override
    public List<PaymentListModel> searchForPayments(PaymentSO paymentSO)
    {
        return paymentDao.searchForPayments(paymentSO);
    }

}
