package com.conta.comer.biz.impl;

import android.content.Context;
import android.util.Log;

import com.conta.comer.R;
import com.conta.comer.biz.AbstractDataTransferBizImpl;
import com.conta.comer.constants.SendStatus;
import com.conta.comer.data.dao.impl.KeyValueDaoImpl;
import com.conta.comer.data.entity.Position;
import com.conta.comer.service.PositionService;
import com.conta.comer.service.impl.PositionServiceImpl;
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
public class PositionDataTransferBizImpl extends AbstractDataTransferBizImpl<String>
{

    public static final String TAG = PaymentsDataTransferBizImpl.class.getSimpleName();

    private Context context;
    private PositionService positionService;
    private ResultObserver observer;
    private List<Position> positions;

    public PositionDataTransferBizImpl(Context context, ResultObserver resultObserver)
    {
        super(context);
        this.context = context;
        this.positionService = new PositionServiceImpl(context);
        this.observer = resultObserver;
        this.keyValueDao = new KeyValueDaoImpl(context);
    }

    @Override
    public void receiveData(String response)
    {
        Log.d(TAG, "Server Response:" + response);
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
                    long positionId = Long.parseLong(columns[0]);
                    long positionBackendId = Long.parseLong(columns[1]);
                    long updated = Long.parseLong(columns[2]);

                    Position position = positionService.getPositionById(positionId);
                    if (Empty.isNotEmpty(position) && updated == 1)
                    {
                        position.setStatus(SendStatus.SENT.getId().intValue());
                        position.setBackendId(positionBackendId);
                        position.setUpdateDateTime(DateUtil.convertDate(new Date(), DateUtil.FULL_FORMATTER_GREGORIAN_WITH_TIME, "EN"));
                        positionService.updatePosition(position);
                        success++;
                    } else
                    {
                        failure++;
                    }
                }
                if (Empty.isNotEmpty(getObserver()))
                {
                    getObserver().publishResult(String.format(context.getString(R.string.payments_data_transferred_successfully),
                            success, failure));
                }
            } catch (Exception ex)
            {
                if (Empty.isNotEmpty(getObserver()))
                {
                    getObserver().publishResult(context.getString(R.string.error_position_transfer));
                }
            }
        } else
        {
            if (Empty.isNotEmpty(getObserver()))
            {
                getObserver().publishResult(context.getString(R.string.message_got_no_response));
            }
        }
    }

    @Override
    public void beforeTransfer()
    {
        if (Empty.isNotEmpty(getObserver()))
        {
            getObserver().publishResult(context.getString(R.string.sending_positions_data));
        }
    }

    @Override
    public ResultObserver getObserver()
    {
        return observer;
    }

    @Override
    public String getMethod()
    {
        return "position/create";
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
        HttpEntity<List<Position>> requestEntity = new HttpEntity<>(positions, headers);
        return requestEntity;
    }

    public void setPositions(List<Position> positions)
    {
        this.positions = positions;
    }

    public void sendAllData()
    {
        prepare();
        getAllData(serverAddress1, serverAddress2, username, password, salesmanId);
    }
}
