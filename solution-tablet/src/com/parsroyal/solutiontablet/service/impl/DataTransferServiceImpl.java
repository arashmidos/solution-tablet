package com.parsroyal.solutiontablet.service.impl;

import android.content.Context;
import android.util.Log;
import com.parsroyal.solutiontablet.R;
import com.parsroyal.solutiontablet.biz.impl.CityDataTransferBizImpl;
import com.parsroyal.solutiontablet.biz.impl.DeliverableGoodsDataTransferBizImpl;
import com.parsroyal.solutiontablet.biz.impl.GoodsDataTransferBizImpl;
import com.parsroyal.solutiontablet.biz.impl.GoodsGroupDataTransferBizImpl;
import com.parsroyal.solutiontablet.biz.impl.GoodsRequestDataTransferBizImpl;
import com.parsroyal.solutiontablet.biz.impl.InvoicedOrdersDataTransferBizImpl;
import com.parsroyal.solutiontablet.biz.impl.NewCustomerDataTransferBizImpl;
import com.parsroyal.solutiontablet.biz.impl.NewCustomerPicDataTransferBizImpl;
import com.parsroyal.solutiontablet.biz.impl.OrdersDataTransferBizImpl;
import com.parsroyal.solutiontablet.biz.impl.PaymentsDataTransferBizImpl;
import com.parsroyal.solutiontablet.biz.impl.PositionDataTransferBizImpl;
import com.parsroyal.solutiontablet.biz.impl.ProvinceDataTransferBizImpl;
import com.parsroyal.solutiontablet.biz.impl.QAnswersDataTransferBizImpl;
import com.parsroyal.solutiontablet.biz.impl.QuestionnaireDataTransferBizImpl;
import com.parsroyal.solutiontablet.biz.impl.RejectedGoodsDataTransferBizImpl;
import com.parsroyal.solutiontablet.biz.impl.SaleRejectsDataTransferBizImpl;
import com.parsroyal.solutiontablet.biz.impl.SaleOrderForDeliveryDataTaransferBizImpl;
import com.parsroyal.solutiontablet.biz.impl.UpdatedCustomerLocationDataTransferBizImpl;
import com.parsroyal.solutiontablet.biz.impl.VisitInformationDataTransferBizImpl;
import com.parsroyal.solutiontablet.biz.impl.VisitLineDataTaransferBizImpl;
import com.parsroyal.solutiontablet.biz.impl.VisitLineForDeliveryDataTaransferBizImpl;
import com.parsroyal.solutiontablet.constants.Constants;
import com.parsroyal.solutiontablet.constants.SaleOrderStatus;
import com.parsroyal.solutiontablet.constants.SendStatus;
import com.parsroyal.solutiontablet.data.dao.KeyValueDao;
import com.parsroyal.solutiontablet.data.dao.impl.KeyValueDaoImpl;
import com.parsroyal.solutiontablet.data.entity.Customer;
import com.parsroyal.solutiontablet.data.entity.KeyValue;
import com.parsroyal.solutiontablet.data.entity.Payment;
import com.parsroyal.solutiontablet.data.entity.Position;
import com.parsroyal.solutiontablet.data.entity.QAnswer;
import com.parsroyal.solutiontablet.data.model.BaseSaleDocument;
import com.parsroyal.solutiontablet.data.model.CustomerLocationDto;
import com.parsroyal.solutiontablet.data.model.GoodsDtoList;
import com.parsroyal.solutiontablet.data.model.VisitInformationDto;
import com.parsroyal.solutiontablet.exception.BusinessException;
import com.parsroyal.solutiontablet.exception.InvalidServerAddressException;
import com.parsroyal.solutiontablet.exception.PasswordNotProvidedForConnectingToServerException;
import com.parsroyal.solutiontablet.exception.SalesmanIdNotProvidedForConnectingToServerException;
import com.parsroyal.solutiontablet.exception.UsernameNotProvidedForConnectingToServerException;
import com.parsroyal.solutiontablet.service.BaseInfoService;
import com.parsroyal.solutiontablet.service.CustomerService;
import com.parsroyal.solutiontablet.service.DataTransferService;
import com.parsroyal.solutiontablet.service.PaymentService;
import com.parsroyal.solutiontablet.service.PositionService;
import com.parsroyal.solutiontablet.service.QuestionnaireService;
import com.parsroyal.solutiontablet.service.SaleOrderService;
import com.parsroyal.solutiontablet.ui.observer.ResultObserver;
import com.parsroyal.solutiontablet.util.Empty;
import com.parsroyal.solutiontablet.util.MediaUtil;
import com.parsroyal.solutiontablet.util.Updater;
import com.parsroyal.solutiontablet.util.constants.ApplicationKeys;
import java.io.File;
import java.util.List;
import java.util.Locale;

