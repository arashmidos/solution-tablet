package com.parsroyal.solutiontablet.service;

import com.parsroyal.solutiontablet.data.model.Message;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;


public interface MessageService {

  @GET("notification/all")
  Call<List<Message>> getAllMessages();
}
