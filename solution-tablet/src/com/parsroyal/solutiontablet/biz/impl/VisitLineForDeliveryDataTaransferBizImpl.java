package com.parsroyal.solutiontablet.biz.impl;

import android.content.Context;

import com.parsroyal.solutiontablet.R;
import com.parsroyal.solutiontablet.biz.AbstractDataTransferBizImpl;
import com.parsroyal.solutiontablet.biz.KeyValueBiz;
import com.parsroyal.solutiontablet.data.dao.CustomerDao;
import com.parsroyal.solutiontablet.data.dao.VisitLineDao;
import com.parsroyal.solutiontablet.data.dao.impl.CustomerDaoImpl;
import com.parsroyal.solutiontablet.data.dao.impl.VisitLineDaoImpl;
import com.parsroyal.solutiontablet.data.entity.Customer;
import com.parsroyal.solutiontablet.data.entity.KeyValue;
import com.parsroyal.solutiontablet.data.entity.VisitLine;
import com.parsroyal.solutiontablet.data.model.VisitLineDto;
import com.parsroyal.solutiontablet.data.model.VisitLineDtoList;
import com.parsroyal.solutiontablet.ui.observer.ResultObserver;
import com.parsroyal.solutiontablet.util.CharacterFixUtil;
import com.parsroyal.solutiontablet.util.DateUtil;
import com.parsroyal.solutiontablet.util.Empty;
import com.parsroyal.solutiontablet.util.constants.ApplicationKeys;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;

import java.util.Date;
import java.util.List;

/**
 * Created by Mahyar on 7/5/2015.
 */
public class VisitLineForDeliveryDataTaransferBizImpl extends
        AbstractDataTransferBizImpl<VisitLineDtoList>
{

    private Context context;
    private ResultObserver resultObserver;
    private VisitLineDao visitLineDao;
    private CustomerDao customerDao;
    private KeyValueBiz keyValueBiz;

    public VisitLineForDeliveryDataTaransferBizImpl(Context context, ResultObserver resultObserver)
    {
        super(context);
        this.context = context;
        this.resultObserver = resultObserver;
        this.visitLineDao = new VisitLineDaoImpl(context);
        this.customerDao = new CustomerDaoImpl(context);
        this.keyValueBiz = new KeyValueBizImpl(context);
    }

    @Override
    public void receiveData(VisitLineDtoList data)
    {
        if (Empty.isNotEmpty(data))
        {
            for (VisitLineDto visitLineDto : data.getVisitLineDtoList())
            {
                visitLineDto.setTitle(CharacterFixUtil.fixString(visitLineDto.getTitle()));
                VisitLine visitLine = createVisitLineEntity(visitLineDto);
                VisitLine oldVisitLine = visitLineDao.getVisitLineByBackendId(visitLine.getBackendId());
                if (Empty.isEmpty(oldVisitLine))
                {
                    visitLineDao.create(visitLine);
                    for (Customer customer : visitLineDto.getCustomerList())
                    {
                        customer.setFullName(CharacterFixUtil.fixString(customer.getFullName()));
                        customer.setShopName(CharacterFixUtil.fixString(customer.getShopName()));
                        customer.setAddress(CharacterFixUtil.fixString(customer.getAddress()));
                        customer.setApproved(true);
                        customer.setCreateDateTime(DateUtil
                                .convertDate(new Date(), DateUtil.FULL_FORMATTER_GREGORIAN_WITH_TIME, "EN"));
                        customer.setUpdateDateTime(DateUtil
                                .convertDate(new Date(), DateUtil.FULL_FORMATTER_GREGORIAN_WITH_TIME, "EN"));
                        customerDao.create(customer);
                    }
                } else
                {
                    List<Customer> customers = customerDao
                            .getCustomersVisitLineBackendId(visitLine.getBackendId());
                    for (Customer customer : visitLineDto.getCustomerList())
                    {
                        if (!customers.contains(customer))
                        {
                            customer.setCreateDateTime(DateUtil
                                    .convertDate(new Date(), DateUtil.FULL_FORMATTER_GREGORIAN_WITH_TIME, "EN"));
                            customer.setUpdateDateTime(DateUtil
                                    .convertDate(new Date(), DateUtil.FULL_FORMATTER_GREGORIAN_WITH_TIME, "EN"));
                            customer.setApproved(true);
                            customerDao.create(customer);
                        }
                    }
                }
            }
        }
        getObserver()
                .publishResult(context.getString(R.string.visitlines_data_transferred_successfully));
    }

    private VisitLine createVisitLineEntity(VisitLineDto visitLineDto)
    {
        VisitLine visitLine = new VisitLine();
        visitLine.setBackendId(visitLineDto.getBackendId());
        visitLine.setCode(visitLineDto.getCode());
        visitLine.setTitle(visitLineDto.getTitle());
        return visitLine;
    }

    @Override
    public void beforeTransfer()
    {
        getObserver().publishResult(context.getString(R.string.message_transferring_visit_lines_data));
    }

    @Override
    public ResultObserver getObserver()
    {
        return resultObserver;
    }

    @Override
    public String getMethod()
    {
        return "customer/deliveryVisitLines";
    }

    @Override
    public Class getType()
    {
        return VisitLineDtoList.class;
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
