package com.conta.comer.data.model;

/**
 * Created by Mahyar on 6/22/2015.
 */
public class LabelValue extends BaseModel
{

    private String label;
    private Long value;
    private String code;

    public LabelValue(Long value, String label)
    {
        this.label = label;
        this.value = value;
    }

    public String getLabel()
    {
        return label;
    }

    public void setLabel(String label)
    {
        this.label = label;
    }

    public Long getValue()
    {
        return value;
    }

    public void setValue(Long value)
    {
        this.value = value;
    }

    public String getCode()
    {
        return code;
    }

    public void setCode(String code)
    {
        this.code = code;
    }
}
