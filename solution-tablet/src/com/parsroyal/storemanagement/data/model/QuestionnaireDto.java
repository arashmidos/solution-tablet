package com.parsroyal.storemanagement.data.model;

import java.util.List;

/**
 * Created by Mahyar on 7/21/2015.
 */
public class QuestionnaireDto extends BaseModel {

  private Long id;
  private String fromDate;
  private String toDate;
  private Long goodsGroupId;
  private Long customerGroupId;
  private String description;
  private Long status;

  private List<QuestionDto> questions;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getFromDate() {
    return fromDate;
  }

  public void setFromDate(String fromDate) {
    this.fromDate = fromDate;
  }

  public String getToDate() {
    return toDate;
  }

  public void setToDate(String toDate) {
    this.toDate = toDate;
  }

  public Long getGoodsGroupId() {
    return goodsGroupId;
  }

  public void setGoodsGroupId(Long goodsGroupId) {
    this.goodsGroupId = goodsGroupId;
  }

  public Long getCustomerGroupId() {
    return customerGroupId;
  }

  public void setCustomerGroupId(Long customerGroupId) {
    this.customerGroupId = customerGroupId;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public Long getStatus() {
    return status;
  }

  public void setStatus(Long status) {
    this.status = status;
  }

  public List<QuestionDto> getQuestions() {
    return questions;
  }

  public void setQuestions(List<QuestionDto> questions) {
    this.questions = questions;
  }
}
