package com.parsroyal.solutiontablet.service;

import com.parsroyal.solutiontablet.data.model.DetectGoodDetail;
import com.parsroyal.solutiontablet.data.model.DetectGoodRequest;
import com.parsroyal.solutiontablet.data.model.Packer;
import com.parsroyal.solutiontablet.data.model.SelectOrderRequest;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Created by Arash on 2017-02-16.
 */

public interface StoreRestService {

  @POST("store/detectGoods")
  Call<List<DetectGoodDetail>> detectGood(@Body DetectGoodRequest goodRequest);

  @POST("store/packer/selectOrder")
  Call<Packer> selectOrder(@Body SelectOrderRequest request);

//
//  @POST("saleorders/free")
//  Call<String> sendFreeOrder(@Body SaleOrderDocument saleDocument);
//
//  @POST("users/updatePusheId")
//  Call<String> updatePusheId(@Body UpdatePusheRequest saleDocument);
//
//  @POST("saleinvoices")
//  Call<String> sendInvoice(@Header("saleType") Long saleType,
//      @Body SaleInvoiceDocument saleDocument);
//
//  @POST("visits")
//  Call<VisitInformationDto> sendVisits(@Body VisitInformationDto visitInformationDto);
//
//  @GET("goods/{customerId}/reject")
//  Call<JsonArray> getAllRejectedData(@Path("customerId") Long customerBackendId);
//
//  @Multipart
//  @POST("customers/images")
//  Call<String> upload(
//      @Part("backendId") RequestBody backendId,
//      @Part MultipartBody.Part file
//  );
}
