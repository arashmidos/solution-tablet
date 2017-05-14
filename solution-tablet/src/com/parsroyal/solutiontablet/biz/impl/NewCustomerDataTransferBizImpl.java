package com.parsroyal.solutiontablet.biz.impl;

import android.content.Context;
import android.util.Log;

import com.parsroyal.solutiontablet.R;
import com.parsroyal.solutiontablet.biz.AbstractDataTransferBizImpl;
import com.parsroyal.solutiontablet.biz.KeyValueBiz;
import com.parsroyal.solutiontablet.data.dao.CustomerDao;
import com.parsroyal.solutiontablet.data.dao.QAnswerDao;
import com.parsroyal.solutiontablet.data.dao.VisitInformationDao;
import com.parsroyal.solutiontablet.data.dao.impl.CustomerDaoImpl;
import com.parsroyal.solutiontablet.data.dao.impl.QAnswerDaoImpl;
import com.parsroyal.solutiontablet.data.dao.impl.VisitInformationDaoImpl;
import com.parsroyal.solutiontablet.data.entity.Customer;
import com.parsroyal.solutiontablet.data.entity.VisitInformation;
import com.parsroyal.solutiontablet.service.CustomerService;
import com.parsroyal.solutiontablet.service.impl.CustomerServiceImpl;
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
import java.util.Date;
import java.util.List;

/**
 * Created by Mahyar on 6/23/2015.
 */
public class NewCustomerDataTransferBizImpl extends AbstractDataTransferBizImpl<String>
{

    public static final String TAG = NewCustomerDataTransferBizImpl.class.getSimpleName();

    private Context context;
    private CustomerDao customerDao;
    private CustomerService customerService;
    private VisitInformationDao visitInformationDao;
    private QAnswerDao qAnswerDao;
    private ResultObserver observer;
    private KeyValueBiz keyValueBiz;

    public NewCustomerDataTransferBizImpl(Context context, ResultObserver resultObserver)
    {
        super(context);
        this.context = context;
        this.customerDao = new CustomerDaoImpl(context);
        this.visitInformationDao = new VisitInformationDaoImpl(context);
        this.qAnswerDao = new QAnswerDaoImpl(context);
        this.customerService = new CustomerServiceImpl(context);
        this.observer = resultObserver;
        this.keyValueBiz = new KeyValueBizImpl(context);
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
                    Long customerId = Long.parseLong(columns[0]);
                    Long backendId = Long.parseLong(columns[1]);
                    String code = columns[2];

                    Customer customer = customerDao.retrieve(customerId);
                    if (Empty.isNotEmpty(customer))
                    {
                        customer.setBackendId(backendId);
                        customer.setCode(code);
                        customer.setUpdateDateTime(DateUtil
                                .convertDate(new Date(), DateUtil.FULL_FORMATTER_GREGORIAN_WITH_TIME, "EN"));
                        customerDao.update(customer);

                        //Update Visits
                        VisitInformation visitList = visitInformationDao.retrieveForNewCustomer(customerId);
//                        for (VisitInformation visit : visitList)
//                        {
                        if (visitList != null)
                        {
                            visitList.setCustomerBackendId(backendId);
                            visitInformationDao.update(visitList);
                        }

                        //update answers
                        qAnswerDao.updateCustomerBackendIdForAnswers(customerId, backendId);
                    }
                }
                getObserver()
                        .publishResult(context.getString(R.string.new_customers_data_transferred_successfully));
            } catch (Exception ex)
            {
                Log.e(TAG, ex.getMessage(), ex);
                getObserver()
                        .publishResult(context.getString(R.string.message_exception_in_sending_new_customers));
            }
        } else
        {
            getObserver().publishResult(context.getString(R.string.message_got_no_response));
        }
    }

    @Override
    public void beforeTransfer()
    {
        getObserver().publishResult(context.getString(R.string.sending_new_customers_data));
    }

    @Override
    public ResultObserver getObserver()
    {
        return observer;
    }

    @Override
    public String getMethod()
    {
        return "customer/new";
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
        MediaType contentType = new MediaType("TEXT", "PLAIN", Charset.forName("UTF-8"));
        return contentType;
    }

    @Override
    protected HttpEntity getHttpEntity(HttpHeaders headers)
    {
        headers
                .add("branchCode", keyValueBiz.findByKey(ApplicationKeys.SETTING_BRANCH_CODE).getValue());

        List<Customer> allNewCustomersForSend = customerService.getAllNewCustomersForSend();
        String customersString = getCustomersString(allNewCustomersForSend);
        headers.setAccept(Arrays.asList(MediaType.TEXT_PLAIN));
        HttpEntity<String> httpEntity = new HttpEntity<String>(customersString, headers);
        return httpEntity;
    }

    private String getCustomersString(List<Customer> allNewCustomersForSend)
    {
        StringBuilder sb = new StringBuilder();
        boolean firstLine = true;
        for (Customer customer : allNewCustomersForSend)
        {
            String customerString = customer.getString();
            customerString = customerString.trim().replaceAll("[\n]", "");
            customerString = customerString.trim().replace("\n", "");
            if (firstLine)
            {
                sb.append(customerString);
                firstLine = false;
                continue;
            }
            sb.append("\n");
            sb.append(customerString);
        }

        return sb.toString();
    }
}
