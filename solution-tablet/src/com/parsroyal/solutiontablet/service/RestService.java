package com.parsroyal.solutiontablet.service;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.parsroyal.solutiontablet.data.model.SaleOrderDocument;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by Arash on 2017-02-16.
 */

public interface RestService {

  @POST("saleorders")
  Call<String> sendOrder(@Body SaleOrderDocument saleDocument);

  @GET("goods/{customerId}/reject")
  Call<JsonArray> getAllRejectedData(@Path("customerId") Long customerBackendId);
}
