package com.parsroyal.solutiontablet.biz.impl;

import android.content.Context;
import android.util.Log;
import com.crashlytics.android.Crashlytics;
import com.parsroyal.solutiontablet.R;
import com.parsroyal.solutiontablet.biz.AbstractDataTransferBizImpl;
import com.parsroyal.solutiontablet.data.dao.VisitInformationDao;
import com.parsroyal.solutiontablet.data.dao.impl.VisitInformationDaoImpl;
import com.parsroyal.solutiontablet.data.entity.VisitInformation;
import com.parsroyal.solutiontablet.data.model.VisitInformationDto;
import com.parsroyal.solutiontablet.service.VisitService;
import com.parsroyal.solutiontablet.service.impl.VisitServiceImpl;
import com.parsroyal.solutiontablet.ui.observer.ResultObserver;
import com.parsroyal.solutiontablet.util.DateUtil;
import com.parsroyal.solutiontablet.util.Empty;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;

/**
 * Created by Mahyar on 8/2/2015.
 */
public class VisitInformationDataTransferBizImpl extends
    AbstractDataTransferBizImpl<VisitInformationDto> {

  private int success = 0;
  private VisitInformationDao visitInformationDao;
  private VisitService visitService;
  private ResultObserver observer;
  private VisitInformationDto data;

  public VisitInformationDataTransferBizImpl(Context context, ResultObserver resultObserver) {
    super(context);
    this.visitInformationDao = new VisitInformationDaoImpl(context);
    this.visitService = new VisitServiceImpl(context);
    this.observer = resultObserver;
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
        Crashlytics.log(Log.ERROR, "Data transfer", "Error in receiving VisitInformationData " + ex.getMessage());
        Log.e(TAG, ex.getMessage(), ex);
        observer.publishResult(
            context.getString(R.string.message_exception_in_sending_visit_information));
      }
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
    return "visit";
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
    HttpEntity<VisitInformationDto> httpEntity = new HttpEntity<>(data, headers);
    return httpEntity;
  }

  public void setData(VisitInformationDto data) {
    this.data = data;
  }

  public int getSuccess() {
    return success;
  }
}
