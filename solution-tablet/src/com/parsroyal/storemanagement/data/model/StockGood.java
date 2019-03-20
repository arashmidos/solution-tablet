package com.parsroyal.storemanagement.data.model;


import com.parsroyal.storemanagement.data.entity.BaseEntity;

public class StockGood extends BaseEntity<Long> {

  public static final String TABLE_NAME = "COMMER_STOCK_GOODS";

  public static final String COL_GLS = "gls";
  public static final String COL_GSN_GLS = "gsnGLS";
  public static final String COL_GOOD_CODE_GLS = "goodCdeGLS";
  public static final String COL_GOOD_NAME_GLS = "goodNamGLS";
  public static final String COL_IS_ACTIVE_GLS = "isActiveGLS";
  public static final String COL_IS_ACTIVE_GLS_NAME = "isActiveGLSName";
  public static final String COL_ASN = "asn";
  public static final String COL_CODE_STK = "codeSTK";
  public static final String COL_NAME_STK = "nameSTK";
  public static final String COL_USN_SERIAL_GLS = "usnSerialGLS";
  public static final String COL_UNAME = "uNAME";
  public static final String COL_USN_SERIAL2_GLS = "usnSerial2GLS";
  public static final String COL_UNAME1 = "uNAME1";
  public static final String COL_USN_SERIAL2_2GLS = "usnSerial2_2GLS";
  public static final String COL_UNAME2 = "uNAME2";
  public static final String COL_URATE_GLS = "uRateGLS";
  public static final String COL_BRATE_GLS = "bRateGLS";
  public static final String COL_URATE_2GLS = "uRate_2GLS";
  public static final String COL_BRATE_2GLS = "bRate_2GLS";
  public static final String COL_COUNTED = "counted";
  public static final String COL_STATUS = "status";

  public static final String CREATE_TABLE_SQL = "CREATE TABLE " + StockGood.TABLE_NAME + " ( "
      + StockGood.COL_GLS + " INTEGER PRIMARY KEY AUTOINCREMENT, "
      + StockGood.COL_GSN_GLS + " INTEGER, "
      + StockGood.COL_GOOD_CODE_GLS + " INTEGER, "
      + StockGood.COL_GOOD_NAME_GLS + " TEXT, "
      + StockGood.COL_IS_ACTIVE_GLS + " INTEGER, "
      + StockGood.COL_IS_ACTIVE_GLS_NAME + " TEXT, "
      + StockGood.COL_ASN + " INTEGER, "
      + StockGood.COL_CODE_STK + " INTEGER, "
      + StockGood.COL_NAME_STK + " TEXT, "
      + StockGood.COL_USN_SERIAL_GLS + " INTEGER, "
      + StockGood.COL_UNAME + " TEXT, "
      + StockGood.COL_USN_SERIAL2_GLS + " INTEGER, "
      + StockGood.COL_UNAME1 + " TEXT, "
      + StockGood.COL_USN_SERIAL2_2GLS + " INTEGER, "
      + StockGood.COL_UNAME2 + " TEXT, "
      + StockGood.COL_URATE_GLS + " INTEGER, "
      + StockGood.COL_BRATE_GLS + " INTEGER, "
      + StockGood.COL_URATE_2GLS + " INTEGER, "
      + StockGood.COL_BRATE_2GLS + " INTEGER, "
      + StockGood.COL_COUNTED + " INTEGER, "
      + StockGood.COL_STATUS + " INTEGER "

      + " );";
  private Long gls;
  private Long gsnGLS;
  private Long goodCdeGLS;
  private String goodNamGLS;
  private boolean isActiveGLS;
  private String isActiveGLSName;
  private Long asn;
  private Long codeSTK;
  private String nameSTK;
  private Long usnSerialGLS;
  private String uName;
  private Long usnSerial2GLS;
  private String uName1;
  private Long usnSerial2_2GLS;
  private String uName2;
  private Long uRateGLS;
  private Long bRateGLS;
  private Long uRate_2GLS;
  private Long bRate_2GLS;
  private Long counted;
  private Long status;

