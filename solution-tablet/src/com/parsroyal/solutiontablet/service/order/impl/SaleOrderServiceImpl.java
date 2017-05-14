package com.parsroyal.solutiontablet.service.order.impl;

import android.content.Context;

import com.parsroyal.solutiontablet.constants.SaleOrderStatus;
import com.parsroyal.solutiontablet.data.dao.CustomerDao;
import com.parsroyal.solutiontablet.data.dao.GoodsDao;
import com.parsroyal.solutiontablet.data.dao.SaleOrderDao;
import com.parsroyal.solutiontablet.data.dao.SaleOrderItemDao;
import com.parsroyal.solutiontablet.data.dao.impl.CustomerDaoImpl;
import com.parsroyal.solutiontablet.data.dao.impl.GoodsDaoImpl;
import com.parsroyal.solutiontablet.data.dao.impl.SaleOrderDaoImpl;
import com.parsroyal.solutiontablet.data.dao.impl.SaleOrderItemDaoImpl;
import com.parsroyal.solutiontablet.data.entity.Customer;
import com.parsroyal.solutiontablet.data.entity.Goods;
import com.parsroyal.solutiontablet.data.entity.SaleOrder;
import com.parsroyal.solutiontablet.data.entity.SaleOrderItem;
import com.parsroyal.solutiontablet.data.listmodel.SaleOrderListModel;
import com.parsroyal.solutiontablet.data.model.GoodsDtoList;
import com.parsroyal.solutiontablet.data.model.SaleOrderDto;
import com.parsroyal.solutiontablet.data.model.SaleOrderItemDto;
import com.parsroyal.solutiontablet.data.searchobject.SaleOrderSO;
import com.parsroyal.solutiontablet.exception.SaleOrderItemCountExceedExistingException;
import com.parsroyal.solutiontablet.service.order.SaleOrderService;
import com.parsroyal.solutiontablet.util.DateUtil;
import com.parsroyal.solutiontablet.util.Empty;

import java.util.List;

/**
 * Created by Mahyar on 8/25/2015.
 */
public class SaleOrderServiceImpl implements SaleOrderService
{

    private Context context;
    private SaleOrderDao saleOrderDao;
    private SaleOrderItemDao saleOrderItemDao;
    private GoodsDao goodsDao;
    private CustomerDao customerDao;

    public SaleOrderServiceImpl(Context context)
    {
        this.context = context;
        this.saleOrderDao = new SaleOrderDaoImpl(context);
        this.saleOrderItemDao = new SaleOrderItemDaoImpl(context);
        this.goodsDao = new GoodsDaoImpl(context);
        this.customerDao = new CustomerDaoImpl(context);
    }

    @Override
    public List<SaleOrderListModel> findOrders(SaleOrderSO saleOrderSO)
    {
        return saleOrderDao.searchForOrders(saleOrderSO);
    }

    @Override
    public SaleOrderDto findOrderDtoById(Long orderId)
    {
        SaleOrderDto orderDto = saleOrderDao.getOrderDtoById(orderId);
        List<SaleOrderItemDto> allOrderItemsDtoByOrderId = getOrderItemDtoList(orderId);
        orderDto.setOrderItems(allOrderItemsDtoByOrderId);
        Customer customer = customerDao.retrieveByBackendId(orderDto.getCustomerBackendId());
        orderDto.setCustomer(customer);
        return orderDto;
    }

    public List<SaleOrderItemDto> getOrderItemDtoList(Long orderId)
    {
        List<SaleOrderItemDto> allOrderItemsDtoByOrderId = saleOrderItemDao
                .getAllOrderItemsDtoByOrderId(orderId);

        for (SaleOrderItemDto saleOrderItemDto : allOrderItemsDtoByOrderId)
        {
            Goods goods = goodsDao.retrieveByBackendId(saleOrderItemDto.getGoodsBackendId());
            saleOrderItemDto.setGoods(goods);
        }
        return allOrderItemsDtoByOrderId;
    }

