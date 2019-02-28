package com.parsroyal.storemanagement.biz.impl;

import android.content.Context;
import com.parsroyal.storemanagement.R;
import com.parsroyal.storemanagement.biz.AbstractDataTransferBizImpl;
import com.parsroyal.storemanagement.constants.SendStatus;
import com.parsroyal.storemanagement.constants.StatusCodes;
import com.parsroyal.storemanagement.constants.VisitInformationDetailType;
import com.parsroyal.storemanagement.data.entity.Payment;
import com.parsroyal.storemanagement.data.event.DataTransferErrorEvent;
import com.parsroyal.storemanagement.data.event.DataTransferSuccessEvent;
import com.parsroyal.storemanagement.service.PaymentService;
import com.parsroyal.storemanagement.service.SettingService;
import com.parsroyal.storemanagement.service.VisitService;
import com.parsroyal.storemanagement.service.impl.PaymentServiceImpl;
import com.parsroyal.storemanagement.service.impl.SettingServiceImpl;
import com.parsroyal.storemanagement.service.impl.VisitServiceImpl;
import com.parsroyal.storemanagement.ui.observer.ResultObserver;
import com.parsroyal.storemanagement.util.DateUtil;
import com.parsroyal.storemanagement.util.Empty;
import com.parsroyal.storemanagement.util.Logger;
import com.parsroyal.storemanagement.util.constants.ApplicationKeys;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import org.greenrobot.eventbus.EventBus;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;

/**
 * Created by Arash on 2016-08-21
 */
public class PaymentsDataTransferBizImpl extends AbstractDataTransferBizImpl<String> {

  public static final String TAG = PaymentsDataTransferBizImpl.class.getSimpleName();
  private final SettingService settingService;
  private Context context;
  private PaymentService paymentService;
  private VisitService visitService;
  private ResultObserver observer;
  private List<Payment> payments;

  public PaymentsDataTransferBizImpl(Context context) {
    super(context);
    this.context = context;
    paymentService = new PaymentServiceImpl(context);
    visitService = new VisitServiceImpl(context);
    settingService = new SettingServiceImpl();
  }

  @Override
  public void receiveData(String response) {
    if (Empty.isNotEmpty(response)) {
      try {
        String[] rows = response.split("[$]");
        int success = 0;
        int failure = 0;
        for (int i = 0; i < rows.length; i++) {
          String row = rows[i];
          String[] columns = row.split("[&]");
          long paymentId = Long.parseLong(columns[0]);
          long paymentBackendId = Long.parseLong(columns[1]);
          long updated = Long.parseLong(columns[2]);

          Payment payment = paymentService.getPaymentById(paymentId);
          if (Empty.isNotEmpty(payment) && updated == 1) {
            payment.setStatus(SendStatus.SENT.getId());
            payment.setBackendId(paymentBackendId);
            payment.setUpdateDateTime(DateUtil
                .convertDate(new Date(), DateUtil.FULL_FORMATTER_GREGORIAN_WITH_TIME, "EN"));
            paymentService.updatePayment(payment);
            visitService
                .updateVisitDetailId(VisitInformationDetailType.CASH, paymentId, paymentBackendId);
            success++;
          } else {
            failure++;
          }
        }

        if (payments.size() == success) {
          EventBus.getDefault().post(new DataTransferSuccessEvent(
              String.format(Locale.US,
                  context.getString(R.string.payments_data_transferred_successfully),
                  String.valueOf(success), String.valueOf(failure)),
              StatusCodes.SUCCESS));
        } else {
          EventBus.getDefault().post(new DataTransferErrorEvent(String.format(Locale.US,
              context.getString(R.string.payments_data_transferred_successfully),
              String.valueOf(success), String.valueOf(failure)), StatusCodes.DATA_STORE_ERROR));
        }
      } catch (Exception ex) {
        Logger.sendError("Data transfer", "Error in receiving PaymentData " + ex.getMessage());
        EventBus.getDefault().post(new DataTransferErrorEvent(context.getString(
            R.string.message_exception_in_data_store), StatusCodes.DATA_STORE_ERROR));
      }
    } else {
      EventBus.getDefault().post(new DataTransferErrorEvent(
          context.getString(R.string.message_got_no_response), StatusCodes.NO_DATA_ERROR));
    }
  }

  @Override
  public void beforeTransfer() {
  }

  @Override
  public ResultObserver getObserver() {
    return observer;
  }

  @Override
  public String getMethod() {
    return String
        .format("payment/%s/create", settingService.getSettingValue(ApplicationKeys.SALESMAN_ID));
  }

  @Override
  public Class getType() {
    return String.class;
  }

  @Override
  public HttpMethod getHttpMethod() {
    return HttpMethod.POST;
  }

  @Override
  protected MediaType getContentType() {
    return MediaType.APPLICATION_JSON;
  }

  @Override
  protected HttpEntity getHttpEntity(HttpHeaders headers) {
    return new HttpEntity<>(payments, headers);
  }

  public void setPayments(List<Payment> payments) {
    this.payments = payments;
  }
}
