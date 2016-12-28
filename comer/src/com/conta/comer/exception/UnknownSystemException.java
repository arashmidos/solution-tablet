package com.conta.comer.exception;

/**
 * Created by Mahyar on 6/9/2015.
 */
public class UnknownSystemException extends ContaBusinessException
{
    public UnknownSystemException(Exception ex)
    {
        super(ex);
    }
}
