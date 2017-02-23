package com.parsroyal.solutiontablet.biz.impl;

import android.content.Context;

import com.parsroyal.solutiontablet.R;
import com.parsroyal.solutiontablet.biz.AbstractDataTransferBizImpl;
import com.parsroyal.solutiontablet.constants.SendStatus;
import com.parsroyal.solutiontablet.data.dao.impl.KeyValueDaoImpl;
import com.parsroyal.solutiontablet.data.entity.Payment;
import com.parsroyal.solutiontablet.service.PaymentService;
import com.parsroyal.solutiontablet.service.impl.PaymentServiceImpl;
import com.parsroyal.solutiontablet.ui.observer.ResultObserver;
import com.parsroyal.solutiontablet.util.DateUtil;
import com.parsroyal.solutiontablet.util.Empty;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;

import java.util.Date;
import java.util.List;
import java.util.Locale;

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
                getObserver().publishResult(String.format(Locale.US,context.getString(R.string.payments_data_transferred_successfully),
                        String.valueOf(success), String.valueOf(failure)));
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
