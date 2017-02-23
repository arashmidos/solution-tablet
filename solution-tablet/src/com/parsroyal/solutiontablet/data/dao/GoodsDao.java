package com.parsroyal.solutiontablet.data.dao;

import com.parsroyal.solutiontablet.data.entity.Goods;
import com.parsroyal.solutiontablet.data.model.GoodsListModel;
import com.parsroyal.solutiontablet.data.searchobject.GoodsSo;

import java.util.List;

/**
 * Created by Mahyar on 7/24/2015.
 */
public interface GoodsDao extends BaseDao<Goods, Long>
{
    List<GoodsListModel> findGoods(GoodsSo goodsSo);

    Goods retrieveByBackendId(Long backendId);


}
