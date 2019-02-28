package com.parsroyal.storemanagement.data.dao;

import com.parsroyal.storemanagement.data.entity.Goods;
import com.parsroyal.storemanagement.data.listmodel.GoodsListModel;
import com.parsroyal.storemanagement.data.searchobject.GoodsSo;
import java.util.List;

/**
 * Created by Mahyar on 7/24/2015.
 */
public interface GoodsDao extends BaseDao<Goods, Long> {

  List<GoodsListModel> findGoods(GoodsSo goodsSo);

  Goods retrieveByBackendId(Long backendId);

  List<Goods> findGoodsList(GoodsSo goodsSo);
}
