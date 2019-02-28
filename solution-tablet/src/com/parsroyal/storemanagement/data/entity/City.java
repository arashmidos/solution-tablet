package com.parsroyal.storemanagement.data.entity;

/**
 * Created by Mahyar on 6/19/2015.
 */
public class City extends BaseEntity<Long> {

  public static final String TABLE_NAME = "BASE_INFO_CITY";
  public static final String COL_ID = "_id";
  public static final String COL_BACKEND_ID = "BACKEND_ID";
  public static final String COL_CODE = "CODE";
  public static final String COL_TITLE = "TITLE";
  public static final String COL_PROVINCE_BACKEND_ID = "PROVINCE_BACKEND_ID";
  public static final String CREATE_TABLE_SQL = "CREATE TABLE " + City.TABLE_NAME + " (" +
      " " + City.COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
      " " + City.COL_BACKEND_ID + " INTEGER NOT NULL," +
      " " + City.COL_CODE + " INTEGER," +
      " " + City.COL_TITLE + " TEXT NOT NULL," +
      " " + City.COL_PROVINCE_BACKEND_ID + " INTEGER NOT NULL" +
      " );";
  private Long id;
  private Long backendId;
  private Integer code;
  private String title;
  private Long provinceBackendId;

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

  public Long getProvinceBackendId() {
    return provinceBackendId;
  }

  public void setProvinceBackendId(Long provinceBackendId) {
    this.provinceBackendId = provinceBackendId;
  }

  @Override
  public Long getPrimaryKey() {
    return null;
  }
}
