package com.parsroyal.solutiontablet.data.dao;

import com.parsroyal.solutiontablet.data.entity.SaleOrderItem;
import com.parsroyal.solutiontablet.data.model.SaleOrderItemDto;

import java.util.List;

/**
 * Created by Mahyar on 8/21/2015.
 */
public interface SaleOrderItemDao extends BaseDao<SaleOrderItem, Long>
{

    void deleteAllItemsBySaleOrderId(Long saleOrderId);

    List<SaleOrderItemDto> getAllOrderItemsDtoByOrderId(Long orderId);

    SaleOrderItem getOrderItemByOrderIdAndGoodsId(Long orderId, Long goodsBackendId,
                                                  long invoiceBackendId);
}
