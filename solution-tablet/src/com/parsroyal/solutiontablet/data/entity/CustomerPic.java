package com.parsroyal.solutiontablet.data.entity;

import com.parsroyal.solutiontablet.constants.CustomerStatus;
import com.parsroyal.solutiontablet.util.DateUtil;
import com.parsroyal.solutiontablet.util.Empty;
import java.util.Date;

/**
 * Created by Arash on 6/6/2016.
 */
public class CustomerPic extends BaseEntity<Long> {

  public static final String TABLE_NAME = "COMMER_CUSTOMER_PIC";
  public static final String COL_ID = "_id";
  public static final String COL_BACKEND_ID = "BACKEND_ID";
  public static final String COL_CUSTOMER_BACKEND_ID = "CUSTOMER_BACKEND_ID";
  public static final String COL_CUSTOMER_ID = "CUSTOMER_ID";
  public static final String COL_TITLE = "CUSTOMER_TITLE";
  public static final String COL_STATUS = "STATUS";
  public static final String COL_VISIT_ID = "VISIT_ID";

  public static final String CREATE_TABLE_SQL = "CREATE TABLE " + CustomerPic.TABLE_NAME + " (" +
      " " + CustomerPic.COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
      " " + CustomerPic.COL_BACKEND_ID + " INTEGER," +
      " " + CustomerPic.COL_CUSTOMER_BACKEND_ID + " INTEGER," +
      " " + CustomerPic.COL_CUSTOMER_ID + " INTEGER," +
      " " + CustomerPic.COL_TITLE + " TEXT," +
      " " + CustomerPic.COL_CREATE_DATE_TIME + " TEXT," +
      " " + CustomerPic.COL_STATUS + " INTEGER," +
      " " + CustomerPic.COL_VISIT_ID + " INTEGER" +
      " );";

  private Long id;
  private Long backendId;
  private Long customerBackendId;
  private Long customerId;
  private String title;
  private Long status;
  private Long visitId;

  public CustomerPic(String title, Long customerId) {
    this.title = title;
    this.customerId = customerId;
    status = CustomerStatus.NEW.getId();
    setCreateDateTime(DateUtil.convertDate(new Date(),
        DateUtil.FULL_FORMATTER_GREGORIAN_WITH_TIME, "EN"));
  }

  public CustomerPic() {

  }

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

  public Long getCustomerBackendId() {
    return customerBackendId;
  }

  public void setCustomerBackendId(Long customerBackendId) {
    this.customerBackendId = customerBackendId;
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

  public Long getVisitId() {
    return visitId;
  }

  public void setVisitId(Long visitId) {
    this.visitId = visitId;
  }

  public Long getCustomerId() {
    return customerId;
  }

  public void setCustomerId(Long customerId) {
    this.customerId = customerId;
  }

  public String getName() {
    return Empty.isNotEmpty(title) ? title.substring(title.lastIndexOf("/")+1) : "";
  }
}
