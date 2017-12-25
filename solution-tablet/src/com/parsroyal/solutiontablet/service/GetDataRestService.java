package com.parsroyal.solutiontablet.service;

import com.google.gson.JsonArray;
import com.parsroyal.solutiontablet.data.model.ProvinceList;
import com.parsroyal.solutiontablet.data.model.SaleOrderDocument;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Created by Arash on 2017-02-16.
 */

public interface GetDataRestService {

  @GET("baseinfo/provinces")
  Call<ProvinceList> getAllProvinces();
}
