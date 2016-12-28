package com.conta.comer.constants;

/**
 * Created by Arash on 2016-08-13
 */
public enum PaymentType
{
    CASH(1L, "وجه نقد"),
    POS(2L,"پرداخت الکترونیکی"),
    CHEQUE(6L,"چک");

    private Long id;
    private String title;

    PaymentType(Long id, String title)
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
