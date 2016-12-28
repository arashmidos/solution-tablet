package com.conta.comer.data.model;

import com.conta.comer.data.entity.Goods;

/**
 * Created by Mahyar on 8/27/2015.
 */
public class SaleOrderItemDto extends BaseModel
{
    private Long id;
    private Long goodsBackendId;
    private Long goodsCount;
    private Long amount;
    private Long saleOrderId;
    private Long saleOrderBackendId;
    private Long selectedUnit;
    private Long backendId;
    private String createDateTime;
    private String updateDateTime;
    private Long invoiceBackendId;
    private Goods goods;

    public Long getInvoiceBackendId()
    {
        return invoiceBackendId;
    }

    public void setInvoiceBackendId(Long invoiceBackendId)
    {
        this.invoiceBackendId = invoiceBackendId;
    }

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public Long getGoodsBackendId()
    {
        return goodsBackendId;
    }

    public void setGoodsBackendId(Long goodsBackendId)
    {
        this.goodsBackendId = goodsBackendId;
    }

    public Long getGoodsCount()
    {
        return goodsCount;
    }

    public void setGoodsCount(Long goodsCount)
    {
        this.goodsCount = goodsCount;
    }

    public Long getAmount()
    {
        return amount;
    }

    public void setAmount(Long amount)
    {
        this.amount = amount;
    }

    public Long getSaleOrderId()
    {
        return saleOrderId;
    }

    public void setSaleOrderId(Long saleOrderId)
    {
        this.saleOrderId = saleOrderId;
    }

    public Long getSaleOrderBackendId()
    {
        return saleOrderBackendId;
    }

    public void setSaleOrderBackendId(Long saleOrderBackendId)
    {
        this.saleOrderBackendId = saleOrderBackendId;
    }

    public Long getSelectedUnit()
    {
        return selectedUnit;
    }

    public void setSelectedUnit(Long selectedUnit)
    {
        this.selectedUnit = selectedUnit;
    }

    public Long getBackendId()
    {
        return backendId;
    }

    public void setBackendId(Long backendId)
    {
        this.backendId = backendId;
    }

    public Goods getGoods()
    {
        return goods;
    }

    public void setGoods(Goods goods)
    {
        this.goods = goods;
    }

    public String getCreateDateTime()
    {
        return createDateTime;
    }

    public void setCreateDateTime(String createDateTime)
    {
        this.createDateTime = createDateTime;
    }

    public String getUpdateDateTime()
    {
        return updateDateTime;
    }

    public void setUpdateDateTime(String updateDateTime)
    {
        this.updateDateTime = updateDateTime;
    }
}
