package com.parsroyal.solutiontablet.data.searchobject;

/**
 * Created by Mahyar on 7/25/2015.
 */
public class QuestionSo extends BaseSO {

  private Long questionnaireBackendId;
  private Long visitId;
  private Long goodsBackendId;

  public Long getQuestionnaireBackendId() {
    return questionnaireBackendId;
  }

  public void setQuestionnaireBackendId(Long questionnaireBackendId) {
    this.questionnaireBackendId = questionnaireBackendId;
  }

  public Long getVisitId() {
    return visitId;
  }

  public void setVisitId(Long visitId) {
    this.visitId = visitId;
  }

  public Long getGoodsBackendId() {
    return goodsBackendId;
  }

  public void setGoodsBackendId(Long goodsBackendId) {
    this.goodsBackendId = goodsBackendId;
  }
}
