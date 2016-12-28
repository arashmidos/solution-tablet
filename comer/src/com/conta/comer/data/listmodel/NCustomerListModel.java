package com.conta.comer.data.listmodel;

/**
 * Created by Mahyar on 7/13/2015.
 */
public class NCustomerListModel extends CustomerListModel
{

    private Long status;
    private Long backendId;
    private String phoneNumber;
    private String cellPhone;
    private String createDateTime;

    public Long getStatus()
    {
        return status;
    }

    public void setStatus(Long status)
    {
        this.status = status;
    }

    public Long getBackendId()
    {
        return backendId;
    }

    public void setBackendId(Long backendId)
    {
        this.backendId = backendId;
    }

    @Override
    public String getPhoneNumber()
    {
        return phoneNumber;
    }

    @Override
    public void setPhoneNumber(String phoneNumber)
    {
        this.phoneNumber = phoneNumber;
    }

    @Override
    public String getCellPhone()
    {
        return cellPhone;
    }

    @Override
    public void setCellPhone(String cellPhone)
    {
        this.cellPhone = cellPhone;
    }

    public String getCreateDateTime()
    {
        return createDateTime;
    }

    public void setCreateDateTime(String createDateTime)
    {
        this.createDateTime = createDateTime;
    }
}
