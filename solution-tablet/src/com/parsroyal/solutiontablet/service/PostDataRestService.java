package com.parsroyal.solutiontablet.service;

import com.parsroyal.solutiontablet.data.model.PositionDto;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Created by Arash on 2017-02-16.
 */

public interface PostDataRestService {


  @POST("saleorders/cancel/{orderBackendId}")
  Call<String> cancelOrders(@Path("orderBackendId") Long orderBackendId, @Body Long rejectType);

  @POST("positions")
  Call<Response<Void>> sendPosition(@Body PositionDto positionDto);
}
