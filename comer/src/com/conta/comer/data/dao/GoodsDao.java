package com.conta.comer.data.dao;

import com.conta.comer.data.entity.Goods;
import com.conta.comer.data.model.GoodsListModel;
import com.conta.comer.data.searchobject.GoodsSo;

import java.util.List;

/**
 * Created by Mahyar on 7/24/2015.
 */
public interface GoodsDao extends BaseDao<Goods, Long>
{
    List<GoodsListModel> findGoods(GoodsSo goodsSo);

    Goods retrieveByBackendId(Long backendId);


}
