package com.parsroyal.solutiontablet.data.entity;

/**
 * Created by Arash on 6/6/2016.
 */
public class CustomerPic extends BaseEntity<Long> {

  public static final String TABLE_NAME = "COMMER_CUSTOMER_PIC";
  public static final String COL_ID = "_id";
  public static final String COL_BACKEND_ID = "BACKEND_ID";
  public static final String COL_CUSTOMER_BACKEND_ID = "CUSTOMER_BACKEND_ID";
  public static final String COL_TITLE = "CUSTOMER_TITLE";
  public static final String COL_STATUS = "STATUS";

  public static final String CREATE_TABLE_SQL = "CREATE TABLE " + CustomerPic.TABLE_NAME + " (" +
      " " + CustomerPic.COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
      " " + CustomerPic.COL_BACKEND_ID + " INTEGER," +
      " " + CustomerPic.COL_CUSTOMER_BACKEND_ID + " INTEGER," +
      " " + CustomerPic.COL_TITLE + " TEXT," +
      " " + CustomerPic.COL_CREATE_DATE_TIME + " TEXT," +
      " " + CustomerPic.COL_STATUS + " INTEGER" +
      " );";

  private Long id;
  private Long backendId;
  private Long customer_backend_id;
  private String title;
  private Long status;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
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

  public Long getCustomer_backend_id() {
    return customer_backend_id;
  }

  public void setCustomer_backend_id(Long customer_backend_id) {
    this.customer_backend_id = customer_backend_id;
  }

  public Long getStatus() {
    return status;
  }

  public void setStatus(Long status) {
    this.status = status;
  }

  public Long getBackendId() {
    return backendId;
  }

  public void setBackendId(Long backendId) {
    this.backendId = backendId;
  }
}
