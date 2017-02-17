package com.conta.comer.service;

import com.conta.comer.data.response.UpdateResponse;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by Arash on 2017-02-16.
 */

public interface UpdaterService
{
    @GET("/pvstore/app/latest/solution-tablet")
    Call<UpdateResponse> getUpdate();
}
