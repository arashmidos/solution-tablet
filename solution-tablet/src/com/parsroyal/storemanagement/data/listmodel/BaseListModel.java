package com.parsroyal.storemanagement.data.listmodel;

import com.parsroyal.storemanagement.data.model.BaseModel;

/**
 * Created by Mahyar on 7/6/2015.
 */
public abstract class BaseListModel extends BaseModel {

  protected Long primaryKey;
  protected String title;
  protected String code;

  public Long getPrimaryKey() {
    return primaryKey;
  }

  public void setPrimaryKey(Long primaryKey) {
    this.primaryKey = primaryKey;
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

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    BaseListModel that = (BaseListModel) o;

    return primaryKey != null ? primaryKey.equals(that.primaryKey) : that.primaryKey == null;

  }

  @Override
  public int hashCode() {
    return primaryKey != null ? primaryKey.hashCode() : 0;
  }
}
