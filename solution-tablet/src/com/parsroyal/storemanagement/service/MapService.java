package com.parsroyal.storemanagement.service;

import com.parsroyal.storemanagement.data.response.MapRoadResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.Url;

/**
 * Created by Arash on 2017-07-05.
 */

public interface MapService {

  @GET
  Call<MapRoadResponse> snapToRoads(@Url String url, @Query("path") String path,
      @Query("key") String apiKey);
}
