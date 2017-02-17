package com.conta.comer.data.response;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Arash on 2017-02-16.
 */
public class GeneralResponse
{
    @SerializedName("timestamp")
    private Integer timestamp;
    @SerializedName("status")
    private Integer status;
    @SerializedName("error")
    private String error;
    @SerializedName("message")
    private String message;
    @SerializedName("path")
    private String path;

    public Integer getTimestamp()
    {
        return timestamp;
    }

    public void setTimestamp(Integer timestamp)
    {
        this.timestamp = timestamp;
    }

    public Integer getStatus()
    {
        return status;
    }

    public void setStatus(Integer status)
    {
        this.status = status;
    }

    public String getError()
    {
        return error;
    }

    public void setError(String error)
    {
        this.error = error;
    }

    public String getMessage()
    {
        return message;
    }

    public void setMessage(String message)
    {
        this.message = message;
    }

    public String getPath()
    {
        return path;
    }

    public void setPath(String path)
    {
        this.path = path;
    }
}
