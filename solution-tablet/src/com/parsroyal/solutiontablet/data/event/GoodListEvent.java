package com.parsroyal.solutiontablet.data.event;

import com.parsroyal.solutiontablet.data.listmodel.GoodsListModel;
import java.util.List;

public class GoodListEvent extends Event {

  private List<GoodsListModel> goodsListModels;

  public GoodListEvent(List<GoodsListModel> goodsListModels) {
    this.goodsListModels = goodsListModels;
  }

  public List<GoodsListModel> getGoodsListModels() {
    return goodsListModels;
  }

  public void setGoodsListModels(
      List<GoodsListModel> goodsListModels) {
    this.goodsListModels = goodsListModels;
  }
}
