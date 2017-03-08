package com.parsroyal.solutiontablet.service.impl;

import android.content.Context;

import com.parsroyal.solutiontablet.data.dao.CustomerDao;
import com.parsroyal.solutiontablet.data.dao.PaymentDao;
import com.parsroyal.solutiontablet.data.dao.impl.CustomerDaoImpl;
import com.parsroyal.solutiontablet.data.dao.impl.PaymentDaoImpl;
import com.parsroyal.solutiontablet.data.entity.Payment;
import com.parsroyal.solutiontablet.data.listmodel.PaymentListModel;
import com.parsroyal.solutiontablet.data.searchobject.PaymentSO;
import com.parsroyal.solutiontablet.service.PaymentService;
import com.parsroyal.solutiontablet.util.DateUtil;
import com.parsroyal.solutiontablet.util.Empty;

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
    public long savePayment(Payment payment)
    {
        if (Empty.isEmpty(payment.getId()))
        {
            payment.setCreateDateTime(DateUtil.convertDate(new Date(), DateUtil.FULL_FORMATTER_GREGORIAN_WITH_TIME, "EN"));
            payment.setUpdateDateTime(DateUtil.convertDate(new Date(), DateUtil.FULL_FORMATTER_GREGORIAN_WITH_TIME, "EN"));
            return paymentDao.create(payment);
        } else
        {
            payment.setUpdateDateTime(new Date().toString());
            paymentDao.update(payment);
            return payment.getId();
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
