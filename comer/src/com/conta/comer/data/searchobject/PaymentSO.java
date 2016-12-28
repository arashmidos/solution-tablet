package com.conta.comer.data.searchobject;

/**
 * Created by Arash on 8/21/2016.
 */
public class PaymentSO extends BaseSO
{
    private Long sent;
    private Long customerBackendId;

    public PaymentSO(Long customerBackendId, Long sentStatus)
    {
        this.customerBackendId = customerBackendId;
        this.sent = sentStatus;
    }

    public PaymentSO(Long sentStatus)
    {
        this.sent = sentStatus;
        this.customerBackendId = -1L;
    }

    public Long getSent()
    {
        return sent;
    }

    public void setSent(Long sent)
    {
        this.sent = sent;
    }

    public Long getCustomerBackendId()
    {
        return customerBackendId;
    }

    public void setCustomerBackendId(Long customerBackendId)
    {
        this.customerBackendId = customerBackendId;
    }
}