  public Long getStatus() {
    return status;
  }

  public void setStatus(Long status) {
    this.status = status;
  }

  public Long getGls() {
    return gls;
  }

  public void setGls(Long gls) {
    this.gls = gls;
  }

  public Long getGsnGLS() {
    return gsnGLS;
  }

  public void setGsnGLS(Long gsnGLS) {
    this.gsnGLS = gsnGLS;
  }

  public Long getGoodCdeGLS() {
    return goodCdeGLS;
  }

  public void setGoodCdeGLS(Long goodCdeGLS) {
    this.goodCdeGLS = goodCdeGLS;
  }

  public String getGoodCdeGLSString() {
    return goodCdeGLS == null ? "" : String.valueOf(goodCdeGLS);
  }

  public String getGoodNamGLS() {
    return goodNamGLS;
  }

  public void setGoodNamGLS(String goodNamGLS) {
    this.goodNamGLS = goodNamGLS;
  }

  public boolean isActiveGLS() {
    return isActiveGLS;
  }

  public void setActiveGLS(boolean activeGLS) {
    isActiveGLS = activeGLS;
  }

  public String getIsActiveGLSName() {
    return isActiveGLSName;
  }

  public void setIsActiveGLSName(String isActiveGLSName) {
    this.isActiveGLSName = isActiveGLSName;
  }

  public Long getAsn() {
    return asn;
  }

  public void setAsn(Long asn) {
    this.asn = asn;
  }

  public Long getCodeSTK() {
    return codeSTK;
  }

  public void setCodeSTK(Long codeSTK) {
    this.codeSTK = codeSTK;
  }

  public String getNameSTK() {
    return nameSTK;
  }

  public void setNameSTK(String nameSTK) {
    this.nameSTK = nameSTK;
  }

  public Long getUsnSerialGLS() {
    return usnSerialGLS;
  }

  public void setUsnSerialGLS(Long usnSerialGLS) {
    this.usnSerialGLS = usnSerialGLS;
  }

  public String getuName() {
    return uName;
  }

  public void setuName(String uName) {
    this.uName = uName;
  }

  public Long getUsnSerial2GLS() {
    return usnSerial2GLS;
  }

  public void setUsnSerial2GLS(Long usnSerial2GLS) {
    this.usnSerial2GLS = usnSerial2GLS;
  }

  public String getuName1() {
    return uName1;
  }

  public void setuName1(String uName1) {
    this.uName1 = uName1;
  }

  public Long getUsnSerial2_2GLS() {
    return usnSerial2_2GLS;
  }

  public void setUsnSerial2_2GLS(Long usnSerial2_2GLS) {
    this.usnSerial2_2GLS = usnSerial2_2GLS;
  }

  public String getuName2() {
    return uName2;
  }

  public void setuName2(String uName2) {
    this.uName2 = uName2;
  }

  public Long getuRateGLS() {
    return uRateGLS;
  }

  public void setuRateGLS(Long uRateGLS) {
    this.uRateGLS = uRateGLS;
  }

  public Long getbRateGLS() {
    return bRateGLS;
  }

  public void setbRateGLS(Long bRateGLS) {
    this.bRateGLS = bRateGLS;
  }

  public Long getuRate_2GLS() {
    return uRate_2GLS;
  }

  public void setuRate_2GLS(Long uRate_2GLS) {
    this.uRate_2GLS = uRate_2GLS;
  }

  public Long getbRate_2GLS() {
    return bRate_2GLS;
  }

  public void setbRate_2GLS(Long bRate_2GLS) {
    this.bRate_2GLS = bRate_2GLS;
  }

  public Long getCounted() {
    return counted;
  }

  public void setCounted(Long counted) {
    this.counted = counted;
  }

  @Override
  public Long getPrimaryKey() {
    return gls;
  }
}
