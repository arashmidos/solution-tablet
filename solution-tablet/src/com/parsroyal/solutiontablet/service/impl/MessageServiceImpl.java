package com.parsroyal.solutiontablet.service.impl;

import android.content.Context;
import android.util.Log;
import com.parsroyal.solutiontablet.constants.StatusCodes;
import com.parsroyal.solutiontablet.data.event.DataTransferErrorEvent;
import com.parsroyal.solutiontablet.data.event.DataTransferSuccessEvent;
import com.parsroyal.solutiontablet.data.event.MessageEvent;
import com.parsroyal.solutiontablet.data.model.Message;
import com.parsroyal.solutiontablet.service.MessageService;
import com.parsroyal.solutiontablet.service.ServiceGenerator;
import com.parsroyal.solutiontablet.util.Empty;
import com.parsroyal.solutiontablet.util.NetworkUtil;
import java.io.IOException;
import java.util.List;
import org.greenrobot.eventbus.EventBus;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MessageServiceImpl {

  private Context context;

  public MessageServiceImpl(Context context) {
    this.context = context;
  }

  public void getAllMessages() {
    if (!NetworkUtil.isNetworkAvailable(context)) {
      EventBus.getDefault().post(new DataTransferErrorEvent(StatusCodes.NO_NETWORK));
    }

    MessageService messageService = ServiceGenerator.createService(MessageService.class);

    Call<List<Message>> call = messageService.getAllMessages();

    call.enqueue(new Callback<List<Message>>() {
      @Override
      public void onResponse(Call<List<Message>> call, Response<List<Message>> response) {
        if (response.isSuccessful()) {
          List<Message> messages = response.body();
          if (Empty.isNotEmpty(messages)) {
            EventBus.getDefault().post(new MessageEvent(messages));
          } else {
            EventBus.getDefault().post(new DataTransferSuccessEvent(StatusCodes.NO_DATA_ERROR));
          }
        } else {
          try {
            Log.d("TAG", response.errorBody().string());
          } catch (IOException e) {
            e.printStackTrace();
          }
          EventBus.getDefault().post(new DataTransferErrorEvent(StatusCodes.SERVER_ERROR));
        }
      }

      @Override
      public void onFailure(Call<List<Message>> call, Throwable t) {
        EventBus.getDefault().post(new DataTransferErrorEvent(StatusCodes.NETWORK_ERROR));
      }
    });
  }
}
