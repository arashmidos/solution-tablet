package com.parsroyal.solutiontablet.service.order;

import com.parsroyal.solutiontablet.data.entity.Goods;
import com.parsroyal.solutiontablet.data.entity.SaleOrderItem;
import com.parsroyal.solutiontablet.data.listmodel.SaleOrderListModel;
import com.parsroyal.solutiontablet.data.model.GoodsDtoList;
import com.parsroyal.solutiontablet.data.model.SaleOrderDto;
import com.parsroyal.solutiontablet.data.model.SaleOrderItemDto;
import com.parsroyal.solutiontablet.data.searchobject.SaleOrderSO;
import java.util.List;

/**
 * Created by Mahyar on 8/25/2015.
 * Edited by Arash 7/17/2016
 */
public interface SaleOrderService {

  List<SaleOrderListModel> findOrders(SaleOrderSO saleOrderSO);

  SaleOrderDto findOrderDtoById(Long orderId);

  void deleteForAllCustomerOrdersByStatus(Long customerBackendId, Long statusId);

  void updateOrderItemCount(Long id, Double count, Long selectedUnit, Long orderStatus,
      Goods goods);

  List<SaleOrderItemDto> getOrderItemDtoList(Long orderId);

  List<SaleOrderItemDto> getLocalOrderItemDtoList(Long orderId, GoodsDtoList goodsList);

  void deleteOrderItem(Long itemId, boolean isRejected);

  void changeOrderStatus(Long orderId, Long statusId);

  List<SaleOrderDto> findOrderDtoByStatus(Long statusId);

  SaleOrderDto findOrderDtoByCustomerBackendIdAndStatus(Long backendId, Long statusId);

  Long saveOrder(SaleOrderDto orderDto);

  SaleOrderItem findOrderItemByOrderIdAndGoodsBackendId(Long id, long goodsBackendId,
      long invoiceBackendId);

  Long saveOrderItem(SaleOrderItem saleOrderItem);

  Long updateOrderAmount(Long orderId);

}
