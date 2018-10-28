package com.parsroyal.solutiontablet.service;

import com.parsroyal.solutiontablet.data.entity.Customer;
import java.util.List;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * Created by shkbhbb on 10/22/18.
 */

public interface SearchGoodService {

  @POST("customers/search")
  Call<List<Customer>> search(@Body RequestBody keyWord);
}
