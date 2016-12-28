package com.conta.comer.data.dao;

import com.conta.comer.data.entity.SaleOrder;
import com.conta.comer.data.model.SaleOrderDto;
import com.conta.comer.data.model.SaleOrderListModel;
import com.conta.comer.data.searchobject.SaleOrderSO;

import java.util.List;

/**
 * Created by Mahyar on 8/21/2015.
 */
public interface SaleOrderDao extends BaseDao<SaleOrder, Long>
{
    List<SaleOrder> retrieveSaleOrderByStatus(Long statusId);

    List<SaleOrderListModel> searchForOrders(SaleOrderSO saleOrderSO);

    SaleOrderDto getOrderDtoById(Long orderId);

    SaleOrderDto getOrderDtoByCustomerBackendIdAndStatus(Long customerBackendId, Long statusId);

    List<SaleOrderDto> findOrderDtoByStatusId(Long statusId);

    void deleteByCustomerBackendIdAndStatus(Long customerBackendId, Long statusId);
}
