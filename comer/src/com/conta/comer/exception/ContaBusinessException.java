package com.conta.comer.exception;

/**
 * Created by Mahyar on 6/4/2015.
 */
public class ContaBusinessException extends RuntimeException
{

    private String[] args;

    public ContaBusinessException()
    {
    }

    public ContaBusinessException(String detailMessage, String... args)
    {
        super(detailMessage);
        this.args = args;
    }

    public ContaBusinessException(String detailMessage)
    {
        super(detailMessage);
    }

    public ContaBusinessException(String detailMessage, Throwable throwable)
    {
        super(detailMessage, throwable);
    }

    public ContaBusinessException(Throwable throwable)
    {
        super(throwable);
    }

    public String[] getArgs()
    {
        return args;
    }

    public void setArgs(String[] args)
    {
        this.args = args;
    }
}
