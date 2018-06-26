package com.parsroyal.solutiontablet.service;

import com.google.gson.JsonArray;
import com.parsroyal.solutiontablet.data.model.SaleInvoiceDocument;
import com.parsroyal.solutiontablet.data.model.SaleOrderDocument;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Created by Arash on 2017-02-16.
 */

public interface RestService {

  @POST("saleorders")
  Call<String> sendOrder(@Body SaleOrderDocument saleDocument);

  @POST("saleinvoices")
  Call<String> sendInvoice(@Header("saleType") Long saleType,
      @Body SaleInvoiceDocument saleDocument);

  @GET("goods/{customerId}/reject")
  Call<JsonArray> getAllRejectedData(@Path("customerId") Long customerBackendId);
}
