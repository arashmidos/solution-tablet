package com.conta.comer.biz.impl;

import android.content.Context;
import android.util.Log;

import com.conta.comer.R;
import com.conta.comer.biz.AbstractDataTransferBizImpl;
import com.conta.comer.data.dao.QAnswerDao;
import com.conta.comer.data.dao.impl.QAnswerDaoImpl;
import com.conta.comer.data.entity.QAnswer;
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
 * Created by Mahyar on 8/4/2015.
 */
public class QAnswersDataTransferBizImpl extends AbstractDataTransferBizImpl<String>
{

    private ResultObserver resultObserver;
    private QAnswerDao qAnswerDao;

    public QAnswersDataTransferBizImpl(Context context, ResultObserver resultObserver)
    {
        super(context);
        this.resultObserver = resultObserver;
        this.qAnswerDao = new QAnswerDaoImpl(context);
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
                    Long answerId = Long.parseLong(columns[0]);
                    Long backendId = Long.parseLong(columns[1]);

                    QAnswer qAnswer = qAnswerDao.retrieve(answerId);

                    if (Empty.isNotEmpty(qAnswer))
                    {
                        qAnswer.setBackendId(backendId);
                        qAnswer.setUpdateDateTime(DateUtil.getCurrentGregorianFullWithTimeDate());
                        qAnswerDao.update(qAnswer);
                    }
                }

                resultObserver.publishResult(context.getString(R.string.answers_data_transferred_successfully));
            } catch (Exception ex)
            {
                Log.e(TAG, ex.getMessage(), ex);
                resultObserver.publishResult(context.getString(R.string.message_exception_in_sending_answers));
            }
        }
    }

    @Override
    public void beforeTransfer()
    {
        resultObserver.publishResult(context.getString(R.string.sending_answers_information_data));
    }

    @Override
    public ResultObserver getObserver()
    {
        return resultObserver;
    }

    @Override
    public String getMethod()
    {
        return "questionnaire/answers";
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
        List<QAnswer> allAnswersForSend = qAnswerDao.getAllQAnswersForSend();
        String qAnswerStr = getAnswersString(allAnswersForSend);
        headers.setAccept(Arrays.asList(MediaType.TEXT_PLAIN));
        HttpEntity<String> httpEntity = new HttpEntity<String>(qAnswerStr, headers);
        return httpEntity;
    }

    private String getAnswersString(List<QAnswer> allAnswersForSend)
    {
        StringBuilder sb = new StringBuilder();
        boolean firstLine = true;
        for (QAnswer qAnswer : allAnswersForSend)
        {
            String qAnswerStr = QAnswer.createString(qAnswer);
            qAnswerStr = qAnswerStr.trim().replaceAll("[\n]", "");
            qAnswerStr = qAnswerStr.trim().replace("\n", "");
            if (firstLine)
            {
                sb.append(qAnswerStr);
                firstLine = false;
                continue;
            }
            sb.append("\n");
            sb.append(qAnswerStr);
        }

        return sb.toString();
    }
}
