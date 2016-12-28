package com.conta.comer.constants;

/**
 * Created by Arash on 2016-08-13
 */
public enum SendStatus
{
    NEW(9999L, "NEW"),
    UPDATED(9998L,"UPDATED"),
    SENT(9997L,"SENT");

    private Long id;
    private String title;

    SendStatus(Long id, String title)
    {
        this.id = id;
        this.title = title;
    }

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

}
