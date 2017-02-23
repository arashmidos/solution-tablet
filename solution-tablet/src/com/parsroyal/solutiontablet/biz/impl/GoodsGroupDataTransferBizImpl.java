package com.parsroyal.solutiontablet.biz.impl;

import android.content.Context;
import android.util.Log;

import com.parsroyal.solutiontablet.R;
import com.parsroyal.solutiontablet.biz.AbstractDataTransferBizImpl;
import com.parsroyal.solutiontablet.data.dao.GoodsGroupDao;
import com.parsroyal.solutiontablet.data.dao.impl.GoodsGroupDaoImpl;
import com.parsroyal.solutiontablet.data.entity.GoodsGroup;
import com.parsroyal.solutiontablet.data.model.GoodsGroupDtoList;
import com.parsroyal.solutiontablet.ui.observer.ResultObserver;
import com.parsroyal.solutiontablet.util.CharacterFixUtil;
import com.parsroyal.solutiontablet.util.DateUtil;
import com.parsroyal.solutiontablet.util.Empty;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;

/**
 * Created by Mahyar on 7/24/2015.
 */
public class GoodsGroupDataTransferBizImpl extends AbstractDataTransferBizImpl<GoodsGroupDtoList>
{

    public static final String TAG = GoodsGroupDataTransferBizImpl.class.getSimpleName();

    private Context context;
    private ResultObserver resultObserver;
    private GoodsGroupDao goodsGroupDao;

    public GoodsGroupDataTransferBizImpl(Context context, ResultObserver resultObserver)
    {
        super(context);
        this.context = context;
        this.resultObserver = resultObserver;
        this.goodsGroupDao = new GoodsGroupDaoImpl(context);
    }

    @Override
    public void receiveData(GoodsGroupDtoList data)
    {

        if (Empty.isNotEmpty(data) && Empty.isNotEmpty(data.getGoodsGroups()))
        {
            try
            {
                for (GoodsGroup group : data.getGoodsGroups())
                {
                    group.setTitle(CharacterFixUtil.fixString(group.getTitle()));
                    group.setCreateDateTime(DateUtil.getCurrentGregorianFullWithTimeDate());
                    group.setUpdateDateTime(DateUtil.getCurrentGregorianFullWithTimeDate());
                    goodsGroupDao.create(group);
                }

                resultObserver.publishResult(context.getString(R.string.message_goods_groups_transferred_successfully));
            } catch (Exception ex)
            {
                Log.e(TAG, ex.getMessage(), ex);
                resultObserver.publishResult(context.getString(R.string.message_exception_in_transferring_goods_groups));
            }
        } else
        {
            resultObserver.publishResult(context.getString(R.string.message_no_goods_group_transferred));
        }
    }

    @Override
    public void beforeTransfer()
    {
        resultObserver.publishResult(context.getString(R.string.message_transferring_goods_group_data));
    }

    @Override
    public ResultObserver getObserver()
    {
        return resultObserver;
    }

    @Override
    public String getMethod()
    {
        return "goods/groups";
    }

    @Override
    public Class getType()
    {
        return GoodsGroupDtoList.class;
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
        HttpEntity<String> entity = new HttpEntity<String>("No Param", headers);
        return entity;
    }
}
