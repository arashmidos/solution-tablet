package com.parsroyal.solutiontablet.data.listmodel;

import com.parsroyal.solutiontablet.data.model.BaseModel;

/**
 * Created by Mahyar on 7/6/2015.
 */
public abstract class BaseListModel extends BaseModel
{
    private Long primaryKey;
    private String title;
    private String code;

    public Long getPrimaryKey()
    {
        return primaryKey;
    }

    public void setPrimaryKey(Long primaryKey)
    {
        this.primaryKey = primaryKey;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
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
