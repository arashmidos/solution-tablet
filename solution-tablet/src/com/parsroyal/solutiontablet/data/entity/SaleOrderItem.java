package com.parsroyal.solutiontablet.data.entity;

import com.parsroyal.solutiontablet.util.DateUtil;

/**
 * Created with IntelliJ IDEA. User: m.sefidi Date: 4/11/13 Time: 11:02 AM To change this template
 * use File | Settings | File Templates.
 */
public class SaleOrderItem extends BaseEntity<Long> {

  public static final String TABLE_NAME = "COMMER_SALE_ORDER_ITEM";
  public static final String COL_ID = "_id";
  public static final String COL_GOODS_BACKEND_ID = "GOODS_BACKEND_ID";
  public static final String COL_GOODS_COUNT = "GOODS_COUNT";
  public static final String COL_AMOUNT = "AMOUNT";
  public static final String COL_SALE_ORDER_ID = "SALE_ORDER_ID";
  public static final String COL_SALE_ORDER_BACKEND_ID = "SALE_ORDER_BACKEND_ID";
  public static final String COL_SELECTED_UNIT = "SELECTED_UNIT";
  public static final String COL_BACKEND_ID = "BACKEND_ID";
  public static final String COL_INVOICE_BACKEND_ID = "INVOICE_BACKEND_ID";
  public static final String COL_GOODS_COUNT_2 = "GOODS_COUNT_2";
  public static final String COL_GOODS_DISCOUNT = "GOODS_DISCOUNT";
  public static final String COL_INC = "INC";
  public static final String COL_DEC = "DEC";

  public static final String CREATE_TABLE_SCRIPT =
      "CREATE TABLE " + SaleOrderItem.TABLE_NAME + " (" +
          " " + SaleOrderItem.COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
          " " + SaleOrderItem.COL_GOODS_BACKEND_ID + " INTEGER," +
          " " + SaleOrderItem.COL_GOODS_COUNT + " INTEGER," +
          " " + SaleOrderItem.COL_AMOUNT + " INTEGER," +
          " " + SaleOrderItem.COL_SALE_ORDER_ID + " INTEGER," +
          " " + SaleOrderItem.COL_SALE_ORDER_BACKEND_ID + " INTEGER," +
          " " + SaleOrderItem.COL_SELECTED_UNIT + " INTEGER," +
          " " + SaleOrderItem.COL_BACKEND_ID + " INTEGER," +
          " " + SaleOrderItem.COL_CREATE_DATE_TIME + " TEXT," +
          " " + SaleOrderItem.COL_UPDATE_DATE_TIME + " TEXT," +
          " " + SaleOrderItem.COL_INVOICE_BACKEND_ID + " INTEGER," +
          " " + SaleOrderItem.COL_GOODS_COUNT_2 + " INTEGER," +
          " " + SaleOrderItem.COL_GOODS_DISCOUNT + " INTEGER," +
          " " + SaleOrderItem.COL_INC + " INTEGER," +
          " " + SaleOrderItem.COL_DEC + " INTEGER" +
          " );";

  private Long id;
  private Long goodsBackendId;
  private Long goodsCount;
  private Long amount;
  private Long saleOrderId;
  private Long saleOrderBackendId;
  private Long selectedUnit;
  private Long goodsUnit2Count;
  private Long backendId;
  private Long invoiceItemBackendId;
  private Long invoiceBackendId;
  private Long rejectBackendId;
  private Long rejectItemBackendId;
  private Long discount;
  private Long ordDec;
  private Long ordInc;

  public SaleOrderItem() {
  }

  public SaleOrderItem(Long goodsBackendId, Long saleOrderId, Long invoiceBackendId) {
    this.goodsBackendId = goodsBackendId;
    this.goodsCount = 0L;
    this.saleOrderId = saleOrderId;
    this.invoiceBackendId = invoiceBackendId;
    this.createDateTime = DateUtil.getCurrentGregorianFullWithTimeDate();
    this.updateDateTime = DateUtil.getCurrentGregorianFullWithTimeDate();
  }

  public SaleOrderItem(Long goodsBackendId, Long saleOrderId, Long invoiceBackendId,
      Long goodsCount) {
    this(goodsBackendId, saleOrderId, invoiceBackendId);
    this.goodsCount = goodsCount;
  }

  public Long getOrdDec() {
    return ordDec;
  }

  public void setOrdDec(Long ordDec) {
    this.ordDec = ordDec;
  }

  public Long getOrdInc() {
    return ordInc;
  }

  public void setOrdInc(Long ordInc) {
    this.ordInc = ordInc;
  }

  public Long getDiscount() {
    return discount;
  }

  public void setDiscount(Long discount) {
    this.discount = discount;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Long getGoodsBackendId() {
    return goodsBackendId;
  }

  public void setGoodsBackendId(Long goodsBackendId) {
    this.goodsBackendId = goodsBackendId;
  }

  public Long getGoodsCount() {
    return goodsCount;
  }

  public void setGoodsCount(Long goodsCount) {
    this.goodsCount = goodsCount;
  }

  public Long getAmount() {
    return amount;
  }

  public void setAmount(Long amount) {
    this.amount = amount;
  }

  public Long getSaleOrderId() {
    return saleOrderId;
  }

  public void setSaleOrderId(Long saleOrderId) {
    this.saleOrderId = saleOrderId;
  }

  public Long getSaleOrderBackendId() {
    return saleOrderBackendId;
  }

  public void setSaleOrderBackendId(Long saleOrderBackendId) {
    this.saleOrderBackendId = saleOrderBackendId;
  }

  public Long getSelectedUnit() {
    return selectedUnit;
  }

  public void setSelectedUnit(Long selectedUnit) {
    this.selectedUnit = selectedUnit;
  }

  public Long getBackendId() {
    return backendId;
  }

  public void setBackendId(Long backendId) {
    this.backendId = backendId;
  }

  public Long getGoodsUnit2Count() {
    return goodsUnit2Count;
  }

  public void setGoodsUnit2Count(Long goodsUnit2Count) {
    this.goodsUnit2Count = goodsUnit2Count;
  }

  public Long getInvoiceBackendId() {
    return invoiceBackendId;
  }

  public void setInvoiceBackendId(Long invoiceBackendId) {
    this.invoiceBackendId = invoiceBackendId;
  }

  public Long getInvoiceItemBackendId() {
    return invoiceItemBackendId;
  }

  public void setInvoiceItemBackendId(Long invoiceItemBackendId) {
    this.invoiceItemBackendId = invoiceItemBackendId;
  }

  public Long getRejectBackendId() {
    return rejectBackendId;
  }

  public void setRejectBackendId(Long rejectBackendId) {
    this.rejectBackendId = rejectBackendId;
  }

  public Long getRejectItemBackendId() {
    return rejectItemBackendId;
  }

  public void setRejectItemBackendId(Long rejectItemBackendId) {
    this.rejectItemBackendId = rejectItemBackendId;
  }

  @Override
  public Long getPrimaryKey() {
    return id;
  }
}
