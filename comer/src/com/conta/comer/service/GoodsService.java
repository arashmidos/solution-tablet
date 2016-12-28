package com.conta.comer.service;

import com.conta.comer.data.entity.Goods;
import com.conta.comer.data.model.GoodsListModel;
import com.conta.comer.data.searchobject.GoodsSo;

import java.util.List;

/**
 * Created by Mahyar on 7/27/2015.
 */
public interface GoodsService
{
    Goods getGoodsById(Long goodsId);

    List<GoodsListModel> searchForGoods(GoodsSo goodsSo);

    Goods getGoodsByBackendId(Long goodsBackendId);
}
