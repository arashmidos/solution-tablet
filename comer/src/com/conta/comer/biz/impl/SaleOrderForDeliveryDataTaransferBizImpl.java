package com.conta.comer.biz.impl;

import android.content.Context;

import com.conta.comer.R;
import com.conta.comer.biz.AbstractDataTransferBizImpl;
import com.conta.comer.biz.KeyValueBiz;
import com.conta.comer.constants.SaleOrderStatus;
import com.conta.comer.data.dao.SaleOrderDao;
import com.conta.comer.data.dao.SaleOrderItemDao;
import com.conta.comer.data.dao.impl.SaleOrderDaoImpl;
import com.conta.comer.data.dao.impl.SaleOrderItemDaoImpl;
import com.conta.comer.data.entity.KeyValue;
import com.conta.comer.data.entity.SaleOrder;
import com.conta.comer.data.entity.SaleOrderItem;
import com.conta.comer.data.model.SaleOrderList;
import com.conta.comer.ui.observer.ResultObserver;
import com.conta.comer.util.DateUtil;
import com.conta.comer.util.Empty;
import com.conta.comer.util.constants.ApplicationKeys;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;

import java.util.List;

/**
 * Created by Mahyar on 8/21/2015.
 */
public class SaleOrderForDeliveryDataTaransferBizImpl extends AbstractDataTransferBizImpl<SaleOrderList>
{

    private ResultObserver resultObserver;
    private SaleOrderDao saleOrderDao;
    private SaleOrderItemDao saleOrderItemDao;
    private KeyValueBiz keyValueBiz;

    public SaleOrderForDeliveryDataTaransferBizImpl(Context context, ResultObserver resultObserver)
    {
        super(context);
        this.resultObserver = resultObserver;
        this.saleOrderDao = new SaleOrderDaoImpl(context);
        this.saleOrderItemDao = new SaleOrderItemDaoImpl(context);
        this.keyValueBiz = new KeyValueBizImpl(context);
    }

    @Override
    public void receiveData(SaleOrderList data)
    {
        try
        {

            if (Empty.isNotEmpty(data))
            {

                List<SaleOrder> deliverableOrders = saleOrderDao.retrieveSaleOrderByStatus(SaleOrderStatus.DELIVERABLE.getId());
                for (SaleOrder deliverableOrder : deliverableOrders)
                {
                    saleOrderItemDao.deleteAllItemsBySaleOrderId(deliverableOrder.getId());
                    saleOrderDao.delete(deliverableOrder.getId());
                }

                for (SaleOrder saleOrder : data.getOrderList())
                {
                    saleOrder.setCreateDateTime(DateUtil.getCurrentGregorianFullWithTimeDate());
                    saleOrder.setUpdateDateTime(DateUtil.getCurrentGregorianFullWithTimeDate());
                    saleOrder.setStatus(SaleOrderStatus.DELIVERABLE.getId());
                    Long saleOrderId = saleOrderDao.create(saleOrder);

                    for (SaleOrderItem orderItem : saleOrder.getOrderItems())
                    {
                        orderItem.setSaleOrderId(saleOrderId);
                        orderItem.setCreateDateTime(DateUtil.getCurrentGregorianFullWithTimeDate());
                        orderItem.setUpdateDateTime(DateUtil.getCurrentGregorianFullWithTimeDate());
                        saleOrderItemDao.create(orderItem);
                    }
                }

                resultObserver.publishResult(context.getString(R.string.message_deliverable_orders_transferred_successfully));

            } else
            {
                resultObserver.publishResult(context.getString(R.string.message_found_no_deliverable_orders));
            }

        } catch (Exception ex)
        {
            resultObserver.publishResult(context.getString(R.string.message_exception_in_transfering_sale_order_for_delivery));
        }
    }

    @Override
    public void beforeTransfer()
    {
        resultObserver.publishResult(context.getString(R.string.message_transferring_deliverable_orders));
    }

    @Override
    public ResultObserver getObserver()
    {
        return resultObserver;
    }

    @Override
    public String getMethod()
    {
        return "order/createDeliverable";
    }

    @Override
    public Class getType()
    {
        return SaleOrderList.class;
    }

    @Override
    public HttpMethod getHttpMethod()
    {
        return HttpMethod.POST;
    }

    @Override
    protected MediaType getContentType()
    {
        return MediaType.TEXT_PLAIN;
    }

    @Override
    protected HttpEntity getHttpEntity(HttpHeaders headers)
    {
        KeyValue userCodeKey = keyValueBiz.findByKey(ApplicationKeys.SETTING_USER_CODE);
        return new HttpEntity<>(userCodeKey.getValue(), headers);
    }
}
