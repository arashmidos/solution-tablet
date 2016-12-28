package com.conta.comer.exception;

/**
 * Created by Admin on 10/4/2014.
 */
public class InvalidDateStringException extends ContaBusinessException
{
    public InvalidDateStringException(String date)
    {
        super(date);
    }
}
