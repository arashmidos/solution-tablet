package com.parsroyal.storemanagement.data.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Arash on 9/20/16.
 */
public class DetectGoodDetail extends BaseModel {

  @SerializedName("Title")
  @Expose
  private String title;
  @SerializedName("Column1")
  @Expose
  private String column1;

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getColumn1() {
    return column1;
  }

  public void setColumn1(String column1) {
    this.column1 = column1;
  }
}
