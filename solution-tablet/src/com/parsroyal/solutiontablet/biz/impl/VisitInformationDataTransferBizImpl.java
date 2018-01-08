package com.parsroyal.solutiontablet.biz.impl;

import android.content.Context;
import android.util.Log;
import com.parsroyal.solutiontablet.R;
import com.parsroyal.solutiontablet.biz.AbstractDataTransferBizImpl;
import com.parsroyal.solutiontablet.constants.StatusCodes;
import com.parsroyal.solutiontablet.data.dao.VisitInformationDao;
import com.parsroyal.solutiontablet.data.dao.impl.VisitInformationDaoImpl;
import com.parsroyal.solutiontablet.data.entity.VisitInformation;
import com.parsroyal.solutiontablet.data.event.DataTransferSuccessEvent;
import com.parsroyal.solutiontablet.data.model.VisitInformationDto;
import com.parsroyal.solutiontablet.ui.observer.ResultObserver;
import com.parsroyal.solutiontablet.util.DateUtil;
import com.parsroyal.solutiontablet.util.Empty;
import com.parsroyal.solutiontablet.util.Logger;
import java.util.Locale;
import org.greenrobot.eventbus.EventBus;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;

/**
 * Created by Arash on 29/12/2017
 */
public class VisitInformationDataTransferBizImpl extends
    AbstractDataTransferBizImpl<VisitInformationDto> {

  private int success = 0;
  private VisitInformationDao visitInformationDao;
  private ResultObserver observer;
  private VisitInformationDto data;
  private int total = 0;

  public VisitInformationDataTransferBizImpl(Context context) {
    super(context);
    this.visitInformationDao = new VisitInformationDaoImpl(context);
  }

  @Override
  public void receiveData(VisitInformationDto response) {
    if (Empty.isNotEmpty(response)) {
      try {
        VisitInformation visitInformation = visitInformationDao.retrieve(data.getId());

        if (Empty.isNotEmpty(visitInformation)) {
          visitInformation.setVisitBackendId(response.getId());
          visitInformation.setUpdateDateTime(DateUtil.getCurrentGregorianFullWithTimeDate());
          visitInformationDao.update(visitInformation);
          success++;
        }

      } catch (Exception ex) {
        Logger.sendError("Data transfer",
            "Error in receiving VisitInformationData " + ex.getMessage());
        Log.e(TAG, ex.getMessage(), ex);

      }
    }
    EventBus.getDefault().post(new DataTransferSuccessEvent(String
        .format(Locale.US, context.getString(R.string.data_transfered_result),
            String.valueOf(getSuccess()),
            String.valueOf((total - success))), StatusCodes.UPDATE));
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
    return "visits";
  }

  @Override
  public Class getType() {
    return VisitInformationDto.class;
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
    return new HttpEntity<>(data, headers);
  }

  public void setData(VisitInformationDto data) {
    this.data = data;
    total++;
  }

  public int getSuccess() {
    return success;
  }
}
