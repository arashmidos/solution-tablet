package com.conta.comer.data.dao;

import com.conta.comer.data.entity.SaleOrderItem;
import com.conta.comer.data.model.SaleOrderItemDto;

import java.util.List;

/**
 * Created by Mahyar on 8/21/2015.
 */
public interface SaleOrderItemDao extends BaseDao<SaleOrderItem, Long>
{

    void deleteAllItemsBySaleOrderId(Long saleOrderId);

    List<SaleOrderItemDto> getAllOrderItemsDtoByOrderId(Long orderId);

    SaleOrderItem getOrderItemByOrderIdAndGoodsId(Long orderId, Long goodsBackendId, long invoiceBackendId);
}
