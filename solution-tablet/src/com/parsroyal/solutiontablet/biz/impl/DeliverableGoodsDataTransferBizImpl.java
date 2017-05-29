package com.parsroyal.solutiontablet.biz.impl;

import android.content.Context;
import android.util.Log;
import com.crashlytics.android.Crashlytics;
import com.parsroyal.solutiontablet.R;
import com.parsroyal.solutiontablet.biz.AbstractDataTransferBizImpl;
import com.parsroyal.solutiontablet.biz.KeyValueBiz;
import com.parsroyal.solutiontablet.data.dao.GoodsDao;
import com.parsroyal.solutiontablet.data.dao.impl.GoodsDaoImpl;
import com.parsroyal.solutiontablet.data.entity.Goods;
import com.parsroyal.solutiontablet.data.entity.KeyValue;
import com.parsroyal.solutiontablet.data.model.GoodsDtoList;
import com.parsroyal.solutiontablet.ui.observer.ResultObserver;
import com.parsroyal.solutiontablet.util.CharacterFixUtil;
import com.parsroyal.solutiontablet.util.DateUtil;
import com.parsroyal.solutiontablet.util.Empty;
import com.parsroyal.solutiontablet.util.constants.ApplicationKeys;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;

/**
 * Created by Mahyar on 7/24/2015.
 */
public class DeliverableGoodsDataTransferBizImpl extends AbstractDataTransferBizImpl<GoodsDtoList> {

  public static final String TAG = DeliverableGoodsDataTransferBizImpl.class.getSimpleName();

  private Context context;
  private ResultObserver resultObserver;
  private GoodsDao goodsDao;
  private KeyValueBiz keyValueBiz;

  public DeliverableGoodsDataTransferBizImpl(Context context, ResultObserver resultObserver) {
    super(context);
    this.context = context;
    this.resultObserver = resultObserver;
    this.goodsDao = new GoodsDaoImpl(context);
    this.keyValueBiz = new KeyValueBizImpl(context);
  }

  @Override
  public void receiveData(GoodsDtoList data) {
    if (Empty.isNotEmpty(data) && Empty.isNotEmpty(data.getGoodsDtoList())) {
      try {
        for (Goods goods : data.getGoodsDtoList()) {
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
        Crashlytics.log(Log.ERROR, "Data transfer", "Error in receiving DeliverableGoods " + ex.getMessage());
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
    return "goods/deliverable";
  }

  @Override
  public Class getType() {
    return GoodsDtoList.class;
  }

  @Override
  public HttpMethod getHttpMethod() {
    return HttpMethod.POST;
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
