package com.parsroyal.solutiontablet.service;

import com.parsroyal.solutiontablet.data.model.BaseInfoList;
import com.parsroyal.solutiontablet.data.model.CityList;
import com.parsroyal.solutiontablet.data.model.ProvinceList;
import com.parsroyal.solutiontablet.data.model.QuestionnaireDtoList;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by Arash on 2017-02-16.
 */

public interface GetDataRestService {

  @GET("baseinfo/provinces")
  Call<ProvinceList> getAllProvinces();

  @GET("baseinfo/cities")
  Call<CityList> getAllCities();

  @GET("baseinfo/all")
  Call<BaseInfoList> getAllBaseInfo();

  @GET("goods/groups/{salesmanId}")
  Call<String> getAllGoodGroups(@Path("salesmanId") String salesmanId);

  @GET("questionnaire/{today}")
  Call<QuestionnaireDtoList> getAllQuestionnaire(@Path("today") String today);

}
