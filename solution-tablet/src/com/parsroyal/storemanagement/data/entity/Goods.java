package com.parsroyal.storemanagement.data.entity;

/**
 * Created by Mahyar on 7/23/2015.
 */
public class Goods extends BaseEntity<Long> {

  public static final String TABLE_NAME = "COMMER_GOODS";

  public static final String COL_ID = "_id";
  public static final String COL_BACKEND_ID = "BACKEND_ID";
  public static final String COL_TITLE = "TITLE";
  public static final String COL_CODE = "CODE";
  public static final String COL_PRICE = "PRICE";
  public static final String COL_CUSTOMER_PRICE = "CUSTOMER_PRICE";
  public static final String COL_EXISTING = "EXISTING";
  public static final String COL_UNIT1_TITLE = "UNIT1_TITLE";
  public static final String COL_UNIT2_TITLE = "UNIT2_TITLE";
  public static final String COL_REFERENCE_CODE = "REFERENCE_CODE";
  public static final String COL_UNIT1_COUNT = "UNIT1_COUNT";
  public static final String COL_GROUP_BACKEND_ID = "GROUP_BACKEND_ID";
  public static final String COL_BAR_CODE = "BAR_CODE";
  public static final String COL_DEFAULT_UNIT = "DEFAULT_UNIT";
  public static final String COL_RECOVERY_DATE = "RECOVERY_DATE";
  public static final String COL_SALE_RATE = "SALE_RATE";
  public static final String COL_SUPPLIER = "SUPPLIER";
  public static final String COL_ASSORTMENT = "ASSORTMENT";

  public static final String CREATE_TABLE_SQL = "CREATE TABLE " + Goods.TABLE_NAME + " ( "
      + Goods.COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
      + Goods.COL_BACKEND_ID + " INTEGER, "
      + Goods.COL_TITLE + " TEXT, "
      + Goods.COL_CODE + " TEXT, "
      + Goods.COL_PRICE + " INTEGER, "
      + Goods.COL_CUSTOMER_PRICE + " INTEGER, "
      + Goods.COL_EXISTING + " INTEGER, "
      + Goods.COL_UNIT1_TITLE + " TEXT, "
      + Goods.COL_UNIT2_TITLE + " TEXT, "
      + Goods.COL_REFERENCE_CODE + " TEXT, "
      + Goods.COL_UNIT1_COUNT + " INTEGER, "
      + Goods.COL_GROUP_BACKEND_ID + " INTEGER, "
      + Goods.COL_BAR_CODE + " TEXT, "
      + Goods.COL_DEFAULT_UNIT + " INTEGER, "
      + Goods.COL_RECOVERY_DATE + " INTEGER, "
      + Goods.COL_SALE_RATE + " INTEGER, "
      + Goods.COL_CREATE_DATE_TIME + " TEXT, "
      + Goods.COL_UPDATE_DATE_TIME + " TEXT,"
      + Goods.COL_SUPPLIER + " TEXT,"
      + Goods.COL_ASSORTMENT + " TEXT"
      + " );";

  private Long id;
  private Long backendId;
  private String title;
  private String code;
  private Long price;
  private Long customerPrice;
  private Long existing;
  private String unit1Title;
  private String unit2Title;
  private String referenceCode;
  private Long unit1Count;
  private Long groupBackendId;
  private String barCode;
  private Integer defaultUnit;
  private String recoveryDate;
  private Long saleRate;
  private Long invoiceBackendId;
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

  public Long getSaleRate() {
    return saleRate;
  }

  public void setSaleRate(Long saleRate) {
    this.saleRate = saleRate;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Long getBackendId() {
    return backendId;
  }

  public void setBackendId(Long backendId) {
    this.backendId = backendId;
  }

  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public Long getPrice() {
    return price;
  }

  public void setPrice(Long price) {
    this.price = price;
  }

  public String getReferenceCode() {
    return referenceCode;
  }

  public void setReferenceCode(String referenceCode) {
    this.referenceCode = referenceCode;
  }

  public Long getUnit1Count() {
    return unit1Count;
  }

  public void setUnit1Count(Long unit1Count) {
    this.unit1Count = unit1Count;
  }

  public Long getGroupBackendId() {
    return groupBackendId;
  }

  public void setGroupBackendId(Long groupBackendId) {
    this.groupBackendId = groupBackendId;
  }

  public String getBarCode() {
    return barCode;
  }

  public void setBarCode(String barCode) {
    this.barCode = barCode;
  }

  public Integer getDefaultUnit() {
    return defaultUnit;
  }

  public void setDefaultUnit(Integer defaultUnit) {
    this.defaultUnit = defaultUnit;
  }

  public String getRecoveryDate() {
    return recoveryDate;
  }

  public void setRecoveryDate(String recoveryDate) {
    this.recoveryDate = recoveryDate;
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

  public Long getExisting() {
    return existing;
  }

  public void setExisting(Long existing) {
    this.existing = existing;
  }

  @Override
  public Long getPrimaryKey() {
    return id;
  }

  public Long getCustomerPrice() {
    return customerPrice;
  }

  public void setCustomerPrice(Long customerPrice) {
    this.customerPrice = customerPrice;
  }

  public Long getInvoiceBackendId() {
    return invoiceBackendId;
  }

  public void setInvoiceBackendId(Long invoiceBackendId) {
    this.invoiceBackendId = invoiceBackendId;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    Goods goods = (Goods) o;

    if (backendId != null ? !backendId.equals(goods.backendId) : goods.backendId != null) {
      return false;
    }
    return invoiceBackendId != null ? invoiceBackendId.equals(goods.invoiceBackendId)
        : goods.invoiceBackendId == null;

  }

  @Override
  public int hashCode() {
    int result = backendId != null ? backendId.hashCode() : 0;
    result = 31 * result + (invoiceBackendId != null ? invoiceBackendId.hashCode() : 0);
    return result;
  }
}
