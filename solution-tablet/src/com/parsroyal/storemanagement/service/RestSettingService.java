package com.parsroyal.storemanagement.service;

import com.parsroyal.storemanagement.data.model.SettingDto;
import com.parsroyal.storemanagement.data.response.CompanyInfoResponse;
import com.parsroyal.storemanagement.data.response.SettingResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
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
