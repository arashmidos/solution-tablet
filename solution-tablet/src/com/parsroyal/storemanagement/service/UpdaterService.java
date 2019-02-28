package com.parsroyal.storemanagement.service;

import com.parsroyal.storemanagement.data.response.UpdateResponse;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

/**
 * Created by Arash on 2017-02-16.
 */

public interface UpdaterService {

  @GET
  Call<UpdateResponse> getUpdate(@Url String url);

  @GET("goods/images")
  Call<ResponseBody> downloadGoodsImages();
}
