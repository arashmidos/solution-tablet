package com.parsroyal.solutiontablet.data.event;

import com.parsroyal.solutiontablet.data.entity.Goods;
import java.util.List;

public class GoodListEvent extends Event {

  private List<Goods> goodsListModels;

  public GoodListEvent(List<Goods> goodsListModels) {
    this.goodsListModels = goodsListModels;
  }

  public List<Goods> getGoodsListModels() {
    return goodsListModels;
  }

  public void setGoodsListModels(
      List<Goods> goodsListModels) {
    this.goodsListModels = goodsListModels;
  }
}
