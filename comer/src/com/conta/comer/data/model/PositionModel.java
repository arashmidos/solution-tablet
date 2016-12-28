package com.conta.comer.data.model;

import com.conta.comer.data.entity.Customer;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

/**
 * Created by Arashmidos on 2016-11-11.
 */

public class PositionModel implements ClusterItem
{
    private Customer customer;

    public PositionModel(Customer customer)
    {
        this.customer = customer;
    }

    @Override
    public LatLng getPosition()
    {
        return new LatLng(customer.getxLocation(), customer.getyLocation());
    }

    public String getName()
    {
        return customer.getFullName();
    }

    public long getCustomerBackendId()
    {
        return customer.getBackendId();
    }

    public long getCustomerId()
    {
        return customer.getId();
    }
}
