package com.parsroyal.solutiontablet.data.model;

/**
 * Created by Arash 2017-08-21
 */
public class AnswerDetailDto extends BaseModel {

  private Long questionId;
  private String answer;
  private Long goodsId;
  private Long answerId;

  public AnswerDetailDto() {
  }

  public AnswerDetailDto(Long questionId, String answer, Long goodsId, Long answerId) {
    this.questionId = questionId;
    this.answer = answer;
    this.goodsId = goodsId;
    this.answerId = answerId;
  }

  public Long getQuestionId() {
    return questionId;
  }

  public void setQuestionId(Long questionId) {
    this.questionId = questionId;
  }

  public String getAnswer() {
    return answer;
  }

  public void setAnswer(String answer) {
    this.answer = answer;
  }

  public Long getGoodsId() {
    return goodsId;
  }

  public void setGoodsId(Long goodsId) {
    this.goodsId = goodsId;
  }

  public Long getAnswerId() {
    return answerId;
  }

  public void setAnswerId(Long answerId) {
    this.answerId = answerId;
  }
}
