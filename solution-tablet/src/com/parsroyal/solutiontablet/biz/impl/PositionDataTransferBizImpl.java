package com.parsroyal.solutiontablet.biz.impl;

import android.content.Context;
import com.parsroyal.solutiontablet.R;
import com.parsroyal.solutiontablet.constants.SaleType;
import com.parsroyal.solutiontablet.constants.SendStatus;
import com.parsroyal.solutiontablet.constants.StatusCodes;
import com.parsroyal.solutiontablet.data.entity.Position;
import com.parsroyal.solutiontablet.data.event.DataTransferSuccessEvent;
import com.parsroyal.solutiontablet.data.event.PositionDataTransferErrorEvent;
import com.parsroyal.solutiontablet.data.model.PositionDto;
import com.parsroyal.solutiontablet.service.PositionService;
import com.parsroyal.solutiontablet.service.PostDataRestService;
import com.parsroyal.solutiontablet.service.ServiceGenerator;
import com.parsroyal.solutiontablet.service.impl.PositionServiceImpl;
import com.parsroyal.solutiontablet.service.impl.SettingServiceImpl;
import com.parsroyal.solutiontablet.util.DateUtil;
import com.parsroyal.solutiontablet.util.Empty;
import com.parsroyal.solutiontablet.util.NetworkUtil;
import com.parsroyal.solutiontablet.util.NumberUtil;
import com.parsroyal.solutiontablet.util.PreferenceHelper;
import com.parsroyal.solutiontablet.util.constants.ApplicationKeys;
import java.io.IOException;
import java.util.Date;
import java.util.Locale;
import org.greenrobot.eventbus.EventBus;
import retrofit2.Call;
import retrofit2.Response;
import timber.log.Timber;

/**
 * Created by Arash on 2016-08-21
 */
public class PositionDataTransferBizImpl {

  private final SettingServiceImpl settingService;

  private Context context;
  private PositionService positionService;
  private PositionDto positionDto;
  private int success = 0;
  private int total = 0;
  private boolean noUpdate;

  public PositionDataTransferBizImpl(Context context) {
    this.context = context;
    this.positionService = new PositionServiceImpl(context);
    this.settingService = new SettingServiceImpl();
  }

  public void sendAllData() {
    if (!NetworkUtil.isNetworkAvailable(context)) {
      EventBus.getDefault().post(new PositionDataTransferErrorEvent(StatusCodes.NO_NETWORK));
      return;
    }

    PostDataRestService restService = ServiceGenerator.createService(PostDataRestService.class);

    Call<Response<Void>> call = restService.sendPosition(positionDto);

    try {
      Response<Response<Void>> response = call.execute();
      if (response.isSuccessful()) {
        updateData();
      }
      if (!noUpdate) {
        EventBus.getDefault()
            .post(new DataTransferSuccessEvent(getSuccessfulMessage(), StatusCodes.UPDATE));
      }
    } catch (IOException e) {
      Timber.d(e);
      EventBus.getDefault().post(new PositionDataTransferErrorEvent(StatusCodes.NETWORK_ERROR));
    }
  }

  public void updateData() {
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
      Timber.d(ex);
    }
  }

  public String getSuccessfulMessage() {
    return NumberUtil.digitsToPersian(String
        .format(Locale.getDefault(), context.getString(R.string.data_transfered_result),
            String.valueOf(success),
            String.valueOf(total - success)));
  }

  public String getMethod() {
    return "positions";
  }

  public void setPosition(PositionDto positionDto) {
    this.positionDto = positionDto;
    try {
      if (Empty.isEmptyOrZero(positionDto.getPersonId())) {
        String settingValue = settingService.getSettingValue(ApplicationKeys.SALESMAN_ID);
        positionDto.setPersonId(Long.valueOf(settingValue));
      }
      this.positionDto.setSaleType(
          SaleType.getByValue(Long.parseLong(PreferenceHelper.getSaleType())));
    } catch (Exception ex) {
      positionDto.setSaleType(SaleType.COLD);
    }
    total++;
  }

  public void setNoUpdate(boolean noUpdate) {
    this.noUpdate = noUpdate;
  }
}
