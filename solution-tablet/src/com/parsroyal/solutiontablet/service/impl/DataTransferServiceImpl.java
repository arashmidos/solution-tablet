package com.parsroyal.solutiontablet.service.impl;

import android.content.Context;
import android.util.Log;
import com.parsroyal.solutiontablet.R;
import com.parsroyal.solutiontablet.biz.impl.CanceledOrdersDataTransfer;
import com.parsroyal.solutiontablet.biz.impl.CityDataTransferBizImpl;
import com.parsroyal.solutiontablet.biz.impl.DeliverableGoodsDataTransferBizImpl;
import com.parsroyal.solutiontablet.biz.impl.GoodsDataTransferBizImpl;
import com.parsroyal.solutiontablet.biz.impl.GoodsGroupDataTransferBizImpl;
import com.parsroyal.solutiontablet.biz.impl.GoodsRequestDataTransferBizImpl;
import com.parsroyal.solutiontablet.biz.impl.InvoicedOrdersDataTransfer;
import com.parsroyal.solutiontablet.biz.impl.NewCustomerDataTransferBizImpl;
import com.parsroyal.solutiontablet.biz.impl.NewCustomerPicDataTransferBizImpl;
import com.parsroyal.solutiontablet.biz.impl.OrdersDataTransferBizImpl;
import com.parsroyal.solutiontablet.biz.impl.PaymentsDataTransferBizImpl;
import com.parsroyal.solutiontablet.biz.impl.PositionDataTransferBizImpl;
import com.parsroyal.solutiontablet.biz.impl.ProvinceDataTransferBizImpl;
import com.parsroyal.solutiontablet.biz.impl.QAnswersDataTransferBizImpl;
import com.parsroyal.solutiontablet.biz.impl.QuestionnaireDataTransferBizImpl;
import com.parsroyal.solutiontablet.biz.impl.SaleOrderForDeliveryDataTaransferBizImpl;
import com.parsroyal.solutiontablet.biz.impl.SaleRejectsDataTransferBizImpl;
import com.parsroyal.solutiontablet.biz.impl.UpdatedCustomerLocationDataTransferBizImpl;
import com.parsroyal.solutiontablet.biz.impl.VisitInformationDataTransferBizImpl;
import com.parsroyal.solutiontablet.biz.impl.VisitLineDataTaransferBizImpl;
import com.parsroyal.solutiontablet.biz.impl.VisitLineForDeliveryDataTaransferBizImpl;
import com.parsroyal.solutiontablet.constants.Constants;
import com.parsroyal.solutiontablet.constants.SaleOrderStatus;
import com.parsroyal.solutiontablet.constants.SendStatus;
import com.parsroyal.solutiontablet.constants.StatusCodes;
import com.parsroyal.solutiontablet.data.dao.KeyValueDao;
import com.parsroyal.solutiontablet.data.dao.impl.KeyValueDaoImpl;
import com.parsroyal.solutiontablet.data.entity.KeyValue;
import com.parsroyal.solutiontablet.data.entity.Payment;
import com.parsroyal.solutiontablet.data.event.DataTransferErrorEvent;
import com.parsroyal.solutiontablet.data.event.DataTransferSuccessEvent;
import com.parsroyal.solutiontablet.data.model.BaseSaleDocument;
import com.parsroyal.solutiontablet.data.model.CustomerDto;
import com.parsroyal.solutiontablet.data.model.CustomerLocationDto;
import com.parsroyal.solutiontablet.data.model.PositionDto;
import com.parsroyal.solutiontablet.data.model.QAnswerDto;
import com.parsroyal.solutiontablet.data.model.VisitInformationDto;
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
import com.parsroyal.solutiontablet.service.SettingService;
import com.parsroyal.solutiontablet.ui.observer.ResultObserver;
import com.parsroyal.solutiontablet.util.Empty;
import com.parsroyal.solutiontablet.util.constants.ApplicationKeys;
import java.io.File;
import java.util.List;
import java.util.Locale;
import org.greenrobot.eventbus.EventBus;

/**
 * Created by Arash 29/12/2017
 */
public class DataTransferServiceImpl implements DataTransferService {

  public static final String TAG = DataTransferServiceImpl.class.getSimpleName();
  private final SettingService settingService;
  private Context context;
  private KeyValueDao keyValueDao;
  private CustomerService customerService;
  private QuestionnaireService questionnaireService;
  private SaleOrderService saleOrderService;
  private PaymentService paymentService;
  private PositionService positionService;
  private VisitServiceImpl visitService;

