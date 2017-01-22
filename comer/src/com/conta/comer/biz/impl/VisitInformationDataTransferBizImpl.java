package com.conta.comer.biz.impl;

import android.content.Context;
import android.util.Log;

import com.conta.comer.R;
import com.conta.comer.biz.AbstractDataTransferBizImpl;
import com.conta.comer.data.dao.VisitInformationDao;
import com.conta.comer.data.dao.impl.VisitInformationDaoImpl;
import com.conta.comer.data.entity.VisitInformation;
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
import java.util.List;

/**
 * Created by Mahyar on 8/2/2015.
 */
public class VisitInformationDataTransferBizImpl extends AbstractDataTransferBizImpl<String>
{

    private VisitInformationDao visitInformationDao;
    private CustomerService customerService;
    private ResultObserver observer;

    public VisitInformationDataTransferBizImpl(Context context, ResultObserver resultObserver)
    {
        super(context);
        this.visitInformationDao = new VisitInformationDaoImpl(context);
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
                String[] rows = response.split("[\n]");
                for (int i = 0; i < rows.length; i++)
                {
                    String row = rows[i];
                    String[] columns = row.split("[&]");
                    Long visitId = Long.parseLong(columns[0]);
                    Long backendId = Long.parseLong(columns[1]);

                    VisitInformation visitInformation = visitInformationDao.retrieve(visitId);

                    if (Empty.isNotEmpty(visitInformation))
                    {
                        visitInformation.setVisitBackendId(backendId);
                        visitInformation.setUpdateDateTime(DateUtil.getCurrentGregorianFullWithTimeDate());
                        visitInformationDao.update(visitInformation);
                    }
                }

                observer.publishResult(context.getString(R.string.visit_information_data_transferred_successfully));
            } catch (Exception ex)
            {
                Log.e(TAG, ex.getMessage(), ex);
                observer.publishResult(context.getString(R.string.message_exception_in_sending_visit_information));
            }
        }

    }

    @Override
    public void beforeTransfer()
    {
        observer.publishResult(context.getString(R.string.sending_visit_information_data));
    }

    @Override
    public ResultObserver getObserver()
    {
        return observer;
    }

    @Override
    public String getMethod()
    {
        return "visit/detail";
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
        List<VisitInformation> allVisitInformationForSend = customerService.getAllVisitInformationForSend();
        String visitsInformationString = getVisitInformationString(allVisitInformationForSend);
        headers.setAccept(Arrays.asList(MediaType.TEXT_PLAIN));
        HttpEntity<String> httpEntity = new HttpEntity<String>(visitsInformationString, headers);
        return httpEntity;
    }

    private String getVisitInformationString(List<VisitInformation> allVisitInformationForSend)
    {
        StringBuilder sb = new StringBuilder();
        boolean firstLine = true;
        for (VisitInformation visitInformation : allVisitInformationForSend)
        {
            String visitInformationString = visitInformation.getString();
            visitInformationString = visitInformationString.trim().replaceAll("[\n]", "");
            visitInformationString = visitInformationString.trim().replace("\n", "");
            if (firstLine)
            {
                sb.append(visitInformationString);
                firstLine = false;
                continue;
            }
            sb.append("\n");
            sb.append(visitInformationString);
        }

        return sb.toString();
    }
}
