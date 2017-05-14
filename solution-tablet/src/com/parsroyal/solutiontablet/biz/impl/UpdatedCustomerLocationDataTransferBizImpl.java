package com.parsroyal.solutiontablet.biz.impl;

import android.content.Context;

import com.parsroyal.solutiontablet.R;
import com.parsroyal.solutiontablet.biz.AbstractDataTransferBizImpl;
import com.parsroyal.solutiontablet.constants.CustomerStatus;
import com.parsroyal.solutiontablet.data.dao.CustomerDao;
import com.parsroyal.solutiontablet.data.dao.impl.CustomerDaoImpl;
import com.parsroyal.solutiontablet.data.entity.Customer;
import com.parsroyal.solutiontablet.data.model.CustomerLocationDto;
import com.parsroyal.solutiontablet.service.CustomerService;
import com.parsroyal.solutiontablet.service.impl.CustomerServiceImpl;
import com.parsroyal.solutiontablet.ui.observer.ResultObserver;
import com.parsroyal.solutiontablet.util.DateUtil;
import com.parsroyal.solutiontablet.util.Empty;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;

import java.util.Date;
import java.util.List;

/**
 * Created by Arash on 2016-08-10
 */
public class UpdatedCustomerLocationDataTransferBizImpl extends
        AbstractDataTransferBizImpl<String>
{

    public static final String TAG = UpdatedCustomerLocationDataTransferBizImpl.class.getSimpleName();

    private Context context;
    private CustomerDao customerDao;
    private CustomerService customerService;
    private ResultObserver observer;

    public UpdatedCustomerLocationDataTransferBizImpl(Context context,
                                                      ResultObserver resultObserver)
    {
        super(context);
        this.context = context;
        this.customerDao = new CustomerDaoImpl(context);
        this.customerService = new CustomerServiceImpl(context);
        this.observer = resultObserver;
    }

    @Override
    public void receiveData(String response)
    {
        if (Empty.isNotEmpty(response))
        {
            try
            {
                String[] rows = response.split("[$]");
                for (int i = 0; i < rows.length; i++)
                {
                    String row = rows[i];
                    String[] columns = row.split("[&]");
                    long customerBackendId = Long.parseLong(columns[0]);
                    long updated = Long.parseLong(columns[1]);

                    Customer customer = customerDao.retrieveByBackendId(customerBackendId);
                    if (Empty.isNotEmpty(customer) && updated == 1)
                    {
                        customer.setStatus(CustomerStatus.SENT.getId());
                        customer.setUpdateDateTime(DateUtil
                                .convertDate(new Date(), DateUtil.FULL_FORMATTER_GREGORIAN_WITH_TIME, "EN"));
                        customerDao.update(customer);
                    }
                }
                getObserver().publishResult(
                        context.getString(R.string.updated_customers_data_transferred_successfully));
            } catch (Exception ex)
            {
                getObserver()
                        .publishResult(context.getString(R.string.error_updated_customers_locaction_transfer));
            }
        } else
        {
            getObserver().publishResult(context.getString(R.string.message_got_no_response));
        }
    }

    @Override
    public void beforeTransfer()
    {
        getObserver().publishResult(context.getString(R.string.sending_updated_customers_data));
    }

    @Override
    public ResultObserver getObserver()
    {
        return observer;
    }

    @Override
    public String getMethod()
    {
        return "customer/updateLocation";
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
        return MediaType.APPLICATION_JSON;
    }

    @Override
    protected HttpEntity getHttpEntity(HttpHeaders headers)
    {
        List<CustomerLocationDto> customerLocationDtoList = customerService
                .getAllUpdatedCustomerLocation();
        HttpEntity<List<CustomerLocationDto>> requestEntity = new HttpEntity<>(customerLocationDtoList,
                headers);
        return requestEntity;
    }
}
