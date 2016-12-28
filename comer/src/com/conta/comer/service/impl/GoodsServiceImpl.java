package com.conta.comer.service.impl;

import android.content.Context;

import com.conta.comer.data.dao.GoodsDao;
import com.conta.comer.data.dao.impl.GoodsDaoImpl;
import com.conta.comer.data.entity.Goods;
import com.conta.comer.data.model.GoodsListModel;
import com.conta.comer.data.searchobject.GoodsSo;
import com.conta.comer.service.GoodsService;

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
