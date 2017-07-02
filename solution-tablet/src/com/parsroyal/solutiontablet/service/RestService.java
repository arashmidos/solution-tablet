package com.parsroyal.solutiontablet.service;

import com.parsroyal.solutiontablet.data.model.SaleOrderDocument;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Created by Arash on 2017-02-16.
 */

public interface RestService {

  @POST("saleorders")
  Call<String> sendOrder(@Body SaleOrderDocument saleDocument);
}
