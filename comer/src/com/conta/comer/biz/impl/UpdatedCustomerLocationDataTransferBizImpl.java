package com.conta.comer.biz.impl;

import android.content.Context;

import com.conta.comer.R;
import com.conta.comer.biz.AbstractDataTransferBizImpl;
import com.conta.comer.constants.CustomerStatus;
import com.conta.comer.data.dao.CustomerDao;
import com.conta.comer.data.dao.impl.CustomerDaoImpl;
import com.conta.comer.data.dao.impl.KeyValueDaoImpl;
import com.conta.comer.data.entity.Customer;
import com.conta.comer.data.model.CustomerLocationDto;
import com.conta.comer.service.CustomerService;
import com.conta.comer.service.impl.CustomerServiceImpl;
import com.conta.comer.ui.observer.ResultObserver;
import com.conta.comer.util.DateUtil;
import com.conta.comer.util.Empty;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;

import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Created by Arash on 2016-08-10
 */
public class UpdatedCustomerLocationDataTransferBizImpl extends AbstractDataTransferBizImpl<String>
{

    public static final String TAG = UpdatedCustomerLocationDataTransferBizImpl.class.getSimpleName();

    private Context context;
    private CustomerDao customerDao;
    private CustomerService customerService;
    private ResultObserver observer;

    public UpdatedCustomerLocationDataTransferBizImpl(Context context, ResultObserver resultObserver)
    {
        super(context);
        this.context = context;
        this.customerDao = new CustomerDaoImpl(context);
        this.customerService = new CustomerServiceImpl(context);
        this.observer = resultObserver;
        this.keyValueDao = new KeyValueDaoImpl(context);
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
                        customer.setUpdateDateTime(DateUtil.convertDate(new Date(), DateUtil.FULL_FORMATTER_GREGORIAN_WITH_TIME, "EN"));
                        customerDao.update(customer);
                    }
                }
                getObserver().publishResult(context.getString(R.string.updated_customers_data_transferred_successfully));
            } catch (Exception ex)
            {
                getObserver().publishResult(context.getString(R.string.error_updated_customers_locaction_transfer));
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
        List<CustomerLocationDto> customerLocationDtoList = customerService.getAllUpdatedCustomerLocation();
        HttpEntity<List<CustomerLocationDto>> requestEntity = new HttpEntity<>(customerLocationDtoList, headers);
        return requestEntity;
    }
}
