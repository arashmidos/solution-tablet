package com.parsroyal.storemanagement.service;

import com.parsroyal.storemanagement.data.entity.Goods;
import com.parsroyal.storemanagement.data.entity.SaleOrderItem;
import com.parsroyal.storemanagement.data.listmodel.SaleOrderListModel;
import com.parsroyal.storemanagement.data.model.BaseSaleDocument;
import com.parsroyal.storemanagement.data.model.BaseSaleDocumentItem;
import com.parsroyal.storemanagement.data.model.GoodsDtoList;
import com.parsroyal.storemanagement.data.model.SaleOrderDto;
import com.parsroyal.storemanagement.data.model.SaleOrderItemDto;
import com.parsroyal.storemanagement.data.searchobject.SaleOrderSO;
import java.util.List;

/**
 * Created by Mahyar on 8/25/2015.
 */
public interface SaleOrderService extends BaseService {

  List<SaleOrderListModel> findOrders(SaleOrderSO saleOrderSO);

  SaleOrderDto findOrderDtoById(Long orderId);

  void deleteForAllCustomerOrdersByStatus(Long customerBackendId, Long statusId);
  void deleteOrder(Long orderId);

  void updateOrderItemCount(Long itemId, Double count, Long selectedUnit, Long orderStatus,
      Goods localGood, Long discount);

  List<SaleOrderItemDto> getOrderItemDtoList(Long orderId);
  List<BaseSaleDocumentItem> getSaleDocumentItems(Long orderId);

  List<SaleOrderItemDto> getLocalOrderItemDtoList(Long orderId, GoodsDtoList goodsList);

  void deleteOrderItem(Long itemId, boolean isRejected);

  void changeOrderStatus(Long orderId, Long statusId);

  List<BaseSaleDocument> findOrderDocumentByStatus(Long statusId);
  BaseSaleDocument findOrderDocumentByOrderId(Long orderId);

  SaleOrderDto findOrderDtoByCustomerBackendIdAndStatus(Long backendId, Long statusId);

  Long saveOrder(SaleOrderDto orderDto);

  SaleOrderItem findOrderItemByOrderIdAndGoodsBackendId(Long id, long goodsBackendId,
      long invoiceBackendId);

  Long saveOrderItem(SaleOrderItem saleOrderItem);

  Long updateOrderAmount(Long orderId);

}
