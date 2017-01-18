package com.conta.comer.data.searchobject;

/**
 * Created by Mahyar on 7/24/2015.
 */
public class QuestionnaireSo extends BaseSO
{
    private boolean general;

    public QuestionnaireSo(boolean isGeneral)
    {
        super();
        setGeneral(isGeneral);
    }

    public QuestionnaireSo()
    {
        super();
    }

    public boolean isGeneral()
    {
        return general;
    }

    public void setGeneral(boolean general)
    {
        this.general = general;
    }
}
