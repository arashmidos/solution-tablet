package com.parsroyal.solutiontablet.data.searchobject;

/**
 * Created by Mahyar on 7/29/2015.
 */
public class GoodsSo extends BaseSO {

  private Long goodsGroupBackendId;
  private String supplier;
  private String assortment;

  public String getSupplier() {
    return supplier;
  }

  public void setSupplier(String supplier) {
    this.supplier = supplier;
  }

  public String getAssortment() {
    return assortment;
  }

  public void setAssortment(String assortment) {
    this.assortment = assortment;
  }

  public Long getGoodsGroupBackendId() {
    return goodsGroupBackendId;
  }

  public void setGoodsGroupBackendId(Long goodsGroupBackendId) {
    this.goodsGroupBackendId = goodsGroupBackendId;
  }
}