  private KeyValue backendUri;
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
    this.settingService = new SettingServiceImpl(context);
  }

  public void getAllData() {
    backendUri = keyValueDao.retrieveByKey(ApplicationKeys.BACKEND_URI);
    username = keyValueDao.retrieveByKey(ApplicationKeys.SETTING_USERNAME);
    password = keyValueDao.retrieveByKey(ApplicationKeys.SETTING_PASSWORD);
    salesmanId = keyValueDao.retrieveByKey(ApplicationKeys.SALESMAN_ID);
    saleType = keyValueDao.retrieveByKey(ApplicationKeys.SETTING_SALE_TYPE);

    if (Empty.isEmpty(backendUri)) {
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

    if (saleType.getValue().equals(ApplicationKeys.SALE_DISTRIBUTER)) {
      new GoodsRequestDataTransferBizImpl(context).exchangeData();
    }

    getAllProvinces();
    getAllCities();
    getAllBaseInfos();
    getAllGoodsGroups();
    getAllQuestionnaires();

    if (saleType.getValue().equals(ApplicationKeys.SALE_DISTRIBUTER)) {
      getAllDeliverableGoods();
      getAllVisitLinesForDelivery();
      getAllOrdersForDelivery();
    } else {
      getAllGoods();
      getAllVisitLines();
    }

//    uiObserver.finished(true);
  }

  @Override
  public boolean hasUnsentData() {
    List<QAnswerDto> answersForSend = questionnaireService.getAllAnswersDtoForSend();
    List<BaseSaleDocument> saleOrders = saleOrderService
        .findOrderDocumentByStatus(SaleOrderStatus.READY_TO_SEND.getId());
    List<CustomerDto> allNewCustomers = customerService.getAllNewCustomersForSend();
//TODO CHECK FOR UNSENT VISIT DATA
    return Empty.isNotEmpty(answersForSend) || Empty.isNotEmpty(saleOrders) || Empty
        .isNotEmpty(allNewCustomers);
  }

  public void getAllProvinces() {
    try {
      new ProvinceDataTransferBizImpl(context).getAllProvinces();
    } catch (Exception ex) {
      EventBus.getDefault().post(new DataTransferErrorEvent(StatusCodes.SERVER_ERROR));
    }
  }

  public void getAllCities() {
    try {
      new CityDataTransferBizImpl(context).getAllCities();
    } catch (Exception ex) {
      EventBus.getDefault().post(new DataTransferErrorEvent(StatusCodes.SERVER_ERROR));
    }
  }

  public void getAllBaseInfos() {
    try {
      new BaseInfoDataTransferBizImpl(context).exchangeData();
    } catch (Exception ex) {
      EventBus.getDefault().post(new DataTransferErrorEvent(StatusCodes.SERVER_ERROR));
    }
  }

  public void getAllGoodsGroups() {
    try {
      new GoodsGroupDataTransferBizImpl(context).exchangeData();
    } catch (Exception ex) {
      EventBus.getDefault().post(new DataTransferErrorEvent(StatusCodes.SERVER_ERROR));
    }
  }

  public void getAllQuestionnaires() {
    try {
      new QuestionnaireDataTransferBizImpl(context).exchangeData();
    } catch (Exception ex) {
      EventBus.getDefault().post(new DataTransferErrorEvent(StatusCodes.SERVER_ERROR));
    }
  }

  public void getAllDeliverableGoods() {
    try {
      new DeliverableGoodsDataTransferBizImpl(context).exchangeData();
    } catch (Exception ex) {
      EventBus.getDefault().post(new DataTransferErrorEvent(StatusCodes.SERVER_ERROR));
    }
  }

  public void getAllVisitLinesForDelivery() {
    try {
      new VisitLineForDeliveryDataTaransferBizImpl(context).exchangeData();
    } catch (Exception ex) {
      EventBus.getDefault().post(new DataTransferErrorEvent(StatusCodes.SERVER_ERROR));
    }
  }

  public void getAllOrdersForDelivery() {
    try {
      new SaleOrderForDeliveryDataTaransferBizImpl(context).exchangeData();
    } catch (Exception ex) {
      EventBus.getDefault().post(new DataTransferErrorEvent(StatusCodes.SERVER_ERROR));
    }
  }

  public void getGoodRequest() {
    try {
      new GoodsRequestDataTransferBizImpl(context).exchangeData();
    } catch (Exception ex) {
      EventBus.getDefault().post(new DataTransferErrorEvent(StatusCodes.SERVER_ERROR));
    }
  }

  public void getAllGoods() {
    try {
      new GoodsDataTransferBizImpl(context).exchangeData();
    } catch (Exception ex) {
      EventBus.getDefault().post(new DataTransferErrorEvent(StatusCodes.SERVER_ERROR));
    }
  }

  public void getAllVisitLines() {
    try {
      new VisitLineDataTaransferBizImpl(context).exchangeData();
    } catch (Exception ex) {
      EventBus.getDefault().post(new DataTransferErrorEvent(StatusCodes.SERVER_ERROR));
    }
  }

  @Override
  public void sendAllData(final ResultObserver uiObserver) {
    backendUri = keyValueDao.retrieveByKey(ApplicationKeys.BACKEND_URI);
    username = keyValueDao.retrieveByKey(ApplicationKeys.SETTING_USERNAME);
    password = keyValueDao.retrieveByKey(ApplicationKeys.SETTING_PASSWORD);
    salesmanId = keyValueDao.retrieveByKey(ApplicationKeys.SALESMAN_ID);

    if (Empty.isEmpty(backendUri)) {
      throw new InvalidServerAddressException();
    }

    if (Empty.isEmpty(username)) {
      throw new UsernameNotProvidedForConnectingToServerException();
    }

    if (Empty.isEmpty(password)) {
      throw new PasswordNotProvidedForConnectingToServerException();
    }

    sendAllNewCustomers();
    sendAllUpdatedCustomers();
    sendAllPositions();
    sendAllAnswers();
    sendAllPayments();
    sendAllOrders();
    sendAllInvoicedOrders();
    sendAllSaleRejects();
    //Visit detail always should be the last one
    sendAllCustomerPics();
    sendAllVisitInformation();
  }

  public void sendAllNewCustomers() {
    List<CustomerDto> allNewCustomers = customerService.getAllNewCustomersForSend();
    if (Empty.isEmpty(allNewCustomers)) {
      EventBus.getDefault().post(new DataTransferSuccessEvent(context.getString(
          R.string.message_found_no_new_customer_for_send), StatusCodes.NO_DATA_ERROR));
      return;
    }

    NewCustomerDataTransferBizImpl newCustomerDataTransferBiz = new NewCustomerDataTransferBizImpl(
        context);

    for (int i = 0; i < allNewCustomers.size(); i++) {
      CustomerDto customerDto = allNewCustomers.get(i);
      newCustomerDataTransferBiz.setCustomer(customerDto);
      boolean isSuccess = newCustomerDataTransferBiz.exchangeData();

      if (isSuccess) {
        File pics = customerService.getAllCustomerPicForSendByCustomerId(customerDto.getId());
        if (Empty.isEmpty(pics)) {
          continue;
        }

        Log.d(TAG, "Send Pic" + pics.length());
        new NewCustomerPicDataTransferBizImpl(context, pics, null, customerDto.getId())
            .exchangeData();
      }
    }

    EventBus.getDefault().post(new DataTransferSuccessEvent(
        newCustomerDataTransferBiz.getSuccessfulMessage(), StatusCodes.SUCCESS));
  }

  public void sendAllUpdatedCustomers() {
    List<CustomerLocationDto> allUpdatedCustomerLocation = customerService
        .getAllUpdatedCustomerLocation();
    if (Empty.isEmpty(allUpdatedCustomerLocation)) {
      EventBus.getDefault().post(new DataTransferSuccessEvent(
          context.getString(R.string.message_found_no_new_customer_for_send),
          StatusCodes.NO_DATA_ERROR));
      return;
    }
    boolean success = new UpdatedCustomerLocationDataTransferBizImpl(context).exchangeData();
    if (!success) {
      EventBus.getDefault().post(new DataTransferErrorEvent(StatusCodes.SERVER_ERROR));
    }
  }

  public void sendAllPositions() {
    List<PositionDto> positions = positionService.getAllPositionDtoByStatus(SendStatus.NEW.getId());
    if (Empty.isNotEmpty(positions)) {
      PositionDataTransferBizImpl positionDataTransferBiz = new PositionDataTransferBizImpl(
          context);

      for (int i = 0; i < positions.size(); i++) {
        PositionDto positionDto = positions.get(i);
        positionDataTransferBiz.setPosition(positionDto);
        positionDataTransferBiz.sendAllData();
      }
      EventBus.getDefault().post(new DataTransferSuccessEvent(
          positionDataTransferBiz.getSuccessfulMessage(), StatusCodes.SUCCESS));
    } else {
      EventBus.getDefault().post(new DataTransferSuccessEvent(context.getString(
          R.string.message_no_positions_for_sending), StatusCodes.NO_DATA_ERROR));
    }
  }

  public void sendAllAnswers() {
    List<QAnswerDto> answersForSend = questionnaireService.getAllAnswersDtoForSend();

    if (Empty.isEmpty(answersForSend)) {
      EventBus.getDefault().post(new DataTransferSuccessEvent(context.getString(
          R.string.message_found_no_answer_for_send), StatusCodes.NO_DATA_ERROR));
      return;
    }

    QAnswersDataTransferBizImpl dataTransferBiz = new QAnswersDataTransferBizImpl(context);

    for (int i = 0; i < answersForSend.size(); i++) {
      QAnswerDto qAnswerDto = answersForSend.get(i);
      dataTransferBiz.setAnswer(qAnswerDto);
      dataTransferBiz.exchangeData();
    }
    EventBus.getDefault().post(new DataTransferSuccessEvent(
        dataTransferBiz.getSuccessfulMessage(), StatusCodes.SUCCESS));
  }

  public void sendAllPayments() {
    List<Payment> payments = paymentService.getAllPaymentsByStatus(SendStatus.NEW.getId());
    if (Empty.isNotEmpty(payments)) {
      PaymentsDataTransferBizImpl dataTransferBiz = new PaymentsDataTransferBizImpl(context);
      dataTransferBiz.setPayments(payments);
      boolean success = dataTransferBiz.exchangeData();
      if (!success) {
        EventBus.getDefault().post(new DataTransferErrorEvent(StatusCodes.SERVER_ERROR));
      }
    } else {
      EventBus.getDefault().post(new DataTransferSuccessEvent(context.getString(
          R.string.message_no_payments_for_sending), StatusCodes.NO_DATA_ERROR));
    }
  }

  public void sendAllOrders() {
    List<BaseSaleDocument> saleOrders = saleOrderService
        .findOrderDocumentByStatus(SaleOrderStatus.READY_TO_SEND.getId());
    if (Empty.isNotEmpty(saleOrders)) {
      OrdersDataTransferBizImpl dataTransfer = new OrdersDataTransferBizImpl(context);

      for (int i = 0; i < saleOrders.size(); i++) {
        BaseSaleDocument baseSaleDocument = saleOrders.get(i);
        dataTransfer.setOrder(baseSaleDocument);
        dataTransfer.exchangeData();
      }

      EventBus.getDefault().post(new DataTransferSuccessEvent(
          dataTransfer.getSuccessfulMessage(), StatusCodes.SUCCESS));
    } else {
      EventBus.getDefault().post(new DataTransferSuccessEvent(context.getString(
          R.string.message_no_orders_for_sending), StatusCodes.NO_DATA_ERROR));
    }
  }

  public void sendAllInvoicedOrders() {
    saleType = keyValueDao.retrieveByKey(ApplicationKeys.SETTING_SALE_TYPE);

    List<BaseSaleDocument> saleOrders;
    if (ApplicationKeys.SALE_DISTRIBUTER.equals(saleType.getValue())) {
      saleOrders = saleOrderService.findOrderDocumentByStatus(SaleOrderStatus.DELIVERED.getId());
    } else {
      saleOrders = saleOrderService.findOrderDocumentByStatus(SaleOrderStatus.INVOICED.getId());
    }
    if (Empty.isNotEmpty(saleOrders)) {
      InvoicedOrdersDataTransfer dataTransfer = new InvoicedOrdersDataTransfer(context);
      for (int i = 0; i < saleOrders.size(); i++) {
        BaseSaleDocument baseSaleDocument = saleOrders.get(i);
        /*if (ApplicationKeys.SALE_DISTRIBUTER.equals(saleType.getValue())) {
          baseSaleDocument.setStockCode(
              Integer.valueOf(settingService.getSettingValue(ApplicationKeys.SETTING_STOCK_CODE)));
          baseSaleDocument.setOfficeCode(
              Integer.valueOf(settingService.getSettingValue(ApplicationKeys.SETTING_BRANCH_CODE)));
        }*/
        dataTransfer.setOrder(baseSaleDocument);
        dataTransfer.exchangeData();
      }
      EventBus.getDefault().post(new DataTransferSuccessEvent(
          dataTransfer.getSuccessfulMessage(), StatusCodes.SUCCESS));
    } else {
      EventBus.getDefault().post(new DataTransferSuccessEvent(context.getString(
          R.string.message_no_invoice_found), StatusCodes.NO_DATA_ERROR));
    }
  }

  public void sendAllCanceledOrders() {

    List<BaseSaleDocument> saleOrders = saleOrderService
        .findOrderDocumentByStatus(SaleOrderStatus.CANCELED.getId());

    if (Empty.isNotEmpty(saleOrders)) {
      CanceledOrdersDataTransfer dataTransfer = new CanceledOrdersDataTransfer(context);
      for (int i = 0; i < saleOrders.size(); i++) {
        BaseSaleDocument baseSaleDocument = saleOrders.get(i);
        dataTransfer.setOrder(baseSaleDocument);
        dataTransfer.exchangeData();
      }
      EventBus.getDefault().post(new DataTransferSuccessEvent(
          dataTransfer.getSuccessfulMessage(), StatusCodes.SUCCESS));
    } else {
      EventBus.getDefault().post(new DataTransferSuccessEvent(context.getString(
          R.string.message_no_invoice_found), StatusCodes.NO_DATA_ERROR));
    }
  }

  public void sendAllSaleRejects() {
    List<BaseSaleDocument> saleOrders = saleOrderService
        .findOrderDocumentByStatus(SaleOrderStatus.REJECTED.getId());
    if (Empty.isNotEmpty(saleOrders)) {
      SaleRejectsDataTransferBizImpl dataTransfer = new SaleRejectsDataTransferBizImpl(context);

      for (int i = 0; i < saleOrders.size(); i++) {
        BaseSaleDocument baseSaleDocument = saleOrders.get(i);
        dataTransfer.setOrder(baseSaleDocument);
        dataTransfer.exchangeData();
      }
      EventBus.getDefault().post(new DataTransferSuccessEvent(
          dataTransfer.getSuccessfulMessage(), StatusCodes.SUCCESS));

    } else {
      EventBus.getDefault().post(new DataTransferSuccessEvent(context.getString(
          R.string.message_no_sale_reject_for_sending), StatusCodes.NO_DATA_ERROR));
    }
  }

  public void sendAllCustomerPics() {

    File pics = customerService.getAllCustomerPicForSend();
    if (Empty.isEmpty(pics)) {
      EventBus.getDefault().post(new DataTransferSuccessEvent(context.getString(
          R.string.message_found_no_new_customer_pic_for_send), StatusCodes.NO_DATA_ERROR));
      return;
    } else {
      Log.d(TAG, "Send Pic" + pics.length());
    }

    new NewCustomerPicDataTransferBizImpl(context, pics, null, null).exchangeData();
  }

  public void sendAllVisitInformation() {

    List<VisitInformationDto> visitInformationList = visitService.getAllVisitDetailForSend(null);
    if (Empty.isEmpty(visitInformationList)) {
      EventBus.getDefault().post(new DataTransferSuccessEvent(context.getString(
          R.string.message_found_no_new_customer_pic_for_send), StatusCodes.NO_DATA_ERROR));
      return;
    }
    VisitInformationDataTransferBizImpl dataTransfer = new VisitInformationDataTransferBizImpl(
        context);

    int emptyVisit = 0;
    for (int i = 0; i < visitInformationList.size(); i++) {
      VisitInformationDto visitInformationDto = visitInformationList.get(i);
      if (visitInformationDto.getDetails() == null
          || visitInformationDto.getDetails().size() == 0) {
        emptyVisit++;
        visitService.deleteVisitById(visitInformationDto.getId());
        continue;
      }
      dataTransfer.setData(visitInformationDto);
      dataTransfer.exchangeData();
    }
    EventBus.getDefault().post(new DataTransferSuccessEvent(String
        .format(Locale.US, context.getString(R.string.data_transfered_result),
            String.valueOf(dataTransfer.getSuccess()),
            String.valueOf(visitInformationList.size() - dataTransfer.getSuccess() - emptyVisit)),
        StatusCodes.SUCCESS));
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
    backendUri = keyValueDao.retrieveByKey(ApplicationKeys.BACKEND_URI);
    username = keyValueDao.retrieveByKey(ApplicationKeys.SETTING_USERNAME);
    password = keyValueDao.retrieveByKey(ApplicationKeys.SETTING_PASSWORD);
    salesmanId = keyValueDao.retrieveByKey(ApplicationKeys.SALESMAN_ID);

    return !(Empty.isEmpty(backendUri) || Empty.isEmpty(username) ||
        Empty.isEmpty(password) || Empty.isEmpty(salesmanId));
  }
}
