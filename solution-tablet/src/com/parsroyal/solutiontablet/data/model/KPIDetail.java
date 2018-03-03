package com.parsroyal.solutiontablet.data.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Arash on 9/20/16.
 */
public class KPIDetail extends BaseModel {

  @SerializedName("Column2")
  @Expose
  private String column2;
  @SerializedName("Title")
  @Expose
  private String title;
  @SerializedName("Column3")
  @Expose
  private String column3;
  @SerializedName("Column4")
  @Expose
  private String column4;
  @SerializedName("id")
  @Expose
  private Integer id;
  @SerializedName("Column1")
  @Expose
  private String column1;

  public String getColumn2() {
    return column2;
  }

  public void setColumn2(String column2) {
    this.column2 = column2;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getColumn3() {
    return column3;
  }

  public void setColumn3(String column3) {
    this.column3 = column3;
  }

  public String getColumn4() {
    return column4;
  }

  public void setColumn4(String column4) {
    this.column4 = column4;
  }

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public String getColumn1() {
    return column1;
  }

  public void setColumn1(String column1) {
    this.column1 = column1;
  }
}
