package com.parsroyal.storemanagement.data.dao;

import com.parsroyal.storemanagement.data.entity.SaleOrderItem;
import com.parsroyal.storemanagement.data.model.BaseSaleDocumentItem;
import com.parsroyal.storemanagement.data.model.SaleOrderItemDto;
import java.util.List;

/**
 * Created by Mahyar on 8/21/2015.
 */
public interface SaleOrderItemDao extends BaseDao<SaleOrderItem, Long> {

  void deleteAllItemsBySaleOrderId(Long saleOrderId);

  List<SaleOrderItemDto> getAllOrderItemsDtoByOrderId(Long orderId);

  List<BaseSaleDocumentItem> getAllSaleDocumentItemsByOrderId(Long orderId);

  SaleOrderItem getOrderItemByOrderIdAndGoodsId(Long orderId, Long goodsBackendId,
      long invoiceBackendId);
}
