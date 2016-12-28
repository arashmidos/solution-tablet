package com.conta.comer.service.order;

import com.conta.comer.data.entity.Goods;
import com.conta.comer.data.entity.SaleOrderItem;
import com.conta.comer.data.model.GoodsDtoList;
import com.conta.comer.data.model.SaleOrderDto;
import com.conta.comer.data.model.SaleOrderItemDto;
import com.conta.comer.data.model.SaleOrderListModel;
import com.conta.comer.data.searchobject.SaleOrderSO;

import java.util.List;

/**
 * Created by Mahyar on 8/25/2015.
 * Edited by Arash 7/17/2016
 */
public interface SaleOrderService
{
    List<SaleOrderListModel> findOrders(SaleOrderSO saleOrderSO);

    SaleOrderDto findOrderDtoById(Long orderId);

    void deleteForAllCustomerOrdersByStatus(Long customerBackendId, Long statusId);

    void updateOrderItemCount(Long id, Double count, Long selectedUnit, Long orderStatus, Goods goods);

    List<SaleOrderItemDto> getOrderItemDtoList(Long orderId);

    List<SaleOrderItemDto> getLocalOrderItemDtoList(Long orderId, GoodsDtoList goodsList);

    void deleteOrderItem(Long itemId, boolean isRejected);

    void changeOrderStatus(Long orderId, Long statusId);

    List<SaleOrderDto> findOrderDtoByStatus(Long statusId);

    SaleOrderDto findOrderDtoByCustomerBackendIdAndStatus(Long backendId, Long statusId);

    Long saveOrder(SaleOrderDto orderDto);

    SaleOrderItem findOrderItemByOrderIdAndGoodsBackendId(Long id, long goodsBackendId,long invoiceBackendId);

    Long saveOrderItem(SaleOrderItem saleOrderItem);

    Long updateOrderAmount(Long orderId);

}
