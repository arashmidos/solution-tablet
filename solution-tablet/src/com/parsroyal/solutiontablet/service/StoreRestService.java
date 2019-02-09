package com.parsroyal.solutiontablet.service;

import com.google.gson.JsonArray;
import com.parsroyal.solutiontablet.data.model.DetectGoodRequest;
import com.parsroyal.solutiontablet.data.model.GoodDetectDetail;
import com.parsroyal.solutiontablet.data.model.SaleInvoiceDocument;
import com.parsroyal.solutiontablet.data.model.SaleOrderDocument;
import com.parsroyal.solutiontablet.data.model.UpdatePusheRequest;
import com.parsroyal.solutiontablet.data.model.VisitInformationDto;
import java.util.List;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

/**
 * Created by Arash on 2017-02-16.
 */

public interface StoreRestService {

  @POST("store/detectGoods")
  Call<List<GoodDetectDetail>> detectGood(@Body DetectGoodRequest goodRequest);
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
