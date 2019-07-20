package com.parsroyal.solutiontablet.data.entity;

import androidx.annotation.NonNull;

/**
 * Created by Mahyar on 7/23/2015.
 */
public class GoodsGroup extends BaseEntity<Long> implements Comparable<GoodsGroup> {

  public static final String TABLE_NAME = "COMMER_GOODS_GROUP";

  public static final String COL_ID = "_id";
  public static final String COL_BACKEND_ID = "BACKEND_ID";
  public static final String COL_PARENT_BACKEND_ID = "PARENT_BACKEND_ID";
  public static final String COL_TITLE = "TITLE";
  public static final String COL_CODE = "CODE";
  public static final String COL_LEVEL = "LEVEL";

  public static final String CREATE_TABLE_SQL = "CREATE TABLE " + GoodsGroup.TABLE_NAME + " (" +
      " " + GoodsGroup.COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
      " " + GoodsGroup.COL_BACKEND_ID + " INTEGER," +
      " " + GoodsGroup.COL_PARENT_BACKEND_ID + " INTEGER," +
      " " + GoodsGroup.COL_TITLE + " TEXT," +
      " " + GoodsGroup.COL_CODE + " TEXT," +
      " " + GoodsGroup.COL_LEVEL + " INTEGER," +
      " " + GoodsGroup.COL_CREATE_DATE_TIME + " TEXT," +
      " " + GoodsGroup.COL_UPDATE_DATE_TIME + " TEXT" +
      " );";

  private Long id;
  private Long backendId;
  private Long parentBackendId;
  private String title;
  private String code;
  private Integer level;

  public GoodsGroup() {
  }

  public GoodsGroup(String title, Integer level) {
    this.title = title;
    this.level = level;
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

  public Long getParentBackendId() {
    return parentBackendId;
  }

  public void setParentBackendId(Long parentBackendId) {
    this.parentBackendId = parentBackendId;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public Integer getLevel() {
    return level;
  }

  public void setLevel(Integer level) {
    this.level = level;
  }

  @Override
  public Long getPrimaryKey() {
    return id;
  }

  @Override
  public int compareTo(@NonNull GoodsGroup goodsGroup) {
    return backendId.compareTo(goodsGroup.backendId);
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (!(obj instanceof GoodsGroup)) {
      return false;
    }
    if (((GoodsGroup) obj).getId() != null && ((GoodsGroup) obj).getId().equals(this.id)) {
      return true;
    }
    return false;
  }
}