/**
 * Created by Mahyar on 6/15/2015.
 */
public class DataTransferServiceImpl implements DataTransferService {

  public static final String TAG = DataTransferServiceImpl.class.getSimpleName();
  private static final int KEEP_ALIVE_TIME = 15;

  private Context context;
  private KeyValueDao keyValueDao;

  private CustomerService customerService;
  private QuestionnaireService questionnaireService;
  private SaleOrderService saleOrderService;
  private PaymentService paymentService;
  private PositionService positionService;
  private VisitServiceImpl visitService;

  private KeyValue serverAddress1;
  private KeyValue username;
  private KeyValue password;
  private KeyValue salesmanId;
  private KeyValue saleType;

  public DataTransferServiceImpl(Context context) {
    this.context = context;
    this.keyValueDao = new KeyValueDaoImpl(context);
    this.customerService = new CustomerServiceImpl(context);
    this.questionnaireService = new QuestionnaireServiceImpl(context);
    this.saleOrderService = new SaleOrderServiceImpl(context);
    this.paymentService = new PaymentServiceImpl(context);
    this.positionService = new PositionServiceImpl(context);
    this.visitService = new VisitServiceImpl(context);
  }

  @Override
  public void getAllData(final ResultObserver uiObserver) {
    serverAddress1 = keyValueDao.retrieveByKey(ApplicationKeys.SETTING_SERVER_ADDRESS_1);
    username = keyValueDao.retrieveByKey(ApplicationKeys.SETTING_USERNAME);
    password = keyValueDao.retrieveByKey(ApplicationKeys.SETTING_PASSWORD);
    salesmanId = keyValueDao.retrieveByKey(ApplicationKeys.SALESMAN_ID);
    saleType = keyValueDao.retrieveByKey(ApplicationKeys.SETTING_SALE_TYPE);

    if (Empty.isEmpty(serverAddress1)) {
      throw new InvalidServerAddressException();
    }

    if (Empty.isEmpty(username)) {
      throw new UsernameNotProvidedForConnectingToServerException();
    }

    if (Empty.isEmpty(password)) {
      throw new PasswordNotProvidedForConnectingToServerException();
    }

    if (Empty.isEmpty(salesmanId)) {
      throw new SalesmanIdNotProvidedForConnectingToServerException();
    }

    clearData(Constants.FULL_UPDATE);

    final ResultObserver resultObserver = prepareResultObserverForDataTransfer(uiObserver);

    if (saleType.getValue().equals(ApplicationKeys.SALE_DISTRIBUTER)) {
      new GoodsRequestDataTransferBizImpl(context, resultObserver).exchangeData();
    }

    getAllProvinces(resultObserver);
    getAllCities(resultObserver);
    getAllBaseInfos(resultObserver);
    getAllGoodsGroups(resultObserver);
    getAllQuestionnaires(resultObserver);

    if (saleType.getValue().equals(ApplicationKeys.SALE_DISTRIBUTER)) {
      getAllDeliverableGoods(resultObserver);
      getAllVisitLinesForDelivery(resultObserver);
      getAllOrdersForDelivery(resultObserver);
    } else {
      getAllGoods(resultObserver);
      getAllVisitLines(resultObserver);
    }

    uiObserver.finished(true);
  }

