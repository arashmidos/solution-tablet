package com.parsroyal.storemanagement.service.impl;

import android.content.Context;
import android.util.Log;

import com.parsroyal.storemanagement.constants.StatusCodes;
import com.parsroyal.storemanagement.data.event.DataTransferErrorEvent;
import com.parsroyal.storemanagement.data.event.DataTransferSuccessEvent;
import com.parsroyal.storemanagement.data.event.MessageEvent;
import com.parsroyal.storemanagement.data.model.CreateMessageRequest;
import com.parsroyal.storemanagement.data.model.Message;
import com.parsroyal.storemanagement.service.MessageService;
import com.parsroyal.storemanagement.service.ServiceGenerator;
import com.parsroyal.storemanagement.util.Empty;
import com.parsroyal.storemanagement.util.NetworkUtil;
import com.parsroyal.storemanagement.util.PreferenceHelper;
import com.parsroyal.storemanagement.util.constants.ApplicationKeys;

import java.io.IOException;
import java.util.ArrayList;
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
      return;
    }

    MessageService messageService = ServiceGenerator.createService(MessageService.class);

    Call<List<Message>> call = messageService.getAllMessages();

    call.enqueue(new Callback<List<Message>>() {
      @Override
      public void onResponse(Call<List<Message>> call, Response<List<Message>> response) {
        if (response.isSuccessful()) {
          List<Message> messages = response.body();
          if (Empty.isNotEmpty(messages)) {
            EventBus.getDefault().post(new MessageEvent(messages, false));
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

  public void sendMessages(String message) {
    if (!NetworkUtil.isNetworkAvailable(context)) {
      EventBus.getDefault().post(new DataTransferErrorEvent(StatusCodes.NO_NETWORK));
      return;
    }

    MessageService messageService = ServiceGenerator.createService(MessageService.class);
    SettingServiceImpl settingService = new SettingServiceImpl();
    long sender = Long.parseLong(settingService.getSettingValue(ApplicationKeys.SALESMAN_ID));
    int pushType = 2;
    int receiverType =
        PreferenceHelper.isVisitor() ? 1 : (PreferenceHelper.isDistributor() ? 3 : 0);

    CreateMessageRequest request = new CreateMessageRequest(sender, receiverType, pushType, message,
        0);
    Call<Message> call = messageService.sendMessage(request);

    call.enqueue(new Callback<Message>() {
      @Override
      public void onResponse(Call<Message> call, Response<Message> response) {
        if (response.isSuccessful()) {
          List<Message> messages = new ArrayList<>();
          messages.add(response.body());
          EventBus.getDefault().post(new MessageEvent(messages, true));
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
      public void onFailure(Call<Message> call, Throwable t) {
        EventBus.getDefault().post(new DataTransferErrorEvent(StatusCodes.NETWORK_ERROR));
      }
    });
  }
}
