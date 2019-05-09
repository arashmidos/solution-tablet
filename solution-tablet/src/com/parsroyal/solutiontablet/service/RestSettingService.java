package com.parsroyal.solutiontablet.service;

import com.parsroyal.solutiontablet.data.model.SettingDto;
import com.parsroyal.solutiontablet.data.response.CompanyInfoResponse;
import com.parsroyal.solutiontablet.data.response.SettingResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Url;

/**
 * Created by Arash on 2017-02-16.
 */

public interface RestSettingService {

  @GET
  Call<CompanyInfoResponse> getCompanyInfo(@Url String uri);

  @POST
  Call<SettingResponse> getCompanySetting(@Url String uri, @Body SettingDto settingDto);
}