  public void getGoodsImages(final ResultObserver uiObserver) {
    serverAddress1 = keyValueDao.retrieveByKey(ApplicationKeys.SETTING_SERVER_ADDRESS_1);
    username = keyValueDao.retrieveByKey(ApplicationKeys.SETTING_USERNAME);
    password = keyValueDao.retrieveByKey(ApplicationKeys.SETTING_PASSWORD);
    salesmanId = keyValueDao.retrieveByKey(ApplicationKeys.SALESMAN_ID);
    saleType = keyValueDao.retrieveByKey(ApplicationKeys.SETTING_SALE_TYPE);

    if (Empty.isEmpty(serverAddress1)) {
      throw new InvalidServerAddressException();
    }

    if (Empty.isEmpty(username)) {
      throw new UsernameNotProvidedForConnectingToServerException();
    }

    if (Empty.isEmpty(password)) {
      throw new PasswordNotProvidedForConnectingToServerException();
    }

    if (Empty.isEmpty(salesmanId)) {
      throw new SalesmanIdNotProvidedForConnectingToServerException();
    }

    MediaUtil.clearGoodsFolder();

    Updater.downloadGoodsImages(context);
  }


  private void getAllProvinces(ResultObserver observer) {
    boolean success = false;
    for (int i = 0; i < 3 && !success; i++) {
      success = new ProvinceDataTransferBizImpl(context, observer).exchangeData();
    }
  }

  private void getAllCities(ResultObserver observer) {
    boolean success = false;
    for (int i = 0; i < 3 && !success; i++) {
      success = new CityDataTransferBizImpl(context, observer).exchangeData();
    }
  }

  private void getAllBaseInfos(ResultObserver resultObserver) {
    boolean success = false;
    for (int i = 0; i < 3 && !success; i++) {
      success = new BaseInfoDataTransferBizImpl(context, resultObserver).exchangeData();
    }
  }

  private void getAllGoodsGroups(ResultObserver resultObserver) {
    boolean success = false;
    for (int i = 0; i < 3 && !success; i++) {
      success = new GoodsGroupDataTransferBizImpl(context, resultObserver).exchangeData();
    }
  }

  private void getAllQuestionnaires(ResultObserver resultObserver) {
    boolean success = false;
    for (int i = 0; i < 3 && !success; i++) {
      success = new QuestionnaireDataTransferBizImpl(context, resultObserver).exchangeData();
    }
  }

  private void getAllDeliverableGoods(ResultObserver resultObserver) {
    boolean success = false;
    for (int i = 0; i < 3 && !success; i++) {
      success = new DeliverableGoodsDataTransferBizImpl(context, resultObserver).exchangeData();
    }
  }

  private void getAllVisitLinesForDelivery(ResultObserver resultObserver) {
    boolean success = false;
    for (int i = 0; i < 3 && !success; i++) {
      success = new VisitLineForDeliveryDataTaransferBizImpl(context, resultObserver)
          .exchangeData();
    }
  }

  private void getAllOrdersForDelivery(ResultObserver resultObserver) {
    boolean success = false;
    for (int i = 0; i < 3 && !success; i++) {
      success = new SaleOrderForDeliveryDataTaransferBizImpl(context, resultObserver)
          .exchangeData();
    }
  }

  private void getAllGoods(ResultObserver resultObserver) {
    boolean success = false;
    for (int i = 0; i < 3 && !success; i++) {
      success = new GoodsDataTransferBizImpl(context, resultObserver).exchangeData();
    }
  }

  private void getAllVisitLines(ResultObserver resultObserver) {
    boolean success = false;
    for (int i = 0; i < 3 && !success; i++) {
      success = new VisitLineDataTaransferBizImpl(context, resultObserver).exchangeData();
    }
  }

