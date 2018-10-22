package com.parsroyal.solutiontablet.service;

import com.parsroyal.solutiontablet.data.entity.Customer;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

/**
 * Created by shkbhbb on 10/22/18.
 */

public interface SearchGoodService {

  //TODO change address
  @GET("visit-lines/delivery/{distributor_id}")
  Call<List<Customer>> search(@Url String keyWord);
}
