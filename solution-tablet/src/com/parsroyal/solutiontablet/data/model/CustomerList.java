package com.parsroyal.solutiontablet.data.model;

import com.parsroyal.solutiontablet.data.entity.Customer;

import java.util.List;

/**
 * Created by Mahyar on 6/23/2015.
 */
public class CustomerList extends BaseModel
{
    private List<Customer> customerList;

    public List<Customer> getCustomerList()
    {
        return customerList;
    }

    public void setCustomerList(List<Customer> customerList)
    {
        this.customerList = customerList;
    }
}
