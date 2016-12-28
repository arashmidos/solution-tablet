package com.conta.comer.data.model;

import com.conta.comer.data.entity.SaleOrder;

import java.util.List;

/**
 * Created by Mahyar on 8/14/2015.
 */
public class SaleOrderList extends BaseModel
{

    private List<SaleOrder> orderList;

    public List<SaleOrder> getOrderList()
    {
        return orderList;
    }

    public void setOrderList(List<SaleOrder> orderList)
    {
        this.orderList = orderList;
    }
}
