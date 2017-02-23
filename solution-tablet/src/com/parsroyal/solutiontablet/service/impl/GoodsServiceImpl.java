package com.parsroyal.solutiontablet.service.impl;

import android.content.Context;

import com.parsroyal.solutiontablet.data.dao.GoodsDao;
import com.parsroyal.solutiontablet.data.dao.impl.GoodsDaoImpl;
import com.parsroyal.solutiontablet.data.entity.Goods;
import com.parsroyal.solutiontablet.data.model.GoodsListModel;
import com.parsroyal.solutiontablet.data.searchobject.GoodsSo;
import com.parsroyal.solutiontablet.service.GoodsService;

import java.util.List;

/**
 * Created by Mahyar on 7/27/2015.
 */
public class GoodsServiceImpl implements GoodsService
{

    private Context context;
    private GoodsDao goodsDao;

    public GoodsServiceImpl(Context context)
    {
        this.context = context;
        this.goodsDao = new GoodsDaoImpl(context);
    }

    @Override
    public Goods getGoodsById(Long goodsId)
    {
        return goodsDao.retrieve(goodsId);
    }

    @Override
    public List<GoodsListModel> searchForGoods(GoodsSo goodsSo)
    {
        return goodsDao.findGoods(goodsSo);
    }

    @Override
    public Goods getGoodsByBackendId(Long goodsBackendId)
    {
        return goodsDao.retrieveByBackendId(goodsBackendId);
    }
}
