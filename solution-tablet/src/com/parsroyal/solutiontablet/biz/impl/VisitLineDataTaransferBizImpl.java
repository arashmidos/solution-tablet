package com.parsroyal.solutiontablet.biz.impl;

import android.content.Context;
import android.util.Log;
import com.crashlytics.android.Crashlytics;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.parsroyal.solutiontablet.R;
import com.parsroyal.solutiontablet.biz.AbstractDataTransferBizImpl;
import com.parsroyal.solutiontablet.data.dao.CustomerDao;
import com.parsroyal.solutiontablet.data.dao.VisitLineDao;
import com.parsroyal.solutiontablet.data.dao.impl.CustomerDaoImpl;
import com.parsroyal.solutiontablet.data.dao.impl.VisitLineDaoImpl;
import com.parsroyal.solutiontablet.data.entity.Goods;
import com.parsroyal.solutiontablet.data.entity.VisitLine;
import com.parsroyal.solutiontablet.data.model.VisitLineDto;
import com.parsroyal.solutiontablet.service.SettingService;
import com.parsroyal.solutiontablet.service.impl.SettingServiceImpl;
import com.parsroyal.solutiontablet.ui.observer.ResultObserver;
import com.parsroyal.solutiontablet.util.CharacterFixUtil;
import com.parsroyal.solutiontablet.util.Empty;
import com.parsroyal.solutiontablet.util.constants.ApplicationKeys;
import java.util.List;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;

/**
 * Created by Mahyar on 7/5/2015.
 */
public class VisitLineDataTaransferBizImpl extends AbstractDataTransferBizImpl<String> {

  private Context context;
  private ResultObserver resultObserver;
  private VisitLineDao visitLineDao;
  private CustomerDao customerDao;
  private SettingService settingService;

  public VisitLineDataTaransferBizImpl(Context context, ResultObserver resultObserver) {
    super(context);
    this.context = context;
    this.resultObserver = resultObserver;
    this.visitLineDao = new VisitLineDaoImpl(context);
    this.customerDao = new CustomerDaoImpl(context);
    this.settingService = new SettingServiceImpl(context);
  }

  @Override
  public void receiveData(String data) {
    try {
      List<VisitLineDto> list = new Gson().fromJson(data, new TypeToken<List<VisitLineDto>>() {
      }.getType());
      if (Empty.isNotEmpty(data) && Empty.isNotEmpty(list)) {
        visitLineDao.deleteAll();
        customerDao.deleteAllCustomersRelatedToVisitLines();

        for (VisitLineDto visitLineDto : list) {
          visitLineDto.setTitle(CharacterFixUtil.fixString(visitLineDto.getTitle()));
          VisitLine visitLine = createVisitLineEntity(visitLineDto);
          visitLineDao.create(visitLine);
          customerDao.bulkInsert(visitLineDto.getCustomerList());
        }
        getObserver()
            .publishResult(context.getString(R.string.visitlines_data_transferred_successfully));
      } else {
        getObserver()
            .publishResult(context.getString(R.string.message_no_visit_lines_data));
      }
    } catch (Exception ex) {
      Crashlytics
          .log(Log.ERROR, "Data transfer",
              "Error in receiving customer visitlines  " + ex.getMessage());
      Log.e(TAG, ex.getMessage(), ex);
      resultObserver
          .publishResult(context.getString(R.string.message_exception_in_transferring_visit_lines_data));
    }
  }

  private VisitLine createVisitLineEntity(VisitLineDto visitLineDto) {
    VisitLine visitLine = new VisitLine();
    visitLine.setBackendId(visitLineDto.getBackendId());
    visitLine.setCode(visitLineDto.getCode());
    visitLine.setTitle(visitLineDto.getTitle());
    return visitLine;
  }

  @Override
  public void beforeTransfer() {
    getObserver().publishResult(context.getString(R.string.message_transferring_visit_lines_data));
  }

  @Override
  public ResultObserver getObserver() {
    return resultObserver;
  }

  @Override
  public String getMethod() {
    //GET /visit-lines/{saleType}/{salesmanId}
    String url = String.format("visit-lines/%s/%s",
        settingService.getSettingValue(ApplicationKeys.SETTING_SALE_TYPE),
        settingService.getSettingValue(ApplicationKeys.SALESMAN_ID));
    Log.d(TAG, "Calling service:" + url);
    return url;
  }

  @Override
  public Class getType() {
    return String.class;
  }

  @Override
  public HttpMethod getHttpMethod() {
    return HttpMethod.GET;
  }

  @Override
  protected MediaType getContentType() {
    return MediaType.TEXT_PLAIN;
  }

  @Override
  protected HttpEntity getHttpEntity(HttpHeaders headers) {
    return (HttpEntity) new HttpEntity<String>(headers);
  }
}
