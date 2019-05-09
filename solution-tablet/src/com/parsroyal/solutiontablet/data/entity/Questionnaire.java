package com.parsroyal.solutiontablet.data.entity;

import com.parsroyal.solutiontablet.util.DateUtil;
import java.util.Date;

/**
 * Created by Mahyar on 7/21/2015.
 */
public class Questionnaire extends BaseEntity<Long> {

  public static final String TABLE_NAME = "COMMER_QUESTIONNAIRE";

  public static final String COL_ID = "_id";
  public static final String COL_BACKEND_ID = "BACKEND_ID";
  public static final String COL_FROM_DATE = "FROM_DATE";
  public static final String COL_TO_DATE = "TO_DATE";
  public static final String COL_GOODS_GROUP_BACKEND_ID = "GOODS_GROUP_BACKEND_ID";
  public static final String COL_CUSTOMER_GROUP_BACKEND_ID = "CUSTOMER_GROUP_BACKEND_ID";
  public static final String COL_DESCRIPTION = "DESCRIPTION";
  public static final String COL_STATUS = "STATUS";

  public static final String CREATE_TABLE_SQL = "CREATE TABLE " + Questionnaire.TABLE_NAME + " (" +
      " " + Questionnaire.COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
      " " + Questionnaire.COL_BACKEND_ID + " INTEGER," +
      " " + Questionnaire.COL_FROM_DATE + " TEXT ," +
      " " + Questionnaire.COL_TO_DATE + " TEXT ," +
      " " + Questionnaire.COL_GOODS_GROUP_BACKEND_ID + " INTEGER," +
      " " + Questionnaire.COL_CUSTOMER_GROUP_BACKEND_ID + " INTEGER," +
      " " + Questionnaire.COL_DESCRIPTION + " TEXT," +
      " " + Questionnaire.COL_STATUS + " INTEGER," +
      " " + Questionnaire.COL_CREATE_DATE_TIME + " TEXT," +
      " " + Questionnaire.COL_UPDATE_DATE_TIME + " TEXT" +
      " );";

  private Long id;
  private Long backendId;
  private String fromDate;
  private String toDate;
  private Long goodsGroupBackendId;
  private Long customerGroupBackendId;
  private String description;
  private Long status;

  public Questionnaire(Long backendId, String fromDate, String toDate,
      Long goodsGroupBackendId, Long customerGroupBackendId, String description,
      Long status) {
    this.backendId = backendId;
    this.fromDate = fromDate;
    this.toDate = toDate;
    this.goodsGroupBackendId = goodsGroupBackendId;
    this.customerGroupBackendId = customerGroupBackendId;
    this.description = description;
    this.status = status;
    this.createDateTime = DateUtil
        .convertDate(new Date(), DateUtil.FULL_FORMATTER_GREGORIAN_WITH_TIME, "EN");
    this.updateDateTime = DateUtil
        .convertDate(new Date(), DateUtil.FULL_FORMATTER_GREGORIAN_WITH_TIME, "EN");
  }

  public Questionnaire() {
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

  public String getFromDate() {
    return fromDate;
  }

  public void setFromDate(String fromDate) {
    this.fromDate = fromDate;
  }

  public String getToDate() {
    return toDate;
  }

  public void setToDate(String toDate) {
    this.toDate = toDate;
  }

  public Long getGoodsGroupBackendId() {
    return goodsGroupBackendId;
  }

  public void setGoodsGroupBackendId(Long goodsGroupBackendId) {
    this.goodsGroupBackendId = goodsGroupBackendId;
  }

  public Long getCustomerGroupBackendId() {
    return customerGroupBackendId;
  }

  public void setCustomerGroupBackendId(Long customerGroupBackendId) {
    this.customerGroupBackendId = customerGroupBackendId;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public Long getStatus() {
    return status;
  }

  public void setStatus(Long status) {
    this.status = status;
  }

  @Override
  public Long getPrimaryKey() {
    return id;
  }
}