  @Override
  public void sendAllData(final ResultObserver uiObserver) {
    serverAddress1 = keyValueDao.retrieveByKey(ApplicationKeys.SETTING_SERVER_ADDRESS_1);
    username = keyValueDao.retrieveByKey(ApplicationKeys.SETTING_USERNAME);
    password = keyValueDao.retrieveByKey(ApplicationKeys.SETTING_PASSWORD);
    salesmanId = keyValueDao.retrieveByKey(ApplicationKeys.SALESMAN_ID);

    if (Empty.isEmpty(serverAddress1)) {
      throw new InvalidServerAddressException();
    }

    if (Empty.isEmpty(username)) {
      throw new UsernameNotProvidedForConnectingToServerException();
    }

    if (Empty.isEmpty(password)) {
      throw new PasswordNotProvidedForConnectingToServerException();
    }

    final ResultObserver resultObserver = prepareResultObserverForDataTransfer(uiObserver);

    sendAllNewCustomers(resultObserver);
    sendAllCustomerPics(resultObserver);
    sendAllUpdatedCustomers(resultObserver);
    sendAllPositions(resultObserver);
    sendAllAnswers(resultObserver);
    sendAllPayments(resultObserver);
    sendAllOrders(resultObserver);
    sendAllInvoicedOrders(resultObserver);
    sendAllSaleRejects(resultObserver);
    //Visit detail always should be the last one
    sendAllVisitInformation(resultObserver);
    uiObserver.finished(true);
  }


  private void sendAllNewCustomers(ResultObserver resultObserver) {
    List<Customer> allNewCustomers = customerService.getAllNewCustomersForSend();
    if (Empty.isEmpty(allNewCustomers)) {
      resultObserver
          .publishResult(context.getString(R.string.message_found_no_new_customer_for_send));
      return;
    }
    new NewCustomerDataTransferBizImpl(context, resultObserver).exchangeData();
  }

  private void sendAllCustomerPics(ResultObserver resultObserver) {

    File pics = customerService.getAllCustomerPicForSend();
    if (Empty.isEmpty(pics)) {
      resultObserver
          .publishResult(context.getString(R.string.message_found_no_new_customer_pic_for_send));
      return;
    } else {
      Log.d(TAG, "Send Pic" + pics.length());
    }

    new NewCustomerPicDataTransferBizImpl(context, resultObserver, pics).exchangeData();
  }

  private void sendAllUpdatedCustomers(ResultObserver resultObserver) {
    List<CustomerLocationDto> allUpdatedCustomerLocation = customerService
        .getAllUpdatedCustomerLocation();
    if (Empty.isEmpty(allUpdatedCustomerLocation)) {
      resultObserver
          .publishResult(context.getString(R.string.message_found_no_updated_customer_for_send));
      return;
    }
    new UpdatedCustomerLocationDataTransferBizImpl(context, resultObserver).exchangeData();
  }

  private void sendAllPositions(ResultObserver resultObserver) {
    List<Position> positions = positionService.getAllPositionByStatus(SendStatus.NEW.getId());
    if (Empty.isNotEmpty(positions)) {
      PositionDataTransferBizImpl positionDataTransferBiz = new PositionDataTransferBizImpl(context,
          resultObserver);
      positionDataTransferBiz.setPositions(positions);
      positionDataTransferBiz.sendAllData();

    } else {
      resultObserver.publishResult(context.getString(R.string.message_no_positions_for_sending));
    }
  }

  private void sendAllAnswers(ResultObserver resultObserver) {
    List<QAnswer> answersForSend = questionnaireService.getAllAnswersForSend();

    if (Empty.isEmpty(answersForSend)) {
      resultObserver.publishResult(context.getString(R.string.message_found_no_answer_for_send));
      return;
    }

    new QAnswersDataTransferBizImpl(context, resultObserver).exchangeData();
  }

