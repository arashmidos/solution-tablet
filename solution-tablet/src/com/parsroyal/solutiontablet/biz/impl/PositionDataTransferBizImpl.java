package com.parsroyal.solutiontablet.biz.impl;

import android.content.Context;
import android.util.Log;
import com.crashlytics.android.Crashlytics;
import com.parsroyal.solutiontablet.R;
import com.parsroyal.solutiontablet.biz.AbstractDataTransferBizImpl;
import com.parsroyal.solutiontablet.constants.SaleType;
import com.parsroyal.solutiontablet.constants.SendStatus;
import com.parsroyal.solutiontablet.constants.StatusCodes;
import com.parsroyal.solutiontablet.data.entity.Position;
import com.parsroyal.solutiontablet.data.event.DataTransferSuccessEvent;
import com.parsroyal.solutiontablet.data.model.PositionDto;
import com.parsroyal.solutiontablet.service.PositionService;
import com.parsroyal.solutiontablet.service.impl.PositionServiceImpl;
import com.parsroyal.solutiontablet.service.impl.SettingServiceImpl;
import com.parsroyal.solutiontablet.ui.observer.ResultObserver;
import com.parsroyal.solutiontablet.util.DateUtil;
import com.parsroyal.solutiontablet.util.Empty;
import com.parsroyal.solutiontablet.util.NumberUtil;
import com.parsroyal.solutiontablet.util.constants.ApplicationKeys;
import java.util.Date;
import java.util.Locale;
import org.greenrobot.eventbus.EventBus;
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
  private boolean noUpdate;

  public PositionDataTransferBizImpl(Context context) {
    super(context);
    this.context = context;
    this.positionService = new PositionServiceImpl(context);
    this.settingService = new SettingServiceImpl();
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
    }
    if (!noUpdate) {
      EventBus.getDefault()
          .post(new DataTransferSuccessEvent(getSuccessfulMessage(), StatusCodes.UPDATE));
    }
  }

  public String getSuccessfulMessage() {
    return NumberUtil.digitsToPersian(String
        .format(Locale.getDefault(), context.getString(R.string.data_transfered_result),
            String.valueOf(success),
            String.valueOf(total - success)));
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
    try {
      if (Empty.isEmpty(positionDto.getPersonId()) || positionDto.getPersonId() == 0) {
        String settingValue = settingService.getSettingValue(ApplicationKeys.SALESMAN_ID);
        positionDto.setPersonId(Long.valueOf(settingValue));
      }
      this.positionDto.setSaleType(SaleType.getByValue(
          Long.parseLong(settingService.getSettingValue(ApplicationKeys.SETTING_SALE_TYPE))));

    } catch (Exception ex) {
      positionDto.setSaleType(SaleType.COLD);
    }
    this.total++;
  }

  public void sendAllData() {
    exchangeData();
  }

  public void setNoUpdate(boolean noUpdate) {
    this.noUpdate = noUpdate;
  }
}
