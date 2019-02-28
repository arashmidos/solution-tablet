package com.parsroyal.storemanagement.data.listmodel;

import com.parsroyal.storemanagement.constants.QuestionType;

/**
 * Created by Mahyar on 7/25/2015.
 */
public class QuestionListModel extends BaseListModel {

  private String question;
  private String answer;
  private Integer qOrder;
  private Long answersGroupNo;
  private QuestionType type;

  public String getQuestion() {
    return question;
  }

  public void setQuestion(String question) {
    this.question = question;
  }

  public String getAnswer() {
    return answer;
  }

  public void setAnswer(String answer) {
    this.answer = answer;
  }

  public Integer getqOrder() {
    return qOrder;
  }

  public void setqOrder(Integer qOrder) {
    this.qOrder = qOrder;
  }

  public QuestionType getType() {
    return type;
  }

  public void setType(QuestionType type) {
    this.type = type;
  }

  public Long getAnswersGroupNo() {
    return answersGroupNo;
  }

  public void setAnswersGroupNo(Long answersGroupNo) {
    this.answersGroupNo = answersGroupNo;
  }
}
