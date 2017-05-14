package com.parsroyal.solutiontablet.biz.impl;

import android.content.Context;
import android.util.Log;

import com.parsroyal.solutiontablet.R;
import com.parsroyal.solutiontablet.biz.AbstractDataTransferBizImpl;
import com.parsroyal.solutiontablet.biz.KeyValueBiz;
import com.parsroyal.solutiontablet.data.dao.GoodsDao;
import com.parsroyal.solutiontablet.data.dao.impl.GoodsDaoImpl;
import com.parsroyal.solutiontablet.data.entity.Goods;
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
public class GoodsDataTransferBizImpl extends AbstractDataTransferBizImpl<GoodsDtoList>
{

    public static final String TAG = GoodsDataTransferBizImpl.class.getSimpleName();
    private final KeyValueBiz keyValueBiz;

    private Context context;
    private ResultObserver resultObserver;
    private GoodsDao goodsDao;

    public GoodsDataTransferBizImpl(Context context, ResultObserver resultObserver)
    {
        super(context);
        this.context = context;
        this.resultObserver = resultObserver;
        this.goodsDao = new GoodsDaoImpl(context);
        this.keyValueBiz = new KeyValueBizImpl(context);
    }

    @Override
    public void receiveData(GoodsDtoList data)
    {
        if (Empty.isNotEmpty(data) && Empty.isNotEmpty(data.getGoodsDtoList()))
        {
            try
            {
                goodsDao.deleteAll();

                for (Goods goods : data.getGoodsDtoList())
                {
                    goods.setTitle(CharacterFixUtil.fixString(goods.getTitle()));
                    goods.setCreateDateTime(DateUtil.getCurrentGregorianFullWithTimeDate());
                    goods.setUpdateDateTime(DateUtil.getCurrentGregorianFullWithTimeDate());
                    goodsDao.create(goods);
                }
                resultObserver
                        .publishResult(context.getString(R.string.message_goods_transferred_successfully));
            } catch (Exception ex)
            {
                Log.e(TAG, ex.getMessage(), ex);
                resultObserver
                        .publishResult(context.getString(R.string.message_exception_in_transferring_goods));
            }
        } else
        {
            resultObserver.publishResult(context.getString(R.string.message_no_goods_transferred));
        }
    }

    @Override
    public void beforeTransfer()
    {
        resultObserver.publishResult(context.getString(R.string.message_transferring_goods_data));
    }

    @Override
    public ResultObserver getObserver()
    {
        return resultObserver;
    }

    @Override
    public String getMethod()
    {
        return "goods/all";
    }

    @Override
    public Class getType()
    {
        return GoodsDtoList.class;
    }

    @Override
    public HttpMethod getHttpMethod()
    {
        return HttpMethod.POST;
    }

    @Override
    protected MediaType getContentType()
    {
        return MediaType.TEXT_PLAIN;
    }

    @Override
    protected HttpEntity getHttpEntity(HttpHeaders headers)
    {
        headers
                .add("branchCode", keyValueBiz.findByKey(ApplicationKeys.SETTING_BRANCH_CODE).getValue());
        headers.add("stockCode", keyValueBiz.findByKey(ApplicationKeys.SETTING_STOCK_CODE).getValue());

        HttpEntity<String> entity = new HttpEntity<>("No Params", headers);
        return entity;
    }
}
