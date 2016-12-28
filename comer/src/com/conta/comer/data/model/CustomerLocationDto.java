package com.conta.comer.data.model;

/**
 * Created by mahyarsefidi1 on 8/17/16.
 */
public class CustomerLocationDto
{

    private Long customerBackendId;
    private Double latitude;
    private Double longitude;

    public Long getCustomerBackendId()
    {
        return customerBackendId;
    }

    public void setCustomerBackendId(Long customerBackendId)
    {
        this.customerBackendId = customerBackendId;
    }

    public Double getLatitude()
    {
        return latitude;
    }

    public void setLatitude(Double latitude)
    {
        this.latitude = latitude;
    }

    public Double getLongitude()
    {
        return longitude;
    }

    public void setLongitude(Double longitude)
    {
        this.longitude = longitude;
    }
}
