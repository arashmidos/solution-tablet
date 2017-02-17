package com.conta.comer.data.response;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Arash on 2017-02-16.
 */

public class UpdateResponse extends GeneralResponse
{
    @SerializedName("id")
    private Integer id;
    @SerializedName("applicationKey")
    private String applicationKey;
    @SerializedName("downloadUrl")
    private String downloadUrl;
    @SerializedName("version")
    private Integer version;
    @SerializedName("published")
    private Boolean published;

    public Integer getId()
    {
        return id;
    }

    public void setId(Integer id)
    {
        this.id = id;
    }

    public String getApplicationKey()
    {
        return applicationKey;
    }

    public void setApplicationKey(String applicationKey)
    {
        this.applicationKey = applicationKey;
    }

    public String getDownloadUrl()
    {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl)
    {
        this.downloadUrl = downloadUrl;
    }

    public Integer getVersion()
    {
        return version;
    }

    public void setVersion(Integer version)
    {
        this.version = version;
    }

    public Boolean getPublished()
    {
        return published;
    }

    public void setPublished(Boolean published)
    {
        this.published = published;
    }

    public boolean isSuccess()
    {
        return getError() == null;
    }
}
