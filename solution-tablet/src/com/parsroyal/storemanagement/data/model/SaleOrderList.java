package com.parsroyal.storemanagement.data.model;

import com.parsroyal.storemanagement.data.entity.SaleOrder;
import java.util.List;

/**
 * Created by Mahyar on 8/14/2015.
 */
public class SaleOrderList extends BaseModel {

  private List<SaleOrder> orderList;

  public List<SaleOrder> getOrderList() {
    return orderList;
  }

  public void setOrderList(List<SaleOrder> orderList) {
    this.orderList = orderList;
  }
}
