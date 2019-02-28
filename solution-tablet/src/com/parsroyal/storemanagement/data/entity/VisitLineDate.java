package com.parsroyal.storemanagement.data.entity;

/**
 * Created by Arash on 6/19/2015.
 */
public class VisitLineDate extends BaseEntity<Long> {

  public static final String COL_ID = "_id";
  public static final String COL_BACKEND_ID = "BACKEND_ID";
  public static final String COL_BACKEND_DATE = "BACKEND_DATE";
  public static final String COL_BACKEND_DATE_GREGORIAN = "BACKEND_DATE_GREGORIAN";

  public static final String TABLE_NAME = "COMMER_VISITLINE_DATE";

  public static final String CREATE_TABLE_SQL = "CREATE TABLE " + VisitLineDate.TABLE_NAME + " (" +
      " " + VisitLineDate.COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
      " " + VisitLineDate.COL_BACKEND_ID + " INTEGER," +
      " " + VisitLineDate.COL_BACKEND_DATE + " TEXT NOT NULL," +
      " " + VisitLineDate.COL_BACKEND_DATE_GREGORIAN + " TEXT NOT NULL" +
      " );";

  private Long id;
  private Long backendId;
  private String backendDate;
  private String backendDateGregorian;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getBackendDateGregorian() {
    return backendDateGregorian;
  }

  public void setBackendDateGregorian(String backendDateGregorian) {
    this.backendDateGregorian = backendDateGregorian;
  }

  @Override
  public Long getPrimaryKey() {
    return id;
  }

  public String getBackendDate() {
    return backendDate;
  }

  public void setBackendDate(String backendDate) {
    this.backendDate = backendDate;
  }

  public Long getBackendId() {
    return backendId;
  }

  public void setBackendId(Long backendId) {
    this.backendId = backendId;
  }
}
