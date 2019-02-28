package com.parsroyal.storemanagement.data.searchobject;

import com.parsroyal.storemanagement.data.model.BaseModel;

/**
 * Created by Mahyar on 7/13/2015.
 */
public class BaseSO extends BaseModel {

  private String constraint;

  public String getConstraint() {
    return constraint;
  }

  public void setConstraint(String constraint) {
    this.constraint = constraint;
  }
}
