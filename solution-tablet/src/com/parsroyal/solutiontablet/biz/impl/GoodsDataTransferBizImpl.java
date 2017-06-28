package com.parsroyal.solutiontablet.biz.impl;

import android.content.Context;
import android.util.Log;
import com.crashlytics.android.Crashlytics;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.parsroyal.solutiontablet.R;
import com.parsroyal.solutiontablet.biz.AbstractDataTransferBizImpl;
import com.parsroyal.solutiontablet.biz.KeyValueBiz;
import com.parsroyal.solutiontablet.data.dao.GoodsDao;
import com.parsroyal.solutiontablet.data.dao.impl.GoodsDaoImpl;
import com.parsroyal.solutiontablet.data.entity.Goods;
import com.parsroyal.solutiontablet.service.SettingService;
import com.parsroyal.solutiontablet.service.impl.SettingServiceImpl;
import com.parsroyal.solutiontablet.ui.observer.ResultObserver;
import com.parsroyal.solutiontablet.util.CharacterFixUtil;
import com.parsroyal.solutiontablet.util.DateUtil;
import com.parsroyal.solutiontablet.util.Empty;
import com.parsroyal.solutiontablet.util.constants.ApplicationKeys;
import java.util.List;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;

/**
 * Created by Mahyar on 7/24/2015.
 */
public class GoodsDataTransferBizImpl extends AbstractDataTransferBizImpl<String> {

  public static final String TAG = GoodsDataTransferBizImpl.class.getSimpleName();
  private final KeyValueBiz keyValueBiz;

  private Context context;
  private ResultObserver resultObserver;
  private GoodsDao goodsDao;
  private SettingService settingService;

  public GoodsDataTransferBizImpl(Context context, ResultObserver resultObserver) {
    super(context);
    this.context = context;
    this.resultObserver = resultObserver;
    this.goodsDao = new GoodsDaoImpl(context);
    this.keyValueBiz = new KeyValueBizImpl(context);
    this.settingService = new SettingServiceImpl(context);
  }

  @Override
  public void receiveData(String data) {
    try {
      List<Goods> list = new Gson().fromJson(data, new TypeToken<List<Goods>>() {
      }.getType());
      if (Empty.isNotEmpty(data) && Empty.isNotEmpty(list)) {
        goodsDao.deleteAll();
        goodsDao.bulkInsert(list);
        resultObserver
            .publishResult(context.getString(R.string.message_goods_transferred_successfully));
      } else {
        resultObserver.publishResult(context.getString(R.string.message_no_goods_transferred));
      }
    } catch (Exception ex) {
      Crashlytics
          .log(Log.ERROR, "Data transfer", "Error in receiving GoodsData  " + ex.getMessage());
      Log.e(TAG, ex.getMessage(), ex);
      resultObserver
          .publishResult(context.getString(R.string.message_exception_in_transferring_goods));
    }
  }

  @Override
  public void beforeTransfer() {
    resultObserver.publishResult(context.getString(R.string.message_transferring_goods_data));
  }

  @Override
  public ResultObserver getObserver() {
    return resultObserver;
  }

  @Override
  public String getMethod() {
    String url = String.format("goods/%s/%s/%s/%s",
        settingService.getSettingValue(ApplicationKeys.USER_COMPANY_ID),
        settingService.getSettingValue(ApplicationKeys.SETTING_STOCK_CODE),
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
    headers
        .add("branchCode", keyValueBiz.findByKey(ApplicationKeys.SETTING_BRANCH_CODE).getValue());
    headers.add("stockCode", keyValueBiz.findByKey(ApplicationKeys.SETTING_STOCK_CODE).getValue());

    HttpEntity<String> entity = new HttpEntity<>("No Params", headers);
    return entity;
  }
}
