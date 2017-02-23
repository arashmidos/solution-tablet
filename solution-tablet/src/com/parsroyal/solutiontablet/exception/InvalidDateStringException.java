package com.parsroyal.solutiontablet.exception;

/**
 * Created by Admin on 10/4/2014.
 */
public class InvalidDateStringException extends BusinessException
{
    public InvalidDateStringException(String date)
    {
        super(date);
    }
}
