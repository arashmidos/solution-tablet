package com.parsroyal.solutiontablet.data.entity;

/**
 * Created by Mahyar on 7/6/2015.
 */
public class VisitLine extends BaseEntity<Long> {

  public static final String TABLE_NAME = "COMMER_VISIT_LINE";
  public static final String COL_ID = "_id";
  public static final String COL_BACKEND_ID = "BACKEND_ID";
  public static final String COL_CODE = "CODE";
  public static final String COL_TITLE = "TITLE";

  public static final String CREATE_TABLE_SQL = "CREATE TABLE " + VisitLine.TABLE_NAME + " (" +
      " " + VisitLine.COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
      " " + VisitLine.COL_BACKEND_ID + " INTEGER," +
      " " + VisitLine.COL_CODE + " TEXT," +
      " " + VisitLine.COL_TITLE + " TEXT" +
      " );";

  private Long id;
  private Long backendId;
  private Integer code;
  private String title;

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

  public Integer getCode() {
    return code;
  }

  public void setCode(Integer code) {
    this.code = code;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  @Override
  public Long getPrimaryKey() {
    return id;
  }
}
