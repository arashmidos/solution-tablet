package com.parsroyal.storemanagement.data.model;

import java.util.Date;
import java.util.List;

/**
 * Created by Arash on 2017-08-21
 */
public class QAnswerDto extends BaseModel {

  private Long salesmanId;
  private Long customerId;
  //  private Long visitId;
  private Date date;
  private Long questionnaireId;
  private Long answersGroupNo;
  private List<AnswerDetailDto> answers;

  public QAnswerDto(Long customerId, Long visitId, Date date,
      Long questionnaireId, Long answersGroupNo) {
    this.customerId = customerId;
//    this.visitId = visitId;
    this.date = date;
    this.questionnaireId = questionnaireId;
    this.answersGroupNo = answersGroupNo;
  }

  public QAnswerDto() {
  }

  public Long getSalesmanId() {
    return salesmanId;
  }

  public void setSalesmanId(Long salesmanId) {
    this.salesmanId = salesmanId;
  }

  public Long getCustomerId() {
    return customerId;
  }

  public void setCustomerId(Long customerId) {
    this.customerId = customerId;
  }

//  public Long getVisitId() {
//    return visitId;
//  }

//  public void setVisitId(Long visitId) {
//    this.visitId = visitId;
//  }

  public Date getDate() {
    return date;
  }

  public void setDate(Date date) {
    this.date = date;
  }

  public Long getQuestionnaireId() {
    return questionnaireId;
  }

  public void setQuestionnaireId(Long questionnaireId) {
    this.questionnaireId = questionnaireId;
  }

  public Long getAnswersGroupNo() {
    return answersGroupNo;
  }

  public void setAnswersGroupNo(Long answersGroupNo) {
    this.answersGroupNo = answersGroupNo;
  }

  public List<AnswerDetailDto> getAnswers() {
    return answers;
  }

  public void setAnswers(List<AnswerDetailDto> answers) {
    this.answers = answers;
  }
}
