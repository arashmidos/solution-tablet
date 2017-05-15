package com.parsroyal.solutiontablet.data.listmodel;

import com.parsroyal.solutiontablet.constants.QuestionType;

/**
 * Created by Mahyar on 7/25/2015.
 */
public class QuestionListModel extends BaseListModel {

  private String question;
  private String answer;
  private Integer qOrder;
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
}
