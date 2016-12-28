package com.conta.comer.biz.impl;

import android.content.Context;

import com.conta.comer.R;
import com.conta.comer.biz.AbstractDataTransferBizImpl;
import com.conta.comer.constants.SendStatus;
import com.conta.comer.data.dao.impl.KeyValueDaoImpl;
import com.conta.comer.data.entity.Payment;
import com.conta.comer.service.PaymentService;
import com.conta.comer.service.impl.PaymentServiceImpl;
import com.conta.comer.ui.observer.ResultObserver;
import com.conta.comer.util.DateUtil;
import com.conta.comer.util.Empty;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;

import java.util.Date;
import java.util.List;

/**
 * Created by Arash on 2016-08-21
 */
public class PaymentsDataTransferBizImpl extends AbstractDataTransferBizImpl<String>
{

    public static final String TAG = PaymentsDataTransferBizImpl.class.getSimpleName();

    private Context context;
    private PaymentService paymentService;
    private ResultObserver observer;
    private List<Payment> payments;

    public PaymentsDataTransferBizImpl(Context context, ResultObserver resultObserver)
    {
        super(context);
        this.context = context;
        this.paymentService = new PaymentServiceImpl(context);
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
                int success = 0;
                int failure = 0;
                for (int i = 0; i < rows.length; i++)
                {
                    String row = rows[i];
                    String[] columns = row.split("[&]");
                    long paymentId = Long.parseLong(columns[0]);
                    long paymentBackendId = Long.parseLong(columns[1]);
                    long updated = Long.parseLong(columns[2]);

                    Payment payment = paymentService.getPaymentById(paymentId);
                    if (Empty.isNotEmpty(payment) && updated == 1)
                    {
                        payment.setStatus(SendStatus.SENT.getId());
                        payment.setBackendId(paymentBackendId);
                        payment.setUpdateDateTime(DateUtil.convertDate(new Date(), DateUtil.FULL_FORMATTER_GREGORIAN_WITH_TIME, "EN"));
                        paymentService.updatePayment(payment);
                        success++;
                    } else
                    {
                        failure++;
                    }
                }
                getObserver().publishResult(String.format(context.getString(R.string.payments_data_transferred_successfully),
                        success, failure));
            } catch (Exception ex)
            {
                getObserver().publishResult(context.getString(R.string.error_payments_transfer));
            }
        } else
        {
            getObserver().publishResult(context.getString(R.string.message_got_no_response));
        }
    }

    @Override
    public void beforeTransfer()
    {
        getObserver().publishResult(context.getString(R.string.sending_payments_data));
    }

    @Override
    public ResultObserver getObserver()
    {
        return observer;
    }

    @Override
    public String getMethod()
    {
        return "payment/create";
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
        HttpEntity<List<Payment>> requestEntity = new HttpEntity<>(payments, headers);
        return requestEntity;
    }

    public void setPayments(List<Payment> payments)
    {
        this.payments = payments;
    }
}
