package com.conta.comer.data.listmodel;

import com.conta.comer.data.model.BaseListModel;

/**
 * Created by Mahyar on 7/6/2015.
 */
public class CustomerListModel extends BaseListModel
{
    protected String address;
    protected String phoneNumber;
    protected String cellPhone;

    public String getAddress()
    {
        return address;
    }

    public void setAddress(String address)
    {
        this.address = address;
    }

    public String getPhoneNumber()
    {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber)
    {
        this.phoneNumber = phoneNumber;
    }

    public String getCellPhone()
    {
        return cellPhone;
    }

    public void setCellPhone(String cellPhone)
    {
        this.cellPhone = cellPhone;
    }
}
