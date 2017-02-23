package com.parsroyal.solutiontablet.constants;

/**
 * Created by Mahyar on 9/4/2015.
 */
public enum VisitResultTypes
{
    WANT(10001L),;

    private Long id;

    VisitResultTypes(Long id)
    {
        this.id = id;
    }

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }
}
