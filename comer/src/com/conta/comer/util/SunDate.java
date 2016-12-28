package com.conta.comer.util;

import com.alirezaafkar.sundatepicker.components.DateItem;

import java.util.Calendar;
import java.util.Locale;

/**
 * Created by Arashmidos on 2016-09-25.
 */
public class SunDate extends DateItem
{
    String getDate()
    {
        Calendar calendar = getCalendar();
        return String.format(Locale.US,
                "%d/%d/%d (%d/%d/%d)",
                getYear(), getMonth(), getDay(),
                calendar.get(Calendar.YEAR),
                +calendar.get(Calendar.MONTH) + 1,
                +calendar.get(Calendar.DAY_OF_MONTH));
    }

}
