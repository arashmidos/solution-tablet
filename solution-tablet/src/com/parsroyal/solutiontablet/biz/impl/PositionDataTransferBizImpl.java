package com.parsroyal.solutiontablet.biz.impl;

import android.content.Context;
import android.util.Log;
import com.crashlytics.android.Crashlytics;
import com.parsroyal.solutiontablet.R;
import com.parsroyal.solutiontablet.biz.AbstractDataTransferBizImpl;
import com.parsroyal.solutiontablet.constants.SaleType;
import com.parsroyal.solutiontablet.constants.SendStatus;
import com.parsroyal.solutiontablet.data.entity.Position;
import com.parsroyal.solutiontablet.data.model.PositionDto;
import com.parsroyal.solutiontablet.service.PositionService;
import com.parsroyal.solutiontablet.service.impl.PositionServiceImpl;
import com.parsroyal.solutiontablet.service.impl.SettingServiceImpl;
import com.parsroyal.solutiontablet.ui.observer.ResultObserver;
import com.parsroyal.solutiontablet.util.DateUtil;
import com.parsroyal.solutiontablet.util.Empty;
import com.parsroyal.solutiontablet.util.constants.ApplicationKeys;
import java.util.Date;
import java.util.Locale;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;

/**
 * Created by Arash on 2016-08-21
 */
public class PositionDataTransferBizImpl extends AbstractDataTransferBizImpl<String> {

  public static final String TAG = PaymentsDataTransferBizImpl.class.getSimpleName();
  private final SettingServiceImpl settingService;

  private Context context;
  private PositionService positionService;
  private ResultObserver observer;
  private PositionDto positionDto;
  private int success = 0;
  private int total = 0;

  public PositionDataTransferBizImpl(Context context, ResultObserver resultObserver) {
    super(context);
    this.context = context;
    this.positionService = new PositionServiceImpl(context);
    this.settingService = new SettingServiceImpl(context);
    this.observer = resultObserver;
  }

  @Override
  public void receiveData(String response) {
    try {
      Position position = positionService.getPositionById(positionDto.getId());
      if (Empty.isNotEmpty(position)) {
        position.setStatus(SendStatus.SENT.getId().intValue());
        position.setUpdateDateTime(DateUtil
            .convertDate(new Date(), DateUtil.FULL_FORMATTER_GREGORIAN_WITH_TIME, "EN"));
        positionService.updatePosition(position);
        success++;
      }
    } catch (Exception ex) {
      Crashlytics
          .log(Log.ERROR, "Data transfer", "Error in receiving PositionData " + ex.getMessage());
      if (Empty.isNotEmpty(getObserver())) {
        getObserver().publishResult(context.getString(R.string.error_position_transfer));
      }
    }
  }

  protected String getExceptionMessage() {
    return context.getString(R.string.message_exception_in_sending_invoices);
  }

  public String getSuccessfulMessage() {
    return String
        .format(Locale.US, context.getString(R.string.data_transfered_result),
            String.valueOf(success),
            String.valueOf(total - success));
  }

  @Override
  public void beforeTransfer() {
    if (Empty.isNotEmpty(getObserver())) {
      getObserver().publishResult(context.getString(R.string.sending_positions_data));
    }
  }

  @Override
  public ResultObserver getObserver() {
    return observer;
  }

  @Override
  public String getMethod() {
    return "positions";
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
    return new HttpEntity<>(positionDto, headers);
  }

  public void setPosition(PositionDto positionDto) {
    this.positionDto = positionDto;
    this.positionDto.setSaleType(SaleType.getByValue(
        Long.parseLong(settingService.getSettingValue(ApplicationKeys.SETTING_SALE_TYPE))));
    this.total++;
  }

  public void sendAllData() {
    exchangeData();
  }
}
