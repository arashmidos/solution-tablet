package com.parsroyal.storemanagement.service;

import com.parsroyal.storemanagement.data.entity.Customer;
import java.util.List;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Created by shkbhbb on 10/22/18.
 */

public interface SearchGoodService {

  @POST("customers/search")
  Call<List<Customer>> search(@Body RequestBody keyWord);
}
