package com.parsroyal.solutiontablet.data.model;

import com.parsroyal.solutiontablet.data.entity.Question;
import java.util.List;

/**
 * Created by Mahyar on 7/21/2015.
 */
public class QuestionnaireDto extends BaseModel {

  private Long backendId;
  private String fromDate;
  private String toDate;
  private Long goodsGroupBackendId;
  private Long customerGroupBackendId;
  private String description;
  private Long status;

  private List<Question> questions;

  public Long getBackendId() {
    return backendId;
  }

  public void setBackendId(Long backendId) {
    this.backendId = backendId;
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

  public Long getGoodsGroupBackendId() {
    return goodsGroupBackendId;
  }

  public void setGoodsGroupBackendId(Long goodsGroupBackendId) {
    this.goodsGroupBackendId = goodsGroupBackendId;
  }

  public Long getCustomerGroupBackendId() {
    return customerGroupBackendId;
  }

  public void setCustomerGroupBackendId(Long customerGroupBackendId) {
    this.customerGroupBackendId = customerGroupBackendId;
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

  public List<Question> getQuestions() {
    return questions;
  }

  public void setQuestions(List<Question> questions) {
    this.questions = questions;
  }
}
