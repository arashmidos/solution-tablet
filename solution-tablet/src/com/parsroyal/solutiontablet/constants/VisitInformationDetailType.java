package com.parsroyal.solutiontablet.constants;

/**
 * Created by mahyar on 3/5/17.
 */
public enum VisitInformationDetailType
{
    //TODO: Clear comments here
    CREATE_ORDER(10L),
    CREATE_REJECT(20L),
    CREATE_INVOICE(30L),
    TAKE_PICTURE(40L),//Done
    FILL_QUESTIONNAIRE(50L),//Done
    SAVE_LOCATION(60L),//Done
    CASH(70L),//Done
    NONE(99L),;//Done

    private long value;

    VisitInformationDetailType(long value)
    {
        this.value = value;
    }

    public static VisitInformationDetailType getByValue(long value)
    {
        VisitInformationDetailType found = null;
        for (VisitInformationDetailType visitType : VisitInformationDetailType.values())
        {
            if (visitType.getValue() == value)
            {
                found = visitType;
            }
        }
        return found;
    }

    public long getValue()
    {
        return value;
    }

    public void setValue(long value)
    {
        this.value = value;
    }
}
