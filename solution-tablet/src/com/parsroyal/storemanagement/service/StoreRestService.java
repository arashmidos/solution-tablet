package com.parsroyal.storemanagement.service;

import com.parsroyal.storemanagement.data.model.DetectGoodDetail;
import com.parsroyal.storemanagement.data.model.DetectGoodRequest;
import com.parsroyal.storemanagement.data.model.Packer;
import com.parsroyal.storemanagement.data.model.SelectOrderRequest;
import com.parsroyal.storemanagement.data.model.StockGood;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by Arash on 2017-02-16.
 */

public interface StoreRestService {

  @POST("store/detectGoods")
  Call<List<DetectGoodDetail>> detectGood(@Body DetectGoodRequest goodRequest);

  @POST("store/packer/selectOrder")
  Call<Packer> selectOrder(@Body SelectOrderRequest request);

  @GET("store/warehouse/checkStockStatus")
  Call<String> checkStockStatus(@Query("asn") Long asn);

  @GET("store/warehouse/getStockGoods")
  Call<List<StockGood>> getStockGoods(@Query("asn") Long asn);

}
