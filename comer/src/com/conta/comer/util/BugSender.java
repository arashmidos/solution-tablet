package com.conta.comer.util;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.conta.comer.constants.Constants;
import com.conta.comer.data.dao.KeyValueDao;
import com.conta.comer.data.dao.impl.KeyValueDaoImpl;
import com.conta.comer.data.entity.KeyValue;
import com.conta.comer.exception.BackendIsNotReachableException;
import com.conta.comer.exception.InvalidServerAddressException;
import com.conta.comer.exception.PasswordNotProvidedForConnectingToServerException;
import com.conta.comer.exception.SalesmanIdNotProvidedForConnectingToServerException;
import com.conta.comer.exception.UsernameNotProvidedForConnectingToServerException;
import com.conta.comer.util.constants.ApplicationKeys;

import org.acra.ReportField;
import org.acra.collector.CrashReportData;
import org.acra.sender.ReportSender;
import org.acra.sender.ReportSenderException;
import org.springframework.http.HttpBasicAuthentication;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: m.sefidi
 * Date: 6/3/13
 * Time: 8:58 AM
 * To change this template use File | Settings | File Templates.
 */
public class BugSender implements ReportSender
{
    private static final String TAG = "BugSender";
    private Context context;

    private KeyValueDao keyValueDao;

    private KeyValue serverAddress1;
    private KeyValue serverAddress2;
    private KeyValue username;
    private KeyValue password;
    private KeyValue salesmanId;

    public void setContext(Context context)
    {
        this.context = context;
        this.keyValueDao = new KeyValueDaoImpl(context);

        serverAddress1 = keyValueDao.retrieveByKey(ApplicationKeys.SETTING_SERVER_ADDRESS_1);
        serverAddress2 = keyValueDao.retrieveByKey(ApplicationKeys.SETTING_SERVER_ADDRESS_2);
        username = keyValueDao.retrieveByKey(ApplicationKeys.SETTING_USERNAME);
        password = keyValueDao.retrieveByKey(ApplicationKeys.SETTING_PASSWORD);
        salesmanId = keyValueDao.retrieveByKey(ApplicationKeys.SALESMAN_ID);
    }

    private String createCrashLog(CrashReportData report)
    {
        Date now = new Date();
        StringBuilder log = new StringBuilder();
        log.append("Package: " + report.get(ReportField.PACKAGE_NAME) + "\n");
        log.append("Version: " + report.get(ReportField.APP_VERSION_CODE) + "\n");
        log.append("Android: " + report.get(ReportField.ANDROID_VERSION) + "\n");
        log.append("Manufacturer: " + android.os.Build.MANUFACTURER + "\n");
        log.append("Model: " + report.get(ReportField.PHONE_MODEL) + "\n");
        log.append("Date: " + now + "\n");
        log.append("Salesman: " + salesmanId.getValue());
        log.append("\n");
        log.append(report.get(ReportField.STACK_TRACE));

        return log.toString();
    }

    private String makeUrl(String serverAddress1, String serverAdress2, String method)
    {
        Log.i(TAG, "Trying to reach url with server addresses");
        if (NetworkUtil.isURLReachable(context, serverAddress1))
        {
            Log.i(TAG, "Server address 1 is available");
            return serverAddress1 + "/" + method;
        } else if (NetworkUtil.isURLReachable(context, serverAdress2))
        {
            Log.i(TAG, "Server address 2 is available");
            return serverAdress2 + "/" + method;
        } else
        {
            Log.i(TAG, "Both server addresses are not available");
            throw new BackendIsNotReachableException();
        }
    }

    @Override
    public void send(Context context, CrashReportData errorContent) throws ReportSenderException
    {
        try
        {

            if (Empty.isEmpty(serverAddress1) || Empty.isEmpty(serverAddress2))
            {
                throw new InvalidServerAddressException();
            }

            if (Empty.isEmpty(username))
            {
                throw new UsernameNotProvidedForConnectingToServerException();
            }

            if (Empty.isEmpty(password))
            {
                throw new PasswordNotProvidedForConnectingToServerException();
            }

            if (Empty.isEmpty(salesmanId))
            {
                throw new SalesmanIdNotProvidedForConnectingToServerException();
            }

            Log.e("BugSENDER", errorContent.get(ReportField.STACK_TRACE));

            String crashLog = createCrashLog(errorContent);

            saveLogToFile(crashLog);

            sendCrashLogToServer(crashLog);


        } catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    protected void sendCrashLogToServer(String crashLog)
    {
        String url = makeUrl(serverAddress1.getValue(), serverAddress2.getValue(), "report/crash");

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.TEXT_PLAIN);
        HttpBasicAuthentication authentication = new HttpBasicAuthentication(username.getValue(), password.getValue());
        httpHeaders.setAuthorization(authentication);
        if (Empty.isNotEmpty(salesmanId))
        {
            httpHeaders.add("salesmanId", salesmanId.getValue());
        }

        HttpEntity<String> httpEntity = new HttpEntity<String>(crashLog, httpHeaders);
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().add(new StringHttpMessageConverter());
        restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory());
        restTemplate.exchange(url, HttpMethod.POST, httpEntity, String.class);
    }

    private void saveLogToFile(String logContent)
    {
        try
        {
            FileWriter fileWritter = null;
            String filesDir = Environment.getExternalStorageDirectory().toString();

            File appDirectory = new File(filesDir + "/" + Constants.APPLICATION_NAME);
            if (!appDirectory.exists())
            {
                if (!appDirectory.mkdirs())
                {
                    return;
                }
                ;
            }

            File logDirectory = new File(appDirectory.getPath(), "logs");
            if (!logDirectory.exists())
            {
                if (!logDirectory.mkdirs())
                {
                    return;
                }
                ;
            }

            File logFile = new File(logDirectory.getPath(), "crashes-" + DateUtil.convertDate(new Date(), DateUtil.GLOBAL_FORMATTER_GREGORIAN, "EN") + ".txt");
            if (!logFile.exists())
            {
                if (!logFile.createNewFile())
                {
                    return;
                }
            }

            fileWritter = new FileWriter(logFile, true);
            BufferedWriter bufferWriter = new BufferedWriter(fileWritter);
            PrintWriter out = new PrintWriter(bufferWriter);
            out.println(logContent);
            bufferWriter.close();
            out.close();

        } catch (IOException e)
        {
            Log.e(TAG, e.getMessage(), e);
        }
    }

}
