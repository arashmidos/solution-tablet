package com.parsroyal.solutiontablet.constants;

/**
 * Created by mahyar on 3/5/17.
 */
public enum VisitInformationDetailType
{
    CREATE_ORDER(10L),
    DELIVER_ORDER(11L),
    CREATE_REJECT(20L),
    CREATE_INVOICE(30L),
    TAKE_PICTURE(40L),
    FILL_QUESTIONNAIRE(50L),
    SAVE_LOCATION(60L),
    CASH(70L),
    NO_ORDER(98L),
    NONE(99L),;

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
