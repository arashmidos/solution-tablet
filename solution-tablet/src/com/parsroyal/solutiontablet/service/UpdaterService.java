package com.parsroyal.solutiontablet.service;

import com.parsroyal.solutiontablet.data.response.UpdateResponse;
import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by Arash on 2017-02-16.
 */

public interface UpdaterService {

  @GET("/pvstore/app/latest/solution-tablet")
  Call<UpdateResponse> getUpdate();
}