    public List<SaleOrderItemDto> getLocalOrderItemDtoList(Long orderId, GoodsDtoList goodsDtoList)
    {
        List<SaleOrderItemDto> allOrderItemsDtoByOrderId = saleOrderItemDao
                .getAllOrderItemsDtoByOrderId(orderId);

        List<Goods> goodsList = goodsDtoList.getGoodsDtoList();
        for (SaleOrderItemDto saleOrderItemDto : allOrderItemsDtoByOrderId)
        {
            for (Goods goods : goodsList)
            {
                if (goods.getBackendId().equals(saleOrderItemDto.getGoodsBackendId())
                        && goods.getInvoiceBackendId().equals(saleOrderItemDto.getInvoiceBackendId())
                        )
                {
                    saleOrderItemDto.setGoods(goods);
                    break;
                }
            }
        }
        return allOrderItemsDtoByOrderId;
    }

    @Override
    public void deleteOrderItem(Long itemId, boolean isRejected)
    {
        if (!isRejected)
        {
            SaleOrderItem orderItem = saleOrderItemDao.retrieve(itemId);
            Goods goods = goodsDao.retrieveByBackendId(orderItem.getGoodsBackendId());

            goods.setExisting(goods.getExisting() + orderItem.getGoodsCount());
            goodsDao.update(goods);
        }
        saleOrderItemDao.delete(itemId);

    }

    @Override
    public void changeOrderStatus(Long orderId, Long statusId)
    {
        SaleOrder order = saleOrderDao.retrieve(orderId);
        order.setStatus(statusId);
        order.setUpdateDateTime(DateUtil.getCurrentGregorianFullWithTimeDate());
        saleOrderDao.update(order);
    }

    @Override
    public List<SaleOrderDto> findOrderDtoByStatus(Long statusId)
    {
        List<SaleOrderDto> saleOrderDtoList = saleOrderDao.findOrderDtoByStatusId(statusId);
        for (SaleOrderDto saleOrderDto : saleOrderDtoList)
        {
            saleOrderDto.setOrderItems(getOrderItemDtoList(saleOrderDto.getId()));
            Customer customer = customerDao.retrieveByBackendId(saleOrderDto.getCustomerBackendId());
            saleOrderDto.setCustomer(customer);
        }
        return saleOrderDtoList;
    }

    @Override
    public SaleOrderDto findOrderDtoByCustomerBackendIdAndStatus(Long backendId, Long statusId)
    {
        SaleOrderDto orderDto = saleOrderDao
                .getOrderDtoByCustomerBackendIdAndStatus(backendId, statusId);
        if (Empty.isEmpty(orderDto))
        {
            return null;
        }
        List<SaleOrderItemDto> allOrderItemsDtoByOrderId = getOrderItemDtoList(orderDto.getId());
        orderDto.setOrderItems(allOrderItemsDtoByOrderId);
        Customer customer = customerDao.retrieveByBackendId(orderDto.getCustomerBackendId());
        orderDto.setCustomer(customer);
        return orderDto;
    }

    @Override
    public Long saveOrder(SaleOrderDto orderDto)
    {

        SaleOrder order = createOrderFromDto(orderDto);

        if (Empty.isEmpty(order.getId()))
        {
            Long newId = saleOrderDao.create(order);
            order.setId(newId);
        } else
        {
            saleOrderDao.update(order);
        }

        return order.getId();
    }

    @Override
    public SaleOrderItem findOrderItemByOrderIdAndGoodsBackendId(Long id, long goodsBackendId,
                                                                 long invoiceBackendId)
    {
        return saleOrderItemDao.getOrderItemByOrderIdAndGoodsId(id, goodsBackendId, invoiceBackendId);
    }

    @Override
    public Long saveOrderItem(SaleOrderItem saleOrderItem)
    {
        if (Empty.isEmpty(saleOrderItem.getId()))
        {
            return saleOrderItemDao.create(saleOrderItem);
        } else
        {
            saleOrderItemDao.update(saleOrderItem);
            return saleOrderItem.getId();
        }
    }

