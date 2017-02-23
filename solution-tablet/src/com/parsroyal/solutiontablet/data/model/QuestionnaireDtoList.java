package com.parsroyal.solutiontablet.data.model;

import java.util.List;

/**
 * Created by Mahyar on 7/5/2015.
 */
public class QuestionnaireDtoList extends BaseModel
{

    private List<QuestionnaireDto> questionnaireDtoList;

    public List<QuestionnaireDto> getQuestionnaireDtoList()
    {
        return questionnaireDtoList;
    }

    public void setQuestionnaireDtoList(List<QuestionnaireDto> questionnaireDtoList)
    {
        this.questionnaireDtoList = questionnaireDtoList;
    }
}
