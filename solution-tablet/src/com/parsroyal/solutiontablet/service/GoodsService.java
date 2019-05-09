package com.parsroyal.solutiontablet.service;

import com.parsroyal.solutiontablet.data.entity.Goods;
import com.parsroyal.solutiontablet.data.entity.GoodsGroup;
import com.parsroyal.solutiontablet.data.listmodel.GoodsListModel;
import com.parsroyal.solutiontablet.data.searchobject.GoodsSo;
import java.util.List;
import java.util.Map;

/**
 * Created by Mahyar on 7/27/2015.
 */
public interface GoodsService extends BaseService {

  Goods getGoodsById(Long goodsId);

  List<GoodsListModel> searchForGoods(GoodsSo goodsSo);

  List<Goods> searchForGoodsList(GoodsSo goodsSo);

  Goods getGoodsByBackendId(Long goodsBackendId);

  void deleteAllGoodsGroup();

  Map<GoodsGroup, List<GoodsGroup>> getCategories();

  List<GoodsGroup> getChilds(Long goodsGroupBackendId);

  List<GoodsGroup> getUpLevel(Long goodsGroupBackendId);

  List<GoodsGroup> getCurrentLevel(Long goodsGroupBackendId);

  GoodsGroup getParent(Long goodsGroupBackendId);

  GoodsGroup getCurrent(Long goodsGroupBackendId);
}