    @Override
    public Long updateOrderAmount(Long orderId)
    {
        List<SaleOrderItemDto> items = getOrderItemDtoList(orderId);
        Long orderAmount = 0L;
        for (SaleOrderItemDto item : items)
        {
            orderAmount += item.getAmount();
        }
        SaleOrder order = saleOrderDao.retrieve(orderId);
        order.setAmount(orderAmount);
        order.setUpdateDateTime(DateUtil.getCurrentGregorianFullWithTimeDate());
        saleOrderDao.update(order);

        return orderAmount;
    }

    @Override
    public void deleteForAllCustomerOrdersByStatus(Long customerBackendId, Long statusId)
    {
        SaleOrderSO saleOrderSO = new SaleOrderSO();
        saleOrderSO.setStatusId(statusId);
        saleOrderSO.setCustomerBackendId(customerBackendId);
        List<SaleOrderListModel> orders = findOrders(saleOrderSO);
        for (SaleOrderListModel order : orders)
        {
            List<SaleOrderItemDto> allOrderItemsDtoByOrderId = saleOrderItemDao
                    .getAllOrderItemsDtoByOrderId(order.getId());
            for (SaleOrderItemDto saleOrderItemDto : allOrderItemsDtoByOrderId)
            {
                if (Empty.isNotEmpty(saleOrderItemDto.getGoodsCount()))
                {
                    Goods goods = goodsDao.retrieveByBackendId(saleOrderItemDto.getGoodsBackendId());
                    goods.setExisting(goods.getExisting() + saleOrderItemDto.getGoodsCount());
                    goodsDao.update(goods);
                }
                saleOrderItemDao.delete(saleOrderItemDto.getGoodsBackendId());
            }
            saleOrderDao.delete(order.getId());
        }
    }

    private SaleOrder createOrderFromDto(SaleOrderDto orderDto)
    {
        SaleOrder order = new SaleOrder();
        order.setId(orderDto.getId());
        order.setNumber(orderDto.getNumber());
        order.setDate(orderDto.getDate());
        order.setAmount(orderDto.getAmount());
        order.setPaymentTypeBackendId(orderDto.getPaymentTypeBackendId());
        order.setSalesmanId(orderDto.getSalesmanId());
        order.setCustomerBackendId(orderDto.getCustomerBackendId());
        order.setDescription(orderDto.getDescription());
        order.setStatus(orderDto.getStatus());
        order.setBackendId(orderDto.getBackendId());
        order.setInvoiceBackendId(orderDto.getInvoiceBackendId());
        return order;
    }

    @Override
    public void updateOrderItemCount(Long itemId, Double count, Long selectedUnit, Long orderStatus,
                                     Goods localGood)
    {
        SaleOrderItem orderItem = saleOrderItemDao.retrieve(itemId);
        Goods goods = null;
        if (orderStatus.equals(SaleOrderStatus.REJECTED_DRAFT.getId()))
        {
            goods = localGood;
        } else
        {
            goods = goodsDao.retrieveByBackendId(orderItem.getGoodsBackendId());
        }

        count *= 1000D;
        Long itemAmount = count.longValue() * goods.getPrice();
        itemAmount /= 1000L;

        if (count.longValue() > goods.getExisting() + orderItem.getGoodsCount())
        {
            throw new SaleOrderItemCountExceedExistingException();
        }
        goods.setExisting(goods.getExisting() + orderItem.getGoodsCount());

        orderItem.setGoodsCount(count.longValue());
        orderItem.setAmount(itemAmount);
        orderItem.setSelectedUnit(selectedUnit);
        saleOrderItemDao.update(orderItem);

        goods.setExisting(goods.getExisting() - count.longValue());
        if (!orderStatus.equals(SaleOrderStatus.REJECTED_DRAFT.getId()))
        {
            goodsDao.update(goods);
        }
    }
}