  private void sendAllPayments(ResultObserver resultObserver) {
    List<Payment> payments = paymentService.getAllPaymentsByStatus(SendStatus.NEW.getId());
    if (Empty.isNotEmpty(payments)) {
      PaymentsDataTransferBizImpl paymentsDataTransferBiz = new PaymentsDataTransferBizImpl(context,
          resultObserver);
      paymentsDataTransferBiz.setPayments(payments);
      paymentsDataTransferBiz.exchangeData();
    } else {
      resultObserver.publishResult(context.getString(R.string.message_no_payments_for_sending));
    }
  }

  private void sendAllOrders(ResultObserver resultObserver) {
    List<BaseSaleDocument> saleOrders = saleOrderService
        .findOrderDocumentByStatus(SaleOrderStatus.READY_TO_SEND.getId());
    if (Empty.isNotEmpty(saleOrders)) {
      OrdersDataTransferBizImpl dataTransfer = new OrdersDataTransferBizImpl(context,
          resultObserver);
      resultObserver.publishResult(context.getString(R.string.message_transferring_orders));
      for (int i = 0; i < saleOrders.size(); i++) {
        BaseSaleDocument baseSaleDocument = saleOrders.get(i);
        dataTransfer.setOrder(baseSaleDocument);
        dataTransfer.exchangeData();
      }
      resultObserver.publishResult(dataTransfer.getSuccessfulMessage());
    } else {
      resultObserver.publishResult(context.getString(R.string.message_no_orders_for_sending));
    }
  }

  private void sendAllInvoicedOrders(ResultObserver resultObserver) {
    List<BaseSaleDocument> saleOrders = saleOrderService
        .findOrderDocumentByStatus(SaleOrderStatus.INVOICED.getId());
    if (Empty.isNotEmpty(saleOrders)) {
      InvoicedOrdersDataTransferBizImpl dataTransfer = new InvoicedOrdersDataTransferBizImpl(
          context, resultObserver);
      resultObserver.publishResult(context.getString(R.string.message_transferring_invoices));
      for (int i = 0; i < saleOrders.size(); i++) {
        BaseSaleDocument baseSaleDocument = saleOrders.get(i);
        dataTransfer.setOrder(baseSaleDocument);
        dataTransfer.exchangeData();
      }
      resultObserver.publishResult(dataTransfer.getSuccessfulMessage());
    } else {
      resultObserver.publishResult(context.getString(R.string.message_no_invoice_found));
    }
  }

  private void sendAllSaleRejects(ResultObserver resultObserver) {
    List<BaseSaleDocument> saleOrders = saleOrderService
        .findOrderDocumentByStatus(SaleOrderStatus.REJECTED.getId());
    if (Empty.isNotEmpty(saleOrders)) {
      SaleRejectsDataTransferBizImpl dataTransfer = new SaleRejectsDataTransferBizImpl(
          context, resultObserver);
      resultObserver
          .publishResult(context.getString(R.string.message_transferring_sale_rejects));
      for (int i = 0; i < saleOrders.size(); i++) {
        BaseSaleDocument baseSaleDocument = saleOrders.get(i);
        dataTransfer.setOrder(baseSaleDocument);
        dataTransfer.exchangeData();
      }
      resultObserver.publishResult(dataTransfer.getSuccessfulMessage());

    } else {
      resultObserver
          .publishResult(context.getString(R.string.message_no_sale_reject_for_sending));
    }
  }

  private void sendAllVisitInformation(ResultObserver resultObserver) {

    List<VisitInformationDto> visitInformationList = visitService.getAllVisitDetailForSend();
    if (Empty.isEmpty(visitInformationList)) {
      resultObserver
          .publishResult(context.getString(R.string.message_found_no_visit_information_for_send));
      return;
    }
    VisitInformationDataTransferBizImpl dataTransfer =
        new VisitInformationDataTransferBizImpl(context, resultObserver);
    resultObserver.publishResult(context.getString(R.string.sending_visit_information_data));
    for (int i = 0; i < visitInformationList.size(); i++) {
      VisitInformationDto visitInformationDto = visitInformationList.get(i);
      if (visitInformationDto.getDetails() == null
          || visitInformationDto.getDetails().size() == 0) {
        continue;
      }
      dataTransfer.setData(visitInformationDto);
      dataTransfer.exchangeData();
    }
    resultObserver.publishResult(String
        .format(Locale.US, context.getString(R.string.data_transfered_result),
            String.valueOf(dataTransfer.getSuccess()),
            String.valueOf(visitInformationList.size() - dataTransfer.getSuccess())));
  }

