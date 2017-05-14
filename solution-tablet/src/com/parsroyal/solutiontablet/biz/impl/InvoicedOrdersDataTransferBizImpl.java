package com.parsroyal.solutiontablet.biz.impl;

import android.content.Context;
import android.util.Log;

import com.parsroyal.solutiontablet.R;
import com.parsroyal.solutiontablet.biz.AbstractDataTransferBizImpl;
import com.parsroyal.solutiontablet.biz.KeyValueBiz;
import com.parsroyal.solutiontablet.constants.SaleOrderStatus;
import com.parsroyal.solutiontablet.constants.VisitInformationDetailType;
import com.parsroyal.solutiontablet.data.dao.SaleOrderDao;
import com.parsroyal.solutiontablet.data.dao.SaleOrderItemDao;
import com.parsroyal.solutiontablet.data.dao.impl.SaleOrderDaoImpl;
import com.parsroyal.solutiontablet.data.dao.impl.SaleOrderItemDaoImpl;
import com.parsroyal.solutiontablet.data.entity.SaleOrder;
import com.parsroyal.solutiontablet.data.model.SaleOrderDto;
import com.parsroyal.solutiontablet.service.VisitService;
import com.parsroyal.solutiontablet.service.impl.VisitServiceImpl;
import com.parsroyal.solutiontablet.ui.observer.ResultObserver;
import com.parsroyal.solutiontablet.util.DateUtil;
import com.parsroyal.solutiontablet.util.Empty;
import com.parsroyal.solutiontablet.util.constants.ApplicationKeys;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;

import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Mahyar on 8/29/2015.
 */
public class InvoicedOrdersDataTransferBizImpl extends AbstractDataTransferBizImpl<String>
{

    protected ResultObserver resultObserver;
    protected SaleOrderDao saleOrderDao;
    protected SaleOrderItemDao saleOrderItemDao;
    protected List<SaleOrderDto> orders;
    private VisitService visitService;
    private KeyValueBiz keyValueBiz;

    public InvoicedOrdersDataTransferBizImpl(Context context, ResultObserver resultObserver)
    {
        super(context);
        this.resultObserver = resultObserver;
        this.saleOrderDao = new SaleOrderDaoImpl(context);
        this.saleOrderItemDao = new SaleOrderItemDaoImpl(context);
        this.keyValueBiz = new KeyValueBizImpl(context);
        visitService = new VisitServiceImpl(context);
    }

    public List<SaleOrderDto> getOrders()
    {
        return orders;
    }

    public void setOrders(List<SaleOrderDto> orders)
    {
        this.orders = orders;
    }

    @Override
    public void receiveData(String response)
    {
        if (Empty.isNotEmpty(response))
        {
            try
            {
                String[] rows = response.split("[\n]");
                for (int i = 0; i < rows.length; i++)
                {
                    String row = rows[i];
                    String[] columns = row.split("[&]");
                    Long orderId = Long.parseLong(columns[0]);
                    Long invoiceBackendId = Long.parseLong(columns[1]);

                    SaleOrder saleOrder = saleOrderDao.retrieve(orderId);

                    if (Empty.isNotEmpty(saleOrder))
                    {
                        updateOrderStatus(invoiceBackendId, saleOrder);
                    }
                }

                resultObserver.publishResult(getSuccessfulMessage());
            } catch (Exception ex)
            {
                Log.e(TAG, ex.getMessage(), ex);
                resultObserver.publishResult(getExceptionMessage());
            }
        }
    }

    protected String getExceptionMessage()
    {
        return context.getString(R.string.message_exception_in_sending_invoices);
    }

    protected String getSuccessfulMessage()
    {
        return context.getString(R.string.invoices_data_transferred_successfully);
    }

    protected void updateOrderStatus(Long invoiceBackendId, SaleOrder saleOrder)
    {
        saleOrder.setInvoiceBackendId(invoiceBackendId);
        saleOrder.setStatus(SaleOrderStatus.SENT_INVOICE.getId());
        saleOrder.setUpdateDateTime(DateUtil.getCurrentGregorianFullWithTimeDate());
        saleOrderDao.update(saleOrder);
        visitService.updateVisitDetailId(getVisitDetailType(), saleOrder.getId(), invoiceBackendId);
    }

    @Override
    public void beforeTransfer()
    {
        resultObserver.publishResult(context.getString(R.string.message_transferring_invoices));
    }

    @Override
    public ResultObserver getObserver()
    {
        return resultObserver;
    }

    @Override
    public String getMethod()
    {
        return "invoice/create";
    }

    @Override
    public Class getType()
    {
        return String.class;
    }

    @Override
    public HttpMethod getHttpMethod()
    {
        return HttpMethod.POST;
    }

    @Override
    protected MediaType getContentType()
    {
        return new MediaType("TEXT", "PLAIN", Charset.forName("UTF-8"));
    }

    @Override
    protected HttpEntity getHttpEntity(HttpHeaders headers)
    {
        headers
                .add("branchCode", keyValueBiz.findByKey(ApplicationKeys.SETTING_BRANCH_CODE).getValue());
        headers.add("stockCode", keyValueBiz.findByKey(ApplicationKeys.SETTING_STOCK_CODE).getValue());

        String invoicesStr = getInvoicesString(orders);
        Log.d(TAG, "INVOICE SENDING:" + invoicesStr);
        headers.setAccept(Arrays.asList(MediaType.TEXT_PLAIN));
        return new HttpEntity<>(invoicesStr, headers);
    }

    protected String getInvoicesString(List<SaleOrderDto> orders)
    {
        StringBuilder sb = new StringBuilder();
        boolean firstLine = true;
        for (SaleOrderDto order : orders)
        {
            String orderStr = SaleOrder.createString(order);
            orderStr = orderStr.trim().replaceAll("[\n]", "");
            orderStr = orderStr.trim().replace("\n", "");
            if (firstLine)
            {
                sb.append(orderStr);
                firstLine = false;
                continue;
            }
            sb.append("\n");
            sb.append(orderStr);
        }
        Log.d(TAG, "ORDERS:" + sb.toString());
        return sb.toString();
    }

    protected VisitInformationDetailType getVisitDetailType()
    {
        String saleType = keyValueBiz.findByKey(ApplicationKeys.SETTING_SALE_TYPE).getValue();
        if (saleType.equals(ApplicationKeys.SALE_DISTRIBUTER))
        {
            return VisitInformationDetailType.DELIVER_ORDER;
        }
        return VisitInformationDetailType.CREATE_INVOICE;
    }
}
