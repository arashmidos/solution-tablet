package com.parsroyal.solutiontablet.data.listmodel;

/**
 * Created by Mahyar on 7/29/2015.
 */
public class GoodsListModel extends BaseListModel {

  private long goodsBackendId;
  private Long goodsAmount;
  private Long goodsCustomerAmount;
  private Long existing;
  private Long unit1Count;
  private String unit1Title;
  private String unit2Title;
  private Long recoveryDate;
  private long customerPrice;
  private long defaultUnit;
  private String supplier;
  private String assortment;

  public long getDefaultUnit() {
    return defaultUnit;
  }

  public void setDefaultUnit(long defaultUnit) {
    this.defaultUnit = defaultUnit;
  }

  public long getGoodsBackendId() {
    return goodsBackendId;
  }

  public void setGoodsBackendId(long goodsBackendId) {
    this.goodsBackendId = goodsBackendId;
  }

  public Long getGoodsAmount() {
    return goodsAmount;
  }

  public void setGoodsAmount(Long goodsAmount) {
    this.goodsAmount = goodsAmount;
  }

  public Long getExisting() {
    return existing;
  }

  public void setExisting(Long existing) {
    this.existing = existing;
  }

  public Long getUnit1Count() {
    return unit1Count;
  }

  public void setUnit1Count(Long unit1Count) {
    this.unit1Count = unit1Count;
  }

  public String getUnit1Title() {
    return unit1Title;
  }

  public void setUnit1Title(String unit1Title) {
    this.unit1Title = unit1Title;
  }

  public String getUnit2Title() {
    return unit2Title;
  }

  public void setUnit2Title(String unit2Title) {
    this.unit2Title = unit2Title;
  }

  public Long getRecoveryDate() {
    return recoveryDate;
  }

  public void setRecoveryDate(Long recoveryDate) {
    this.recoveryDate = recoveryDate;
  }

  public long getCustomerPrice() {
    return customerPrice;
  }

  public void setCustomerPrice(long customerPrice) {
    this.customerPrice = customerPrice;
  }

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
}
