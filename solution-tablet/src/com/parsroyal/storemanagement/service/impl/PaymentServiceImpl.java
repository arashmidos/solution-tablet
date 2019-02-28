package com.parsroyal.storemanagement.service.impl;

import android.content.Context;
import com.parsroyal.storemanagement.constants.VisitInformationDetailType;
import com.parsroyal.storemanagement.data.dao.CustomerDao;
import com.parsroyal.storemanagement.data.dao.PaymentDao;
import com.parsroyal.storemanagement.data.dao.VisitInformationDetailDao;
import com.parsroyal.storemanagement.data.dao.impl.CustomerDaoImpl;
import com.parsroyal.storemanagement.data.dao.impl.PaymentDaoImpl;
import com.parsroyal.storemanagement.data.dao.impl.VisitInformationDetailDaoImpl;
import com.parsroyal.storemanagement.data.entity.Payment;
import com.parsroyal.storemanagement.data.listmodel.PaymentListModel;
import com.parsroyal.storemanagement.data.searchobject.PaymentSO;
import com.parsroyal.storemanagement.service.PaymentService;
import com.parsroyal.storemanagement.util.DateUtil;
import com.parsroyal.storemanagement.util.Empty;
import java.util.Date;
import java.util.List;

/**
 * Edited by Arash on 8/19/2016
 */
public class PaymentServiceImpl implements PaymentService {

  private Context context;
  private PaymentDao paymentDao;
  private CustomerDao customerDao;

  public PaymentServiceImpl(Context context) {
    this.context = context;
    this.paymentDao = new PaymentDaoImpl(context);
    this.customerDao = new CustomerDaoImpl(context);
  }

  @Override
  public Payment getPaymentById(Long paymentID) {
    return paymentDao.retrieve(paymentID);
  }

  @Override
  public long savePayment(Payment payment) {
    if (Empty.isEmpty(payment.getId())) {
      payment.setCreateDateTime(
          DateUtil.convertDate(new Date(), DateUtil.FULL_FORMATTER_GREGORIAN_WITH_TIME, "EN"));
      payment.setUpdateDateTime(
          DateUtil.convertDate(new Date(), DateUtil.FULL_FORMATTER_GREGORIAN_WITH_TIME, "EN"));
      return paymentDao.create(payment);
    } else {
      payment.setUpdateDateTime(
          DateUtil.convertDate(new Date(), DateUtil.FULL_FORMATTER_GREGORIAN_WITH_TIME, "EN"));
      paymentDao.update(payment);
      return payment.getId();
    }
  }

  @Override
  public void updatePayment(Payment payment) {
    paymentDao.update(payment);
  }

  @Override
  public List<Payment> getAllPaymentsByStatus(Long status) {
    return paymentDao.findPaymentsByStatusId(status);
  }

  @Override
  public List<Payment> getAllPaymentsByVisitId(Long visitId) {
    return paymentDao.findPaymentsByVisitId(visitId);
  }

  @Override
  public List<PaymentListModel> searchForPayments(PaymentSO paymentSO) {
    return paymentDao.searchForPayments(paymentSO);
  }

  @Override
  public void clearAllSentPayment() {
    paymentDao.deleteAllSentPayment();
  }

  @Override
  public void deleteAll() {
    paymentDao.deleteAll();
  }

  @Override
  public void deletePayment(Long paymentId) {
    paymentDao.delete(paymentId);

    //Delete Payment detail
    VisitInformationDetailDao visitInformationDetailDao = new VisitInformationDetailDaoImpl(
        context);
    visitInformationDetailDao.deleteVisitDetail(VisitInformationDetailType.CASH, paymentId);
  }
}
