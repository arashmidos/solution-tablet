package com.parsroyal.storemanagement.data.model;

import java.util.List;

/**
 * Created by Mahyar on 7/5/2015.
 */
public class QuestionnaireDtoList extends BaseModel {

  private List<QuestionnaireDto> questionnaires;

  public List<QuestionnaireDto> getQuestionnaires() {
    return questionnaires;
  }

  public void setQuestionnaires(List<QuestionnaireDto> questionnaires) {
    this.questionnaires = questionnaires;
  }
}
