package com.parsroyal.solutiontablet.data.listmodel;

/**
 * Created by Arash on 08/19/2016
 */
public class PaymentListModel extends BaseListModel
{

    protected String date;
    protected String amount;
    protected String type;
    protected long customerBackendId;
    private String customerFullName;

    public long getCustomerBackendId()
    {
        return customerBackendId;
    }

    public void setCustomerBackendId(long customerBackendId)
    {
        this.customerBackendId = customerBackendId;
    }

    public String getAmount()
    {
        return amount;
    }

    public void setAmount(String amount)
    {
        this.amount = amount;
    }

    public String getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = type;
    }

    public String getDate()
    {
        return date;
    }

    public void setDate(String date)
    {
        this.date = date;
    }

    public String getCustomerFullName()
    {
        return customerFullName;
    }

    public void setCustomerFullName(String customerFullName)
    {
        this.customerFullName = customerFullName;
    }
}
