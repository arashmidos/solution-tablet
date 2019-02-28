package com.parsroyal.storemanagement.service.impl;

import android.content.Context;
import com.parsroyal.storemanagement.data.dao.GoodsDao;
import com.parsroyal.storemanagement.data.dao.GoodsGroupDao;
import com.parsroyal.storemanagement.data.dao.impl.GoodsDaoImpl;
import com.parsroyal.storemanagement.data.dao.impl.GoodsGroupDaoImpl;
import com.parsroyal.storemanagement.data.entity.Goods;
import com.parsroyal.storemanagement.data.entity.GoodsGroup;
import com.parsroyal.storemanagement.data.listmodel.GoodsListModel;
import com.parsroyal.storemanagement.data.searchobject.GoodsSo;
import com.parsroyal.storemanagement.service.GoodsService;
import com.parsroyal.storemanagement.util.Empty;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by Mahyar on 7/27/2015.
 */
public class GoodsServiceImpl implements GoodsService {

  private final GoodsGroupDao goodsGroupDao;
  private Context context;
  private GoodsDao goodsDao;

  public GoodsServiceImpl(Context context) {
    this.context = context;
    this.goodsDao = new GoodsDaoImpl(context);
    this.goodsGroupDao = new GoodsGroupDaoImpl(context);
  }

  @Override
  public Goods getGoodsById(Long goodsId) {
    return goodsDao.retrieve(goodsId);
  }

  @Override
  public List<GoodsListModel> searchForGoods(GoodsSo goodsSo) {
    return goodsDao.findGoods(goodsSo);
  }

  @Override
  public List<Goods> searchForGoodsList(GoodsSo goodsSo) {
    return goodsDao.findGoodsList(goodsSo);
  }

  @Override
  public Goods getGoodsByBackendId(Long goodsBackendId) {
    return goodsDao.retrieveByBackendId(goodsBackendId);
  }

  @Override
  public void deleteAllGoodsGroup() {
    goodsGroupDao.deleteAll();
  }

  @Override
  public Map<GoodsGroup, List<GoodsGroup>> getCategories() {
    List<GoodsGroup> categories = goodsGroupDao.getCategories();
    Map<GoodsGroup, List<GoodsGroup>> returnMap = new TreeMap<>();

    for (GoodsGroup goodsGroup : categories) {
      returnMap.put(goodsGroup, getChilds(goodsGroup.getBackendId()));
    }

    return returnMap;
  }

  @Override
  public List<GoodsGroup> getChilds(Long goodsGroupBackendId) {
    return goodsGroupDao.getChilds(goodsGroupBackendId);
  }

  @Override
  public List<GoodsGroup> getUpLevel(Long goodsGroupBackendId) {
    GoodsGroup currentGoodsGroup = goodsGroupDao.retrieveByBackendId(goodsGroupBackendId);
    if (Empty.isNotEmpty(currentGoodsGroup)) {
      GoodsGroup parent = goodsGroupDao.retrieveByBackendId(currentGoodsGroup.getParentBackendId());
      if (Empty.isNotEmpty(parent)) {
        // parent.getParentBackendId()
        return goodsGroupDao.getChilds(parent.getParentBackendId());
      }
    }
    return null;
  }

  @Override
  public GoodsGroup getParent(Long goodsGroupBackendId) {
    GoodsGroup currentGoodsGroup = goodsGroupDao.retrieveByBackendId(goodsGroupBackendId);
    if (Empty.isNotEmpty(currentGoodsGroup)) {
      GoodsGroup parent = goodsGroupDao.retrieveByBackendId(currentGoodsGroup.getParentBackendId());
      if (Empty.isNotEmpty(parent)) {
        return parent;
      }
    }
    return null;
  }

  @Override
  public GoodsGroup getCurrent(Long goodsGroupBackendId) {
    GoodsGroup currentGoodsGroup = goodsGroupDao.retrieveByBackendId(goodsGroupBackendId);
    if (Empty.isNotEmpty(currentGoodsGroup)) {
        return currentGoodsGroup;
    }
    return null;
  }

  @Override
  public List<GoodsGroup> getCurrentLevel(Long goodsGroupBackendId) {
    GoodsGroup currentGoodsGroup = goodsGroupDao.retrieveByBackendId(goodsGroupBackendId);
    if (Empty.isNotEmpty(currentGoodsGroup)) {
      return goodsGroupDao.getChilds(currentGoodsGroup.getParentBackendId());
    }
    return null;
  }

  @Override
  public void deleteAll() {
    goodsDao.deleteAll();
  }
}
