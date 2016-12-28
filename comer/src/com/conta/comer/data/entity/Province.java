package com.conta.comer.data.entity;

/**
 * Created by Mahyar on 6/15/2015.
 */
public class Province extends BaseEntity<Long>
{

    public static final String TABLE_NAME = "BASE_INFO_PROVINCE";
    public static final String COL_ID = "_id";
    public static final String COL_BACKEND_ID = "BACKEND_ID";
    public static final String COL_CODE = "CODE";
    public static final String COL_TITLE = "TITLE";
    public static final String CREATE_TABLE_SQL = "CREATE TABLE " + Province.TABLE_NAME + " (" +
            " " + Province.COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            " " + Province.COL_BACKEND_ID + " INTEGER NOT NULL," +
            " " + Province.COL_CODE + " INTEGER," +
            " " + Province.COL_TITLE + " TEXT NOT NULL" +
            " );";
    private Long id;
    private Long backendId;
    private Integer code;
    private String title;

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public Long getBackendId()
    {
        return backendId;
    }

    public void setBackendId(Long backendId)
    {
        this.backendId = backendId;
    }

    public Integer getCode()
    {
        return code;
    }

    public void setCode(Integer code)
    {
        this.code = code;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    @Override
    public Long getPrimaryKey()
    {
        return id;
    }

}
