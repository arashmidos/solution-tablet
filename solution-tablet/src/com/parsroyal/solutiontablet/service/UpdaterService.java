package com.parsroyal.solutiontablet.service;

import com.parsroyal.solutiontablet.data.response.UpdateResponse;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

/**
 * Created by Arash on 2017-02-16.
 */

public interface UpdaterService {

  @GET("/pvstore/app/latest/solution-tablet")
  Call<UpdateResponse> getUpdate();


  @GET/*("/tabletbackend/goods/images")*/
  Call<ResponseBody> downloadGoodsImages(@Url String url);
}
