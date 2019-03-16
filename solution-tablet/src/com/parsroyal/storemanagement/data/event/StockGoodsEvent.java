package com.parsroyal.storemanagement.data.event;

import com.parsroyal.storemanagement.constants.StatusCodes;
import com.parsroyal.storemanagement.data.model.StockGood;
import java.util.List;

public class StockGoodsEvent extends Event {

  private List<StockGood> goods;

  public StockGoodsEvent(StatusCodes statusCode, List<StockGood> goods) {
    this.goods = goods;
    this.statusCode = statusCode;
  }

  public List<StockGood> getGoods() {
    return goods;
  }

  public void setGoods(List<StockGood> goods) {
    this.goods = goods;
  }
}
