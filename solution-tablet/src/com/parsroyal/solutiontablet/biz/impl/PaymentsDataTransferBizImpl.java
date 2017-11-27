package com.parsroyal.solutiontablet.biz.impl;

import android.content.Context;
import com.parsroyal.solutiontablet.R;
import com.parsroyal.solutiontablet.biz.AbstractDataTransferBizImpl;
import com.parsroyal.solutiontablet.constants.SendStatus;
import com.parsroyal.solutiontablet.constants.StatusCodes;
import com.parsroyal.solutiontablet.constants.VisitInformationDetailType;
import com.parsroyal.solutiontablet.data.entity.Payment;
import com.parsroyal.solutiontablet.data.event.ErrorEvent;
import com.parsroyal.solutiontablet.data.event.SendOrderEvent;
import com.parsroyal.solutiontablet.service.PaymentService;
import com.parsroyal.solutiontablet.service.SettingService;
import com.parsroyal.solutiontablet.service.VisitService;
import com.parsroyal.solutiontablet.service.impl.PaymentServiceImpl;
import com.parsroyal.solutiontablet.service.impl.SettingServiceImpl;
import com.parsroyal.solutiontablet.service.impl.VisitServiceImpl;
import com.parsroyal.solutiontablet.ui.observer.ResultObserver;
import com.parsroyal.solutiontablet.util.DateUtil;
import com.parsroyal.solutiontablet.util.Empty;
import com.parsroyal.solutiontablet.util.Logger;
import com.parsroyal.solutiontablet.util.NumberUtil;
import com.parsroyal.solutiontablet.util.constants.ApplicationKeys;
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

  public PaymentsDataTransferBizImpl(Context context, ResultObserver resultObserver) {
    super(context);
    this.context = context;
    paymentService = new PaymentServiceImpl(context);
    visitService = new VisitServiceImpl(context);
    settingService = new SettingServiceImpl(context);
    observer = resultObserver;
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
        if (Empty.isNotEmpty(observer)) {
          getObserver().publishResult(NumberUtil.digitsToPersian(String
              .format(Locale.US, context.getString(R.string.payments_data_transferred_successfully),
                  String.valueOf(success), String.valueOf(failure))));
        } else {
          if (payments.size() == success) {
            EventBus.getDefault().post(new SendOrderEvent(StatusCodes.SUCCESS, 0L, String
                .format(Locale.US,
                    context.getString(R.string.payments_data_transferred_successfully),
                    String.valueOf(success), String.valueOf(failure))));
          } else {
            EventBus.getDefault().post(new ErrorEvent(StatusCodes.DATA_STORE_ERROR));
          }
        }
      } catch (Exception ex) {
        Logger.sendError("Data transfer", "Error in receiving PaymentData " + ex.getMessage());
        if (Empty.isNotEmpty(observer)) {
          getObserver().publishResult(context.getString(R.string.error_payments_transfer));
        } else {
          EventBus.getDefault().post(new ErrorEvent(StatusCodes.DATA_STORE_ERROR));
        }
      }
    } else {
      if (Empty.isNotEmpty(observer)) {
        getObserver().publishResult(context.getString(R.string.message_got_no_response));
      } else {
        EventBus.getDefault().post(new ErrorEvent(StatusCodes.NETWORK_ERROR));
      }
    }
  }

  @Override
  public void beforeTransfer() {
    if (Empty.isNotEmpty(observer)) {
      getObserver().publishResult(context.getString(R.string.sending_payments_data));
    }
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
