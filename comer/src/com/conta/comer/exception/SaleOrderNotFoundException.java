package com.conta.comer.exception;

/**
 * Created by Mahyar on 8/27/2015.
 */
public class SaleOrderNotFoundException extends ContaBusinessException
{
    public SaleOrderNotFoundException()
    {
    }

    public SaleOrderNotFoundException(String detailMessage, String... args)
    {
        super(detailMessage, args);
    }

    public SaleOrderNotFoundException(String detailMessage)
    {
        super(detailMessage);
    }

    public SaleOrderNotFoundException(String detailMessage, Throwable throwable)
    {
        super(detailMessage, throwable);
    }

    public SaleOrderNotFoundException(Throwable throwable)
    {
        super(throwable);
    }
}
