package com.parsroyal.solutiontablet.biz.impl;

import android.content.Context;
import android.util.Log;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.parsroyal.solutiontablet.R;
import com.parsroyal.solutiontablet.biz.AbstractDataTransferBizImpl;
import com.parsroyal.solutiontablet.biz.KeyValueBiz;
import com.parsroyal.solutiontablet.data.dao.GoodsDao;
import com.parsroyal.solutiontablet.data.dao.impl.GoodsDaoImpl;
import com.parsroyal.solutiontablet.data.entity.Goods;
import com.parsroyal.solutiontablet.data.entity.KeyValue;
import com.parsroyal.solutiontablet.service.SettingService;
import com.parsroyal.solutiontablet.service.impl.SettingServiceImpl;
import com.parsroyal.solutiontablet.ui.observer.ResultObserver;
import com.parsroyal.solutiontablet.util.CharacterFixUtil;
import com.parsroyal.solutiontablet.util.DateUtil;
import com.parsroyal.solutiontablet.util.Empty;
import com.parsroyal.solutiontablet.util.Logger;
import com.parsroyal.solutiontablet.util.constants.ApplicationKeys;
import java.util.List;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;

/**
 * Created by Mahyar on 7/24/2015.
 */
public class DeliverableGoodsDataTransferBizImpl extends AbstractDataTransferBizImpl<String> {

  public static final String TAG = DeliverableGoodsDataTransferBizImpl.class.getSimpleName();

  private Context context;
  private ResultObserver resultObserver;
  private GoodsDao goodsDao;
  private KeyValueBiz keyValueBiz;
  private SettingService settingService;

  public DeliverableGoodsDataTransferBizImpl(Context context, ResultObserver resultObserver) {
    super(context);
    this.context = context;
    this.resultObserver = resultObserver;
    this.goodsDao = new GoodsDaoImpl(context);
    this.keyValueBiz = new KeyValueBizImpl(context);
    this.settingService = new SettingServiceImpl(context);
  }

  @Override
  public void receiveData(String data) {
    List<Goods> list = new Gson().fromJson(data, new TypeToken<List<Goods>>() {
    }.getType());

    if (Empty.isNotEmpty(data) && Empty.isNotEmpty(list)) {
      try {
        for (Goods goods : list) {
          goods.setTitle(CharacterFixUtil.fixString(goods.getTitle()));
          Goods oldGoods = goodsDao.retrieveByBackendId(goods.getBackendId());
          if (Empty.isNotEmpty(oldGoods)) {
            oldGoods.setUpdateDateTime(DateUtil.getCurrentGregorianFullWithTimeDate());
            oldGoods.setExisting(oldGoods.getExisting() + goods.getExisting());
            goodsDao.update(oldGoods);
          } else {
            goods.setCreateDateTime(DateUtil.getCurrentGregorianFullWithTimeDate());
            goods.setUpdateDateTime(DateUtil.getCurrentGregorianFullWithTimeDate());
            goodsDao.create(goods);
          }
        }

        resultObserver.publishResult(
            context.getString(R.string.message_deliverable_goods_transferred_successfully));

      } catch (Exception ex) {
        Logger.sendError("Data transfer", "Error in receiving DeliverableGoods " + ex.getMessage());
        Log.e(TAG, ex.getMessage(), ex);
        resultObserver.publishResult(
            context.getString(R.string.message_exception_in_transferring_deliverable_goods));
      }
    } else {
      resultObserver.publishResult(context.getString(R.string.message_no_goods_transferred));
    }
  }

  @Override
  public void beforeTransfer() {
    resultObserver
        .publishResult(context.getString(R.string.message_transferring_deliverable_goods_data));
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
    KeyValue userCodeKey = keyValueBiz.findByKey(ApplicationKeys.SETTING_USER_CODE);
    return new HttpEntity<>(userCodeKey.getValue(), headers);
  }
}
