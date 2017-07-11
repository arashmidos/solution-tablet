package com.parsroyal.solutiontablet.data.searchobject;

/**
 * Created by Mahyar on 7/24/2015.
 */
public class QuestionnaireSo extends BaseSO {

  private boolean general;
  private boolean anonymous;
  private long visitId;

  public long getVisitId() {
    return visitId;
  }

  public void setVisitId(long visitId) {
    this.visitId = visitId;
  }

  public QuestionnaireSo(boolean isGeneral) {
    super();
    setGeneral(isGeneral);
  }

  public QuestionnaireSo() {
    super();
  }

  public boolean isAnonymous() {
    return anonymous;
  }

  public void setAnonymous(boolean anonymous) {
    this.anonymous = anonymous;
  }

  public boolean isGeneral() {
    return general;
  }

  public void setGeneral(boolean general) {
    this.general = general;
  }
}
