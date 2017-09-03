package com.parsroyal.solutiontablet.util;

import android.util.Log;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

/**
 * Created by Arashmidos on 2016-08-23.
 */
public class LoggingRequestInterceptor implements ClientHttpRequestInterceptor {

  private String TAG = LoggingRequestInterceptor.class.getName();
  final static Logger logger = LoggerFactory.getLogger(LoggingRequestInterceptor.class);

  @Override
  public ClientHttpResponse intercept(HttpRequest request, byte[] body,
      ClientHttpRequestExecution execution) throws IOException {

    traceRequest(request, body);
    ClientHttpResponse response = execution.execute(request, body);
    traceResponse(response);
    return response;
  }

  private void traceRequest(HttpRequest request, byte[] body) throws IOException {
    Log.i(TAG,
        "===========================request begin================================================");

    Log.i(TAG,"URI : " + request.getURI());
    Log.i(TAG,"Method : " + request.getMethod());
    Log.i(TAG,"Request Body : " + new String(body, "UTF-8"));

    Log.i(TAG,
        "==========================request end================================================");
  }

  private void traceResponse(ClientHttpResponse response) throws IOException {
    StringBuilder inputStringBuilder = new StringBuilder();
    BufferedReader bufferedReader = new BufferedReader(
        new InputStreamReader(response.getBody(), "UTF-8"));
    String line = bufferedReader.readLine();
    while (line != null) {
      inputStringBuilder.append(line);
      inputStringBuilder.append('\n');
      line = bufferedReader.readLine();
    }
    Log.i(TAG,
        "============================response begin==========================================");
    Log.i(TAG,"status code: " + response.getStatusCode());
    Log.i(TAG,"status text: " + response.getStatusText());
    Log.i(TAG,"Response Body : " + inputStringBuilder.toString());
    Log.i(TAG,
        "=======================response end=================================================");
  }
}
