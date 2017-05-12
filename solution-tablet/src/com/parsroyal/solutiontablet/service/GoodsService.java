package com.parsroyal.solutiontablet.service;

import com.parsroyal.solutiontablet.data.entity.Goods;
import com.parsroyal.solutiontablet.data.listmodel.GoodsListModel;
import com.parsroyal.solutiontablet.data.searchobject.GoodsSo;
import java.util.List;

/**
 * Created by Mahyar on 7/27/2015.
 */
public interface GoodsService {

  Goods getGoodsById(Long goodsId);

  List<GoodsListModel> searchForGoods(GoodsSo goodsSo);

  Goods getGoodsByBackendId(Long goodsBackendId);
}
