package com.parsroyal.solutiontablet.biz.impl;

import android.content.Context;
import android.util.Log;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.parsroyal.solutiontablet.R;
import com.parsroyal.solutiontablet.biz.AbstractDataTransferBizImpl;
import com.parsroyal.solutiontablet.data.dao.GoodsGroupDao;
import com.parsroyal.solutiontablet.data.dao.impl.GoodsGroupDaoImpl;
import com.parsroyal.solutiontablet.data.entity.GoodsGroup;
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
public class GoodsGroupDataTransferBizImpl extends AbstractDataTransferBizImpl<String> {

  public static final String TAG = GoodsGroupDataTransferBizImpl.class.getSimpleName();

  private Context context;
  private ResultObserver resultObserver;
  private GoodsGroupDao goodsGroupDao;
  private SettingService settingService;

  public GoodsGroupDataTransferBizImpl(Context context, ResultObserver resultObserver) {
    super(context);
    this.context = context;
    this.resultObserver = resultObserver;
    this.goodsGroupDao = new GoodsGroupDaoImpl(context);
    this.settingService = new SettingServiceImpl(context);
  }

  @Override
  public void receiveData(String data) {
    try {
      List<GoodsGroup> list = new Gson().fromJson(data, new TypeToken<List<GoodsGroup>>() {
      }.getType());
      if (Empty.isNotEmpty(data) && Empty.isNotEmpty(list)) {

        for (GoodsGroup group : list) {
          group.setTitle(CharacterFixUtil.fixString(group.getTitle()));
          group.setCreateDateTime(DateUtil.getCurrentGregorianFullWithTimeDate());
          group.setUpdateDateTime(DateUtil.getCurrentGregorianFullWithTimeDate());
          goodsGroupDao.create(group);
        }

        resultObserver.publishResult(
            context.getString(R.string.message_goods_groups_transferred_successfully));
      } else {
        resultObserver
            .publishResult(context.getString(R.string.message_no_goods_group_transferred));
      }
    } catch (Exception ex) {
      Logger.sendError("Data transfer", "Error in receiving GoodsGroupData " + ex.getMessage());
      Log.e(TAG, ex.getMessage(), ex);
      resultObserver.publishResult(
          context.getString(R.string.message_exception_in_transferring_goods_groups));
    }
  }

  @Override
  public void beforeTransfer() {
  }

  @Override
  public ResultObserver getObserver() {
    return resultObserver;
  }

  @Override
  public String getMethod() {
    return "goods/groups/" + settingService.getSettingValue(ApplicationKeys.SALESMAN_ID);
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
    HttpEntity<String> entity = new HttpEntity<String>("No Param", headers);
    return entity;
  }
}