  private void cleanOldData() {
    paymentService.clearAllSentPayment();
    visitService.deleteAll();
  }

  public void clearData(int updateType) {
    if (updateType == Constants.FULL_UPDATE) {
      BaseInfoService infoService = new BaseInfoServiceImpl(context);
      infoService.deleteAll();
      infoService.deleteAllCities();
      infoService.deleteAllProvinces();
    }
    customerService.deleteAll();
    customerService.deleteAllPics();

    GoodsServiceImpl goodsService = new GoodsServiceImpl(context);
    goodsService.deleteAll();
    goodsService.deleteAllGoodsGroup();

    paymentService.deleteAll();
    visitService.deleteAll();
    questionnaireService.deleteAll();
    saleOrderService.deleteAll();
    positionService.deleteAll();
  }

  @Override
  public boolean isDataTransferPossible() {
    serverAddress1 = keyValueDao.retrieveByKey(ApplicationKeys.SETTING_SERVER_ADDRESS_1);
    username = keyValueDao.retrieveByKey(ApplicationKeys.SETTING_USERNAME);
    password = keyValueDao.retrieveByKey(ApplicationKeys.SETTING_PASSWORD);
    salesmanId = keyValueDao.retrieveByKey(ApplicationKeys.SALESMAN_ID);

    if (Empty.isEmpty(serverAddress1) || Empty.isEmpty(username) ||
        Empty.isEmpty(password) || Empty.isEmpty(salesmanId)) {
      return false;
    } else {
      return true;
    }
  }

  @Override
  public GoodsDtoList getRejectedData(ResultObserver uiObserver, Long customerId) {
    serverAddress1 = keyValueDao.retrieveByKey(ApplicationKeys.SETTING_SERVER_ADDRESS_1);
    username = keyValueDao.retrieveByKey(ApplicationKeys.SETTING_USERNAME);
    password = keyValueDao.retrieveByKey(ApplicationKeys.SETTING_PASSWORD);
    salesmanId = keyValueDao.retrieveByKey(ApplicationKeys.SALESMAN_ID);
    if (Empty.isEmpty(serverAddress1)) {
      throw new InvalidServerAddressException();
    }

    if (Empty.isEmpty(username)) {
      throw new UsernameNotProvidedForConnectingToServerException();
    }

    if (Empty.isEmpty(password)) {
      throw new PasswordNotProvidedForConnectingToServerException();
    }

    if (Empty.isEmpty(salesmanId)) {
      throw new SalesmanIdNotProvidedForConnectingToServerException();
    }

    final ResultObserver resultObserver = prepareResultObserverForDataTransfer(uiObserver);

    return getAllRejectedGoods(resultObserver, customerId);
  }

  private GoodsDtoList getAllRejectedGoods(ResultObserver observer, Long customerId) {
    return new RejectedGoodsDataTransferBizImpl(context, observer)
        .getAllRejectedData(serverAddress1, username, password, salesmanId, customerId);
  }

  private ResultObserver prepareResultObserverForDataTransfer(final ResultObserver uiObserver) {
    ResultObserver resultObserver = new ResultObserver() {
      @Override
      public void publishResult(BusinessException ex) {
        uiObserver.publishResult(ex);
      }

      @Override
      public void publishResult(String message) {
        uiObserver.publishResult(message);
      }

      @Override
      public void finished(boolean result) {
      }
    };
    return resultObserver;
  }


}
